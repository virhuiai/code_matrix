//  /Volumes/THAWSPACE/Soft.Ok/cosmocc-4.0.2\[用于编译\]/bin/cosmocc -o hello hello.c
//  /Volumes/THAWSPACE/Soft.Ok/cosmocc-4.0.2\[用于编译\]/bin/cosmoc++  -o hello  hello.cc
//  hello.cc
//
//  Copyright (c) 2019 Yuji Hirose. All rights reserved.
//  MIT License
//

// #include <httplib.h>
#include "httplib.h"//注意用双引号本地包含，而不是尖括号系统包含
using namespace httplib;

int main(void) {
  Server svr;

  svr.Get("/hi", [](const Request & /*req*/, Response &res) {
    res.set_content("Hello World!", "text/plain");
  });

  svr.listen("0.0.0.0", 8080);
}
