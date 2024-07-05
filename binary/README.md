以下是对上述内容的中文翻译：

# 继续核心二进制
这个文件夹的目的是以一种可以在任何IDE或平台上运行的方式打包Typescript代码。我们首先使用  `esbuild`  进行捆绑，然后使用  `pkg`  打包成二进制文件。
 `pkgJson/package.json`  包含了使用 pkg 进行构建的说明，需要放在一个单独的文件夹中，因为没有关于 assets 选项的CLI标志（它必须在 package.json 中），而且 pkg 不识别除 package.json 之外的任何名称，但如果我们使用带有依赖项的相同 package.json，pkg 将自动包含这些依赖项，从而显著增加二进制文件的大小。
构建过程在  `build.js`  中完全定义。
### 本地模块列表
- sqlite3/build/Release/node_sqlite3.node (\*)
- @lancedb/\*\*
- esbuild？
- @esbuild？
- onnxruntime-node？
### 动态导入模块列表
- posthog-node
- @octokit/rest
- esbuild
### .wasm 文件列表
- tree-sitter.wasm
- tree-sitter-wasms/
(\*) = 需要为每个平台手动下载
## 调试
要在IntelliJ中调试二进制文件，请在  `CoreMessenger.kt`  中将  `useTcp`  设置为  `true` ，然后在VS Code中运行 "Core Binary" 调试脚本。与启动子进程并通过stdin/stdout通信不同，IntelliJ扩展将通过TCP连接到从VS Code窗口启动的服务器。您可以在  `core`  或  `binary`  文件夹的任何位置设置断点。