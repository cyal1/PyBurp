# PyBurp
[English Version](./README.md)

PyBurp 是一个 Burp Suite 扩展，提供预定义的 Python 函数用于 HTTP/WebSocket 流量修改、上下文菜单注册、Intruder 负载处理、被动/主动扫描以及 Collaborator 交互。您还可以在 Python 脚本中直接访问 [Montoya API](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html)。

## 主要特性
- HTTP 和 WebSocket 流量的动态修改，特别适合处理加密通信
- 复杂的嵌套 JSON、XML 和 Form/QueryString 转换
- 强大的参数模糊测试
- 通过 Chrome DevTools Protocol 或 gRPC 进行远程方法调用

更多特性请参见下面的[示例](#示例)部分。

## 视频教程
- [在 PyBurp 中通过 Chrome DevTools Protocol 调用网页中 JavaScript 函数](https://youtu.be/FRCnZ8a7UGI)
- [在 PyBurp 中通过 gRPC 调用 Frida hook 的移动应用的方法](https://youtu.be/zfvNqd5VmY0?t=45)

## 安装
从[Release](https://github.com/cyal1/PyBurp/releases)下载或直接从 BApp Store 安装，然后从顶部菜单栏打开 PyBurp。

<img src="images/pyburp.png"/>

## 使用指南

### 预定义函数
PyBurp 提供了几个预定义函数，当在脚本中定义时，会自动在 Burp Suite 中注册相应的功能。以下是详细列表：

| 函数名称 | 描述 |
|------------|-------------|
| handleRequest(request: [HttpRequest](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html), annotations: [Annotations](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/Annotations.html)) | 处理 Burp Suite 和服务器之间的请求 |
| handleResponse(response: [HttpResponse](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html), annotations: [Annotations](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/Annotations.html)) | 处理 Burp Suite 和服务器之间的响应 |
| handleProxyRequest(request: [HttpRequest](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html), annotations: [Annotations](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/Annotations.html)) | 处理客户端和 Burp Suite 之间的请求 |
| handleProxyResponse(response: [HttpResponse](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html), annotations: [Annotations](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/Annotations.html)) | 处理客户端和 Burp Suite 之间的响应 |
| urlPrefixAllowed(urls) | 设置当前 PyBurp 标签页允许的 URL 前缀 |
| registerContextMenu(menus) | 注册自定义上下文菜单项 |
| processPayload(str) | 注册自定义 Intruder 负载处理器 |
| handleInteraction(interaction: [Interaction](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/collaborator/Interaction.html)) | 轮询 Collaborator 服务器 |
| passiveAudit(baseRequestResponse: [HttpRequestResponse](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/HttpRequestResponse.html)) | 执行被动扫描 |
| activeAudit(baseRequestResponse: [HttpRequestResponse](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/HttpRequestResponse.html), auditInsertionPoint: [AuditInsertionPoint](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/scanner/audit/insertionpoint/AuditInsertionPoint.html)) | 执行主动扫描 |
| handleWsTextMsg(message: [TextMessage](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/websocket/TextMessage.html)) | 处理文本 WebSocket 消息 |
| handleWsBinMsg(message: [BinaryMessage](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/websocket/BinaryMessage.html)) | 处理二进制 WebSocket 消息 |
| handleProxyWsTextMsg(message: [InterceptedTextMessage](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/proxy/websocket/InterceptedTextMessage.html)) | 处理代理中的文本 WebSocket 消息 |
| handleProxyWsBinMsg(message: [InterceptedBinaryMessage](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/proxy/websocket/InterceptedBinaryMessage.html)) | 处理代理中的二进制 WebSocket 消息 |
| finish() | 脚本停止时调用 |

### 示例
PyBurp 包含各种示例脚本来展示其功能：

| 文件 | 描述 |
|------|-------------|
| [`env_init.py`](src/main/resources/examples/env_init.py) | 在每个用户脚本执行前自动运行，提供通用类和方法 |
| [`bambdas.py`](src/main/resources/examples/bambdas.py) | 从代理历史中快速提取信息 |
| [`chrome_devtools_protocol.py`](src/main/resources/examples/chrome_devtools_protocol.py) | 远程 Chrome DevTools 方法调用 |
| [`collaborator.py`](src/main/resources/examples/collaborator.py) | Collaborator 使用示例 |
| [`customise_context_menu.py`](src/main/resources/examples/customise_context_menu.py) | 上下文菜单注册示例 |
| [`default.py`](src/main/resources/examples/default.py) | 基本请求/响应修改演示 |
| [`encryptedCompleteBody.py`](src/main/resources/examples/encryptedCompleteBody.py) | 完整 HTTP 体加密处理 |
| [`encryptedCompleteBodyAes.py`](src/main/resources/examples/encryptedCompleteBodyAes.py) | 完整 HTTP 体的 AES 加密 |
| [`encryptedJsonParam.py`](src/main/resources/examples/encryptedJsonParam.py) | JSON 参数加密处理 |
| [`encryptedJsonParamRpc.py`](src/main/resources/examples/encryptedJsonParamRpc.py) | RPC 处理 JSON 参数加密 |
| [`encryptedQueryForm.py`](src/main/resources/examples/encryptedQueryForm.py) | 查询字符串加密处理 |
| [`fuzz_params.py`](src/main/resources/examples/fuzz_params.py) | 全面的参数模糊测试 |
| [`highlight_interesting_http.py`](src/main/resources/examples/highlight_interesting_http.py) | HTTP 请求/响应高亮 |
| [`passive_active_scan.py`](src/main/resources/examples/passive_active_scan.py) | 主动和被动扫描示例 |
| [`process_intruder_payload.py`](src/main/resources/examples/process_intruder_payload.py) | 自定义 Intruder 负载处理 |
| [`race_condition.py`](src/main/resources/examples/race_condition.py) | 竞态条件测试 |
| [`rpc_debug.py`](src/main/resources/examples/rpc_debug.py) | RPC 测试 |
| [`save_subdomain_to_sqlite.py`](src/main/resources/examples/save_subdomain_to_sqlite.py) | 从代理历史中收集子域名 |
| [`signatureHeader.py`](src/main/resources/examples/signatureHeader.py) | Header中的签名处理 |
| [`traffic_redirector.py`](src/main/resources/examples/traffic_redirector.py) | HTTP 目标重定向 |
| [`urls_from_file.py`](src/main/resources/examples/urls_from_file.py) | 多线程 URL 请求处理 |
| [`urls_from_file2.py`](src/main/resources/examples/urls_from_file2.py) | 基于线程池的 URL 请求处理 |
| [`websocket.py`](src/main/resources/examples/websocket.py) | WebSocket 消息处理和修改 |
| [`use_pip2_packages.py`](src/main/resources/examples/use_pip2_packages.py) | 第三方包使用示例 |

### 快速入门

#### 上下文菜单注册
要注册上下文菜单项，定义一个接受菜单集合作为参数的 `registerContextMenu` 函数。`register` 方法接受三个参数：菜单名称、关联的函数名称和菜单类型（`MenuType`）。菜单类型决定了菜单项何时何地出现，以及传递给关联函数的数据。

可用的菜单类型：
- `CARET`：在 Repeater 工具的光标位置显示菜单，返回要在光标处插入的字符串
- `SELECTED_TEXT`：在文本被选中时显示菜单，处理选中的文本并可以修改它
- `REQUEST`：仅在请求上下文中显示菜单，接收当前 [HttpRequest](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html) 作为参数
- `REQUEST_RESPONSE`：仅在请求和响应都可用时显示菜单，接收 [HttpRequestResponse](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/HttpRequestResponse.html) 作为参数
- `MESSAGE_EDITOR`：在消息编辑器上下文中显示菜单，接收 [MessageEditorHttpRequestResponse](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/ui/contextmenu/MessageEditorHttpRequestResponse.html) 参数

使用 `MESSAGE_EDITOR` 作为菜单类型时，还可以使用以下两个实用方法：
* getSelectedText([editor](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/ui/contextmenu/MessageEditorHttpRequestResponse.html))，获取选中的文本，返回 [ByteArray](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/ByteArray.html) 实例。
* replaceSelectedText([editor](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/ui/contextmenu/MessageEditorHttpRequestResponse.html), "new string")，替换编辑器中的选中文本，返回 [HttpRequest](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html) 实例。

参见 [`customise_context_menu.py`](src/main/resources/examples/customise_context_menu.py) 获取示例。

#### RPC 集成
PyBurp 支持通过 gRPC 进行方法调用。您需要实现 [burpextender.proto](https://github.com/cyal1/pyburpRPC/blob/main/burpextender.proto) 中定义的服务接口。

对于 Python 用户，服务接口在 [pyburp](https://github.com/cyal1/pyburpRPC/) 中实现。使用以下命令安装：
```bash
pip install pyburp
```

以下是 Python 演示：

1. 使用以下代码运行 RPC 服务器，它提供 `test1` 和 `test2` 函数：
   ```python
   import pyburp
   from base64 import b64encode
   
   def test1(s):
       return b64encode(s)
   
   def test2(i, j):
       return i + j
   
   pyburp.expose(test1)
   pyburp.expose(test2)
   pyburp.run("127.0.0.1:30051")
    ```

2. 在 PyBurp 中运行客户端代码：
    ```python
   server = rpc("localhost", 30051)
   result1 = server.callFunc('test1', bytearray("123"))
   result2 = server.callFunc('test2', 3, 4)
   print(result1)
   print(result1.tostring()) # 或 print(bytearray(result1))
   print(result2)
   server.shutdown()
    ```

**注意：**
1. pyburp 仅支持以下参数类型：`str`、`bool`、`int`、`float`、`bytes`、`None`。
2. 如果服务器端暴露的方法**只有一个参数且类型为 bytes**，在 PyBurp 中，您需要使用 [bytearray](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/ByteArray.html#byteArray(java.lang.String)) 包装参数或将其放在 `[]` 中；否则，`byte[]` 将被视为可变长度参数数组，每个字节都被视为单独的参数。
3. 服务器返回的 `bytes` 类型在 PyBurp 中是 [array.array('b',initializer)](https://www.jython.org/jython-old-sites/docs/library/array.html#array-efficient-arrays-of-numeric-values) 类型，您可以将其视为 `byte[]`，但需要使用 `tostring()` 而不是 `toString()` 来转换为字符串。

更多示例请查看 [`rpc_debug.py`](src/main/resources/examples/rpc_debug.py) 和 [`encryptedJsonParamRpc.py`](src/main/resources/examples/encryptedJsonParamRpc.py)。有关与 Frida 交互的示例，请查看 [server_frida.py](https://github.com/cyal1/pyburpRPC/blob/main/examples/server_frida.py)。

## 开发快速入门

### 项目结构
```
PyBurp/
├── src/
    ├── main/
        ├── java/
        │   └── io/github/cyal1/pyburp/
        │       ├── PyBurp.java             # 扩展主入口点
        │       ├── PyBurpTab.java          # 扩展标签页实现
        │       ├── PyBurpTabs.java         # 标签页管理
        │       ├── MyHttpHandler.java      # HTTP 请求/响应处理器
        │       ├── MyProxyRequestHandler.java  # 代理请求处理器
        │       ├── MyProxyResponseHandler.java # 代理响应处理器
        │       ├── MyPayloadProcessor.java # Intruder 负载处理器
        │       ├── MyContextMenuItemsProvider.java  # 上下文菜单提供者
        │       ├── MyWebSocketCreatedHandler.java  # WebSocket 处理器
        │       ├── MyProxyWebSocketCreationHandler.java  # 代理 WebSocket 处理器
        │       ├── MyInteractionHandler.java  # Collaborator 交互处理器
        │       ├── MyScanCheck.java        # 扫描器实现
        │       ├── ContentTypeContextMenu.java  # 内容类型菜单处理器
        │       ├── ContentTypeConverter.java  # 内容类型转换
        │       ├── Tools.java              # 工具函数
        │       ├── ComboBoxRenderer.java   # UI 组件
        │       ├── CallFuncClient.java     # gRPC 客户端
        │       ├── CallFuncServiceGrpc.java  # gRPC 服务
        │       ├── Burpextender.java       # 源文件：burpextender.proto
        │       └── poller/                 # 轮询器实现
        └── resources/
            ├── examples/                   # 示例脚本
            └── qs.js                       # https://github.com/ljharb/qs
```

大部分代码基于 [PortSwigger's Burp Extensions Montoya API Examples](https://github.com/PortSwigger/burp-extensions-montoya-api-examples)。

## 贡献
欢迎社区贡献来帮助改进 PyBurp。无论您是修复错误、添加功能还是改进文档，您的帮助都将受到极大的赞赏！以下是我接下来要关注的内容：

- 简化 `HttpRequestEditorProvider` 和 `HttpResponseEditorProvider` 的注册过程
- 添加代码补全支持（参见[示例](https://github.com/bobbylight/AutoComplete/tree/master/AutoCompleteDemo/src/main/java/org/fife/ui/autocomplete/demo)）
- 实现轻量级搜索框
- 使超链接可点击
- 创建更好的视频演示
- 增强错误处理

感谢您的支持和参与！

## 常见问题
1. 为什么某些 Python 库或方法不能在 PyBurp 中使用？  
   PyBurp 使用 Jython 而不是标准的 CPython，因此并非所有第三方 Python 库都兼容，但它提供了对 Java 库的无缝访问。

2. 如何在 Jython 中创建 `byte[]` 兼容的字节数组？  
   有三种方法：
   - [`bytestring("asdf")`](https://github.com/cyal1/PyBurp/blob/main/src/main/resources/examples/env_init.py#L128)
   - [`bytearray("asdf").getBytes()`](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/ByteArray.html)
   - [`import array; print(array.array('b', [97, 115, 100, 102]))`](https://www.jython.org/jython-old-sites/docs/library/array.html#array-efficient-arrays-of-numeric-values)

   注意，这些方法创建的字节数组应该使用 `tostring()` 而不是 `toString()` 来转换为字符串。

## 致谢
PyBurp 的灵感来自 [Turbo Intruder](https://github.com/PortSwigger/turbo-intruder/)


