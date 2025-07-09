package com.virhuiai.Cli;

import com.virhuiai.log.logext.LogFactory;
import org.apache.commons.logging.Log;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;

/**
 * 使用枚举方式实现的单例模式来管理命令行选项解析。
 * 枚举单例模式保证了线程安全和懒加载。
 */
public enum OptionsSingleton {
    INSTANCE;

    //    private static final Log LOGGER = CshLogUtils.createLogExtended(OptionsSingleton.class); // 日志记录器
    private static final Log LOGGER = LogFactory.getLog();

    // 在单例枚举实例的属性初始化时，直接在属性声明时进行初始化
    private final Options options = new Options();
    private final CommandLineParser parser = new DefaultParser(); // 创建默认的命令行解析器
    ////////////////////////////////////////////////////////////////////////////////
    private volatile CommandLine cmd; // 存储解析后的命令行参数 声明为volatile，确保修改对其他线程立即可见


    private volatile boolean  parsed; // 标记是否已经解析过命令行参数  声明为volatile，确保修改对其他线程立即可见


    /**
     * 私有构造函数，用于初始化选项
     */
    OptionsSingleton() {
        // 在这里添加命令行选项
        // 例如:
        options.addOption("h", "help", false, "显示帮助信息");
    }

    /**
     * 获取命令行选项。
     *
     * @return 命令行选项
     */
    public Options getOptions() {
        return options;
    }

    /**
     * 解析命令行参数。
     * 使用双重检查锁定模式确保线程安全
     *
     双重检查工作流程：

     1. **第一次检查** (!parsed)
     - 在同步块之外快速检查是否已解析
     - 如果已经解析过（parsed=true），直接返回，避免获取锁的开销
     - 这是一个性能优化，避免每次调用都需要获取锁
     2. **加锁** (synchronized)
     - 只有第一次检查发现未解析时才会执行
     - 确保多线程环境下只有一个线程能进入关键区域
     - 防止多个线程同时进行解析
     3. **第二次检查** (!parsed)
     - 防止多个线程在第一次检查后但在获取锁之前进入
     - 确保即使多个线程通过了第一次检查，也只有第一个获得锁的线程会执行解析

     场景分析：
			// 假设有两个线程 A 和 B 同时调用 parseCmd
			
			// 线程A                        // 线程B
			if (!parsed) {                 if (!parsed) {
			    // A看到parsed=false         // B也看到parsed=false
			    
			    // A获得锁                   // B等待锁
			    synchronized (this) {
			        if (!parsed) {         
			            // A执行解析
			            cmd = parser.parse();
			            parsed = true;
			        }
			    }                         // B获得锁
			                             synchronized (this) {
			                                 if (!parsed) {
			                                     // B看到parsed=true
			                                     // 不会重复解析
			                                 }
			                             }
			}

     *
     * @param args 命令行参数数组
     * @return 解析后的CommandLine对象
     * @throws ParseException 如果解析失败
     */
    public CommandLine parseCmd(String[] args) throws ParseException {
        if (!parsed) { // 第一次检查
            synchronized (this) {// 同步块
                if (!parsed) {// 第二次检查
                    try {
                        cmd = parser.parse(options, args);
                        parsed = true;
                    } catch (ParseException e) {
                        LOGGER.error("命令行参数解析失败: ", e);
                        throw e;
                    }
                }
            }
        }
        return cmd;
    }

    public boolean isParsed() {
        return parsed;
    }

    /**
     * 获取解析后的命令行对象。
     *
     * @return 解析后的CommandLine对象
     * @throws IllegalStateException 如果未执行解析
     */
    public CommandLine getCmd() {
        if (!parsed || null == cmd) {
            throw new IllegalStateException("命令行参数尚未解析，请先调用 parseCmd 方法");
        }
        return cmd;
    }

//    /**
//     * 重置命令行解析结果。
//     * 在需要重新解析命令行参数时调用此方法。
//     */
//    public void reset() {
//        cmd = null;
//    }
//
//    /**
//     * 添加命令行选项。
//     *
//     * @param opt         短选项名
//     * @param longOpt     长选项名
//     * @param hasArg      是否有参数
//     * @param description 选项描述
//     */
//    public void addOption(String opt, String longOpt, boolean hasArg, String description) {
//        options.addOption(opt, longOpt, hasArg, description);
//    }

    /**
     * 打印帮助信息。
     */
    public void printHelp(String cmdLineSyntax) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(cmdLineSyntax, options);
    }
}