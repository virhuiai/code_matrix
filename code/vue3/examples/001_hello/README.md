# 外面下载js
```html
curl -L -o vue.esm-browser@3.5.22.js \
  https://cdn.jsdelivr.net/npm/vue@3.5.22/dist/vue.esm-browser.js
```

# 链接文件

```shell
ln -s ../../vue.esm-browser@3.5.22.js ./vue@3.5.22.js
```

# 静态服务器

```shell
python3 -m http.server
```

```
http://127.0.0.1:8000/vue@3.5.22.js
```

# hello.html

```html

```

# 访问

```url
http://127.0.0.1:8000/001_hello.html
```