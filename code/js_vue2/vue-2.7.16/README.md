<p align="center"><a href="https://vuejs.org" target="_blank" rel="noopener noreferrer"><img width="100" src="https://vuejs.org/images/logo.png" alt="Vue logo"></a></p>

<p align="center">
  <a href="https://circleci.com/gh/vuejs/vue/tree/dev"><img src="https://img.shields.io/circleci/project/github/vuejs/vue/dev.svg?sanitize=true" alt="Build Status"></a>
  <a href="https://codecov.io/github/vuejs/vue?branch=dev"><img src="https://img.shields.io/codecov/c/github/vuejs/vue/dev.svg?sanitize=true" alt="Coverage Status"></a>
  <a href="https://npmcharts.com/compare/vue?minimal=true"><img src="https://img.shields.io/npm/dm/vue.svg?sanitize=true" alt="Downloads"></a>
  <a href="https://www.npmjs.com/package/vue"><img src="https://img.shields.io/npm/v/vue.svg?sanitize=true" alt="Version"></a>
  <a href="https://www.npmjs.com/package/vue"><img src="https://img.shields.io/npm/l/vue.svg?sanitize=true" alt="License"></a>
  <a href="https://chat.vuejs.org/"><img src="https://img.shields.io/badge/chat-on%20discord-7289da.svg?sanitize=true" alt="Chat"></a>
</p>

## This repo is for Vue 2
## 这个仓库是 Vue 2 的代码仓库

