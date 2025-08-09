#include <iostream>
// 引入必要的头文件
#include <cstdint>   // 标准整数类型

#include <unistd.h>   // 系统调用
#include <sys/mount.h> // 挂载相关
#include <sys/types.h> // 系统类型
#include <fcntl.h>    // 文件控制
using namespace std;

// 参考: http://www.sans.org/reading-room/whitepapers/forensics/reverse-engineering-microsoft-exfat-file-system-33274

// 定义exFAT卷引导记录(VBR)结构体
// __attribute__((__packed__)) 确保结构体紧凑排列，不进行字节对齐
struct __attribute__((__packed__)) exfat_vbr
{
  uint8_t jump_boot[3];              // 跳转指令
  uint8_t file_system_name[8];       // 文件系统名称，通常为"EXFAT   "
  uint8_t zero[53];                  // 保留字段，填充为0
  uint64_t partition_offset;         // 分区偏移量（扇区）
  uint64_t volume_length;            // 卷长度（扇区）
  uint32_t fat_offset;               // FAT表偏移量（扇区）
  uint32_t fat_length;               // FAT表长度（扇区）
  uint32_t cluster_heap_offset;      // 簇堆偏移量（扇区）
  uint32_t cluster_count;            // 簇数量
  uint32_t root_dir_first_cluster;   // 根目录第一个簇
  uint32_t volume_serial_number;     // 卷序列号
  struct
  {
    uint8_t minor;                   // 次要版本号
    uint8_t major;                   // 主要版本号
  } file_system_revision;            // 文件系统版本
  struct
  {
    uint16_t active_fat:1;           // 活动FAT表(0或1)
    uint16_t volume_dirty:1;         // 卷脏标记(0=干净,1=脏)
    uint16_t media_failure:1;        // 介质失败标记
    uint16_t zero:1;                 // 保留位，必须为0
    uint16_t reserved:12;            // 保留位
  } volume_flags;                    // 卷标志
  uint8_t bytes_per_sector;          // 每扇区字节数的指数值(2^n)
  uint8_t sector_per_cluster;        // 每簇扇区数的指数值(2^n)
  uint8_t fats_count;                // FAT表数量
  uint8_t drive_select;              // 驱动器选择
  uint8_t percent_in_use;            // 已使用百分比
  uint8_t reserved[7];               // 保留字段
  uint8_t boot_code[390];            // 引导代码
  uint16_t boot_signature;           // 引导签名(0xAA55)
};

// 静态断言确保exfat_vbr结构体大小为512字节
static_assert(sizeof(exfat_vbr) == 512, "exfat_vbr is not packed");

// 打印VBR信息函数
// 该函数详细输出exFAT卷引导记录的各个字段值，便于调试和分析
void print_vbr(exfat_vbr& vbr) {
// 打印跳转指令（以十六进制形式）
cout << "jump_boot[3]: 0x" << hex << (vbr.jump_boot[2] | (vbr.jump_boot[1] << 8) | (vbr.jump_boot[0] << 16)) << dec << endl 
// 打印文件系统名称（通常为"EXFAT   "）
<< "file_system_name[8]: " << (char*)vbr.file_system_name << endl 
// 打印保留字段（应为0）
<< "zero[53]: " << (int)vbr.zero[0] << endl 
// 打印分区偏移量（扇区）
<< "partition_offset: " << vbr.partition_offset << endl 
// 打印卷长度（扇区）
<< "volume_length: " << vbr.volume_length << endl 
// 打印FAT表偏移量（扇区）
<< "fat_offset: " << vbr.fat_offset << endl 
// 打印FAT表长度（扇区）
<< "fat_length: " << vbr.fat_length << endl 
// 打印簇堆偏移量（扇区）
<< "cluster_heap_offset: " << vbr.cluster_heap_offset << endl 
// 打印簇数量
<< "cluster_count: " << vbr.cluster_count << endl 
// 打印根目录第一个簇
<< "root_dir_first: " << vbr.root_dir_first_cluster << endl 
// 打印卷序列号
<< "volume_serial_number: " << vbr.volume_serial_number << endl 
// 打印文件系统版本
<< "file_system_revision: " << endl
<< "  major: " << (int)vbr.file_system_revision.major << endl
<< "  minor: " << (int)vbr.file_system_revision.minor << endl
// 打印卷标志
<< "volume_flags: " << endl
<< "  active_fat: " << vbr.volume_flags.active_fat << endl  // 活动FAT表(0或1)
<< "  volume_dirty: " << vbr.volume_flags.volume_dirty << endl  // 卷脏标记(0=干净,1=脏)
<< "  media_failure: " << vbr.volume_flags.media_failure << endl  // 介质失败标记
<< "  zero: " << vbr.volume_flags.zero << endl  // 保留位，必须为0
<< "  reserved: " << vbr.volume_flags.reserved << endl  // 保留位
// 打印每扇区字节数（2的幂次）
<< "bytes_per_sector: 2^" << (int)vbr.bytes_per_sector << endl 
// 打印每簇扇区数（2的幂次）
<< "sector_per_cluster: 2^" << (int)vbr.sector_per_cluster << endl 
// 打印FAT表数量
<< "fats_count: " << (int)vbr.fats_count << endl 
// 打印驱动器选择
<< "drive_select: " << (int)vbr.drive_select << endl 
// 打印已使用百分比
<< "percent_in_use: " << (int)vbr.percent_in_use << endl 
// 打印保留字段
<< "reserved[7]: " << (int)vbr.reserved[0] << endl 
// 打印引导代码的第一个字节
<< "boot_code[390]: " << vbr.boot_code[0] << endl 
// 打印引导签名（应为0xAA55）
<< "boot_signature: 0x" << hex << vbr.boot_signature << dec << endl
<< endl;
}

