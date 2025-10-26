// 1. 包含你下载的库文件
#include "httplib.h"
#include <iostream>
// /Volumes/THAWSPACE/Soft.Ok/cosmocc-4.0.2\[用于编译\]/bin/cosmoc++  -o server  server.cpp
int main(void)
{
    // 2. 创建 Server 实例
    httplib::Server svr;

    // 3. 设置静态文件目录
    // 这行代码告诉服务器：当浏览器请求文件时，
    // 去 "./www" 目录里查找。
    // 例如：请求 "/" 时，它会查找 "./www/index.html"
    //       请求 "/script.js" 时，它会查找 "./www/script.js"
    const char *base_dir = "./www";
    bool ret = svr.set_base_dir(base_dir);

    if (!ret) {
        std::cerr << "错误：目录 " << base_dir << " 不存在或无法访问。" << std::endl;
        return 1;
    }

    // 4. 启动服务器
    // 监听 0.0.0.0 (所有网络接口) 的 8080 端口
    int port = 8080;
    std::cout << "C++ 静态 Web 服务器已启动，访问 http://localhost:" << port << std::endl;

    svr.listen("0.0.0.0", port);

    return 0;
}