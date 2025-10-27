#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <map>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <cstring>

std::map<std::string, std::string> mimeTypes = {
    {".html", "text/html"},
    {".css", "text/css"},
    {".js", "application/javascript"},
    {".jpg", "image/jpeg"},
    {".png", "image/png"},
    {".gif", "image/gif"}
};

std::string getContentType(const std::string& path) {
    size_t pos = path.find_last_of('.');
    if (pos != std::string::npos) {
        std::string ext = path.substr(pos);
        if (mimeTypes.count(ext)) return mimeTypes[ext];
    }
    return "text/plain";
}

std::string readFile(const std::string& path) {
    std::ifstream file(path, std::ios::binary);
    if (!file) return "";
    std::stringstream buffer;
    buffer << file.rdbuf();
    return buffer.str();
}

int main() {
    int server_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (server_fd < 0) {
        std::cerr << "创建socket失败\n";
        return 1;
    }

    int opt = 1;
    setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));

    sockaddr_in address;
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = INADDR_ANY;
    address.sin_port = htons(8080);

    if (bind(server_fd, (sockaddr*)&address, sizeof(address)) < 0) {
        std::cerr << "绑定失败\n";
        return 1;
    }

    if (listen(server_fd, 10) < 0) {
        std::cerr << "监听失败\n";
        return 1;
    }

    std::cout << "服务器运行在 http://localhost:8080\n";

    while (true) {
        int client_fd = accept(server_fd, nullptr, nullptr);
        if (client_fd < 0) continue;

        char buffer[4096] = {0};
        read(client_fd, buffer, 4096);

        std::string request(buffer);
        size_t start = request.find("GET ") + 4;
        size_t end = request.find(" ", start);
        std::string path = request.substr(start, end - start);

        if (path == "/") path = "/index.html";
        path = "." + path;

        std::string content = readFile(path);
        std::string response;

        if (!content.empty()) {
            response = "HTTP/1.1 200 OK\r\n";
            response += "Content-Type: " + getContentType(path) + "\r\n";
            response += "Content-Length: " + std::to_string(content.size()) + "\r\n";
            response += "\r\n" + content;
        } else {
            response = "HTTP/1.1 404 Not Found\r\n\r\n<h1>404 Not Found</h1>";
        }

        write(client_fd, response.c_str(), response.size());
        close(client_fd);
    }

    close(server_fd);
    return 0;
}