You are looking at the repository for Vue 2. The repo for Vue 3 is [vuejs/core](https://github.com/vuejs/core).

你现在看到的是 Vue 2 的代码仓库。Vue 3 的仓库在 [vuejs/core](https://github.com/vuejs/core)。

## Sponsors
## 赞助商

Vue.js is an MIT-licensed open source project with its ongoing development made possible entirely by the support of these awesome [backers](https://github.com/vuejs/core/blob/main/BACKERS.md). If you'd like to join them, please consider [ sponsor Vue's development](https://vuejs.org/sponsor/).

Vue.js 是一个采用 MIT 许可的开源项目，它的持续开发完全得益于这些优秀的[支持者](https://github.com/vuejs/core/blob/main/BACKERS.md)的支持。如果你想加入他们，请考虑[赞助 Vue 的开发](https://vuejs.org/sponsor/)。

<p align="center">
  <h3 align="center">Special Sponsor</h3>
</p>

<p align="center">
  <a target="_blank" href="https://github.com/appwrite/appwrite">
  <img alt="special sponsor appwrite" src="https://sponsors.vuejs.org/images/appwrite.svg" width="300">
  </a>
</p>

<p align="center">
  <a target="_blank" href="https://vuejs.org/sponsor/">
    <img alt="sponsors" src="https://sponsors.vuejs.org/sponsors.svg?v3">
  </a>
</p>

---

## Introduction
## 介绍

Vue (pronounced `/vjuː/`, like view) is a **progressive framework** for building user interfaces. It is designed from the ground up to be incrementally adoptable, and can easily scale between a library and a framework depending on different use cases. It consists of an approachable core library that focuses on the view layer only, and an ecosystem of supporting libraries that helps you tackle complexity in large Single-Page Applications.

Vue (发音为 `/vjuː/`，类似于 view) 是一个用于构建用户界面的**渐进式框架**。它从底层设计就支持渐进式引入，可以根据不同使用场景轻松地在库和框架之间进行扩展。它由一个专注于视图层的易用核心库组成，并有一个生态系统的配套库，帮助你处理大型单页应用中的复杂性。

#### Browser Compatibility
#### 浏览器兼容性
Vue.js supports all browsers that are [ES5-compliant](https://kangax.github.io/compat-table/es5/) (IE8 and below are not supported).

Vue.js 支持所有[兼容 ES5](https://kangax.github.io/compat-table/es5/) 的浏览器（不支持 IE8 及以下版本）。

## Ecosystem
## 生态系统

| Project               | Status                                                       | Description                                             |
| --------------------- | ------------------------------------------------------------ | ------------------------------------------------------- |
| [vue-router]          | [![vue-router-status]][vue-router-package]                   | Single-page application routing                         |
| [vuex]                | [![vuex-status]][vuex-package]                               | Large-scale state management                            |
| [vue-cli]             | [![vue-cli-status]][vue-cli-package]                         | Project scaffolding                                     |
| [vue-loader]          | [![vue-loader-status]][vue-loader-package]                   | Single File Component (`*.vue` file) loader for webpack |
| [vue-server-renderer] | [![vue-server-renderer-status]][vue-server-renderer-package] | Server-side rendering support                           |
| [vue-class-component] | [![vue-class-component-status]][vue-class-component-package] | TypeScript decorator for a class-based API              |
| [vue-rx]              | [![vue-rx-status]][vue-rx-package]                           | RxJS integration                                        |
| [vue-devtools]        | [![vue-devtools-status]][vue-devtools-package]               | Browser DevTools extension                              |


| Project | Status | Description |
| 项目 | 状态 | 描述 |
| --------------------- | ------------------------------------------------------------ | ------------------------------------------------------- |
| [vue-router] | [![vue-router-status]][vue-router-package] | Single-page application routing 单页应用路由 |
| [vuex] | [![vuex-status]][vuex-package] | Large-scale state management 大规模状态管理 |
| [vue-cli] | [![vue-cli-status]][vue-cli-package] | Project scaffolding 项目脚手架 |
| [vue-loader] | [![vue-loader-status]][vue-loader-package] | Single File Component (`*.vue` file) loader for webpack 用于 webpack 的单文件组件（`*.vue` 文件）加载器 |
| [vue-server-renderer] | [![vue-server-renderer-status]][vue-server-renderer-package] | Server-side rendering support 服务端渲染支持 |
| [vue-class-component] | [![vue-class-component-status]][vue-class-component-package] | TypeScript decorator for a class-based API 用于基于类的 API 的 TypeScript 装饰器 |
| [vue-rx] | [![vue-rx-status]][vue-rx-package] | RxJS integration RxJS 集成 |
| [vue-devtools] | [![vue-devtools-status]][vue-devtools-package] | Browser DevTools extension 浏览器开发者工具扩展 |



[vue-router]: https://github.com/vuejs/vue-router
[vuex]: https://github.com/vuejs/vuex
[vue-cli]: https://github.com/vuejs/vue-cli
[vue-loader]: https://github.com/vuejs/vue-loader
[vue-server-renderer]: https://github.com/vuejs/vue/tree/dev/packages/vue-server-renderer
[vue-class-component]: https://github.com/vuejs/vue-class-component
[vue-rx]: https://github.com/vuejs/vue-rx
[vue-devtools]: https://github.com/vuejs/vue-devtools
[vue-router-status]: https://img.shields.io/npm/v/vue-router.svg
[vuex-status]: https://img.shields.io/npm/v/vuex.svg
[vue-cli-status]: https://img.shields.io/npm/v/@vue/cli.svg
[vue-loader-status]: https://img.shields.io/npm/v/vue-loader.svg
[vue-server-renderer-status]: https://img.shields.io/npm/v/vue-server-renderer.svg
[vue-class-component-status]: https://img.shields.io/npm/v/vue-class-component.svg
[vue-rx-status]: https://img.shields.io/npm/v/vue-rx.svg
[vue-devtools-status]: https://img.shields.io/chrome-web-store/v/nhdogjmejiglipccpnnnanhbledajbpd.svg
[vue-router-package]: https://npmjs.com/package/vue-router
[vuex-package]: https://npmjs.com/package/vuex
[vue-cli-package]: https://npmjs.com/package/@vue/cli
[vue-loader-package]: https://npmjs.com/package/vue-loader
[vue-server-renderer-package]: https://npmjs.com/package/vue-server-renderer
[vue-class-component-package]: https://npmjs.com/package/vue-class-component
[vue-rx-package]: https://npmjs.com/package/vue-rx
[vue-devtools-package]: https://chrome.google.com/webstore/detail/vuejs-devtools/nhdogjmejiglipccpnnnanhbledajbpd

## Documentation
## 文档

To check out [live examples](https://v2.vuejs.org/v2/examples/) and docs, visit [vuejs.org](https://v2.vuejs.org).

查看[在线示例](https://v2.vuejs.org/v2/examples/)和文档，请访问 [vuejs.org](https://v2.vuejs.org)。

## Questions
## 问题

For questions and support please use [the official forum](https://forum.vuejs.org) or [community chat](https://chat.vuejs.org/). The issue list of this repo is **exclusively** for bug reports and feature requests.

如需提问和获取支持，请使用[官方论坛](https://forum.vuejs.org)或[社区聊天](https://chat.vuejs.org/)。本仓库的 issue 列表**仅用于**错误报告和功能请求。

## Issues
## Issues（问题）

Please make sure to read the [Issue Reporting Checklist](https://github.com/vuejs/vue/blob/dev/.github/CONTRIBUTING.md#issue-reporting-guidelines) before opening an issue. Issues not conforming to the guidelines may be closed immediately.

在开 issue 之前，请务必阅读[Issue 报告清单](https://github.com/vuejs/vue/blob/dev/.github/CONTRIBUTING.md#issue-reporting-guidelines)。不符合指南的 issue 可能会被立即关闭。

## Changelog
## 更新日志

Detailed changes for each release are documented in the [release notes](https://github.com/vuejs/vue/releases).

每个版本的详细更改都记录在[发布说明](https://github.com/vuejs/vue/releases)中。

## Stay In Touch
## 保持联系


- [Twitter](https://twitter.com/vuejs)
- [Blog](https://medium.com/the-vue-point)
- [Job Board](https://vuejobs.com/?ref=vuejs)

## Contribution
## 贡献

Please make sure to read the [Contributing Guide](https://github.com/vuejs/vue/blob/dev/.github/CONTRIBUTING.md) before making a pull request. If you have a Vue-related project/component/tool, add it with a pull request to [this curated list](https://github.com/vuejs/awesome-vue)!

在提出 pull request 之前，请确保阅读[贡献指南](https://github.com/vuejs/vue/blob/dev/.github/CONTRIBUTING.md)。如果你有 Vue 相关的项目/组件/工具，请通过 pull request 将其添加到[这个精选列表](https://github.com/vuejs/awesome-vue)！

Thank you to all the people who already contributed to Vue!

感谢所有已经为 Vue 做出贡献的人！

<a href="https://github.com/vuejs/vue/graphs/contributors"><img src="https://opencollective.com/vuejs/contributors.svg?width=890" /></a>

## License
## 许可证

[MIT](https://opensource.org/licenses/MIT)

Copyright (c) 2013-present, Yuxi (Evan) You

版权所有 (c) 2013-至今，尤雨溪（Evan You）
