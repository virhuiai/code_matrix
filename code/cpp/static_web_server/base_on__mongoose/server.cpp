// server.cpp
// Copyright (c) 2022 Cesanta Software Limited
// All rights reserved

#include "mongoose.h"
#include <iostream>

const char *s_listening_url = "http://0.0.0.0:8000";

// HTTP request handler
void fn(struct mg_connection *c, int ev, void *ev_data) {
  if (ev == MG_EV_HTTP_MSG) {
    struct mg_http_message *hm = (struct mg_http_message *) ev_data;

    // 仅处理 GET 请求
    if (mg_strcmp(hm->method, mg_str("GET")) == 0) {
      struct mg_http_serve_opts opts = {};
      opts.root_dir = ".";  // 当前目录
      opts.mime_types = "html=text/html,css=text/css,js=application/javascript";
      mg_http_serve_dir(c, hm, &opts);
    } else {
      mg_http_reply(c, 405, "", "Method Not Allowed\n");
    }
  }
}

int main(void) {
  struct mg_mgr mgr;
  mg_mgr_init(&mgr);

  std::cout << "Server running at " << s_listening_url << std::endl;
  std::cout << "Put your files in the current directory.\n";

  if (mg_http_listen(&mgr, s_listening_url, fn, nullptr) == nullptr) {
    std::cerr << "Failed to listen on " << s_listening_url << std::endl;
    return 1;
  }

  for (;;) mg_mgr_poll(&mgr, 1000);
  mg_mgr_free(&mgr);
  return 0;
}
// /Volumes/THAWSPACE/Soft.Ok/cosmocc-4.0.2\[用于编译\]/bin/cosmoc++ server.cpp mongoose.c -o webserver