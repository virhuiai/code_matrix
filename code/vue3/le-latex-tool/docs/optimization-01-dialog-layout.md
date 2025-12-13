# 优化一：统一“按钮 + 弹窗 + 两栏布局 + 代码预览”壳组件

## 背景
项目内多个宏包设置组件（如 `GeometryPackage.vue`、`CodeListingPackage.vue`、`PackageOptions.vue` 等）存在高度相似的结构：

- 顶部按钮触发弹窗
- 弹窗内部使用 `ElCard`
- 左侧为选项设置，右侧为 LaTeX 代码预览

为降低重复、提升一致性与可维护性，本次抽取通用壳组件，仅优化这一类结构。

## 变更概要

- 新增通用壳组件：`src/components/PackageDialogLayout.vue`
- 将 `src/components/GeometryPackage.vue` 改造为使用该壳组件

## 新组件 API：PackageDialogLayout

Props：

- `buttonLabel: string`：触发弹窗的按钮文案
- `dialogTitle: string`：弹窗标题
- `buttonFullWidth?: boolean`：按钮是否全宽（默认全宽，`false` 时取消全宽样式）
- `dialogWidth?: string | number`：弹窗宽度（默认 `100%`）
- `code?: string`：右侧代码预览内容，若不传则使用 `#right` 插槽

Slots：

- `#left`：左侧选项内容
- `#right`：右侧预览内容（不传 `code` 时使用此插槽）
- `#footer`：弹窗底部区域

Expose：

- `openDialog()`：打开弹窗
- `closeDialog()`：关闭弹窗

文件位置：`src/components/PackageDialogLayout.vue`

## 使用示例（以 GeometryPackage 为例）

改造前（简化示例）：

```vue
<el-button @click="openDialog">Geometry 版面设置</el-button>
<el-dialog v-model="isDialogOpen" title="Geometry 版面设置">
  <el-card>
    <div class="package-options-container">
      <div class="package-options-left">...左侧选项...</div>
      <div class="package-options-right">
        <pre class="code-preview-content">{{ latexCode }}</pre>
      </div>
    </div>
  </el-card>
</el-dialog>
```

改造后：

```vue
<PackageDialogLayout
  button-label="Geometry 版面设置"
  dialog-title="Geometry 版面设置"
  :code="latexCode"
>
  <template #left>
    <!-- 左侧选项内容 -->
  </template>
</PackageDialogLayout>
```

注意：保留了既有的 CSS 类名（如 `package-options-container`、`package-options-left`、`package-options-right`、`code-preview-content`），与项目样式保持一致。

## 受影响文件

- 新增：`src/components/PackageDialogLayout.vue`
- 更新：`src/components/GeometryPackage.vue`

## 优势

- 统一 UI 结构，减少重复代码
- 降低维护成本：布局修改只需改动壳组件
- 更易扩展：右侧预览既可通过 `code` 传入，也可自定义 `#right` 插槽

## 后续可拓展方向（不在本次变更内）

- 将其它同类型组件逐步迁移到 `PackageDialogLayout`
- 将代码预览区域支持复制、下载等交互
- 将按钮样式、弹窗宽度等规范化为主题配置

## 验证

- 通过 `vue-tsc` 类型检查与 `vite build` 构建
- 运行开发服务后，`Geometry 版面设置` 功能正常，代码预览显示正常

---

一次优化一种类型：本次仅抽取并统一弹窗两栏布局的壳组件，未涉及其它逻辑改动。