// 计算校验和的宏定义
#define ADDSUM(sum, byte) ((sum << 31) | (sum >> 1)) + byte

// 验证VBR校验和
bool verify_vbr(exfat_vbr& vbr, int fd)
{
  size_t sector_size = 1 << vbr.bytes_per_sector; // 计算扇区大小
  uint8_t* sector_buffer = new uint8_t[sector_size]; // 分配扇区缓冲区

  uint32_t checksum = 0; // 初始化校验和
  // 读取前11个扇区计算校验和
  for (int sector = 0; sector < 11; sector++)
  {
    pread(fd, sector_buffer, sector_size, sector * sector_size);
    for (int i = 0; i < sector_size; i++)
    {
      // 跳过vbr中的volume_flags和percent_in_use字段
      if (sector || (i != 0x6a && i != 0x6b && i != 0x70)) {
        checksum = ADDSUM(checksum, sector_buffer[i]);
      }
    }
  }
  
  // 读取第12个扇区(校验和扇区)
  pread(fd, sector_buffer, sector_size, 11 * sector_size);
  // 验证校验和
  for (int i = 0; i < sector_size / sizeof(uint32_t); i++)
  {
    if (((uint32_t*)sector_buffer)[i] != checksum)
    {
      cerr << "invalid checksum 0x" << hex << checksum << " (expected 0x" << ((uint32_t*)sector_buffer)[i] << ")" << endl;
      return false;
    }
  }
  return true;
}

// 打印使用帮助信息
void usage()
{
  cerr << "USAGE: exfat_clean [-yvh] rdisk" << endl
    << endl
    << "OPTIONS: " << endl
    << "  -y     假设yes，不提示确认" << endl
    << "  -v     显示版本信息" << endl
    << "  -h     显示帮助信息" << endl
    << "  rdisk  原始exfat磁盘设备" << endl
    << endl;
  cerr << "exfat_clean v1.0.0" << endl
    << "源码: https://github.com/zzh8829/exfat_clean" << endl
    << "许可证: MIT 2017 Zihao Zhang" << endl;
}

// 主函数
int main(int argc, char* argv[])
{
  int ch;         // 命令行选项
  int yes = 0;    // 是否自动确认

  // 解析命令行选项
  while ((ch = getopt(argc, argv, "yh")) != EOF)
  {
    switch (ch)
    {
    case 'y':
      yes++;
    case 'h':
    default:
      usage();
      return 0;
      break;
    }
  }

  // 调整参数指针
  argc -= optind;
  argv += optind;

  // 检查是否提供了磁盘设备参数
  if (argc != 1) {
    cerr << "错误: 缺少磁盘设备参数!" << endl;
    usage();
    return 1;
  }

  // 打开磁盘设备
  int fd = open(argv[0], O_RDWR);

  exfat_vbr vbr; // 定义VBR结构体变量

  // 读取VBR
  if (pread(fd, &vbr, sizeof(vbr), 0) < 0) {
    cerr << "VBR读取失败" << endl;
    return 1;
  }

  // 打印VBR信息
  print_vbr(vbr);

  // 验证VBR校验和
  if(!verify_vbr(vbr, fd)) {
    cerr << "校验和失败" << endl;
    return 1;
  } else {
    cout << "校验和正常" << endl;
  }

  // 检查卷是否已经干净
  if(!vbr.volume_flags.volume_dirty) {
    cout << "exFAT卷已经干净" << endl;
    return 0;
  } else {
    cout << "exFAT卷处于脏状态" << endl;
  }

  // 如果没有使用-y选项，提示用户确认
  if(!yes) {
    string ans;
    cout << "确定要清除脏标记吗?" << endl
      << "数据丢失概不负责!!! [y/n]" << endl;
    cin >> ans;
    if(ans != "y" && ans != "Y") {
      cout << "未执行任何操作" << endl;
      close(fd);
      return 0;
    }      
  }

  // 清除脏标记
  cout << "清除脏标记" << endl;
  vbr.volume_flags.volume_dirty = 0;

  // 写回VBR
  if(pwrite(fd, &vbr, sizeof(vbr), 0) < 0) {
    cerr << "写入失败" << endl;
    return 1;
  }

  // 刷新缓冲区
  if(fsync(fd)) {
    cerr << "同步失败" << endl;
    return 1;
  }

  cout << "操作完成" << endl;
  
  // 关闭文件描述符
  close(fd);
  return 0;
}
