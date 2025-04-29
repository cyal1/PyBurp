# PyBurp
[中文版本](./README-ZH.md)

PyBurp is a Burp Suite extension that provides predefined Python functions for HTTP/WebSocket traffic modification, context menu registration, Intruder payload processing, passive/active scanning, and Collaborator interaction. You can also directly access [Montoya API](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/MontoyaApi.html) in your Python scripts.

## Key Features
- Dynamic modification of HTTP and WebSocket traffic, especially for encrypted communications
- Complex nested JSON, XML, and Form/QueryString transformations
- Powerful parameter fuzzing and testing
- Remote method invocation through Chrome DevTools Protocol and gRPC

For more features, see the [Examples'](#Examples) section below.

## Video Tutorials
- [In PyBurp, invoke JavaScript functions in a webpage via the Chrome DevTools Protocol](https://youtu.be/FRCnZ8a7UGI)
- [In PyBurp, call methods of a Frida-hooked mobile apps through gRPC](https://youtu.be/zfvNqd5VmY0?t=45)

## Installation
Download from the [Release](https://github.com/cyal1/PyBurp/releases) page or install directly from the BApp Store, then open PyBurp from the top menu bar.

<img src="images/pyburp.png"/>

## Usage Guide

### Predefined Functions
PyBurp provides several predefined functions that automatically register corresponding functionality in Burp Suite when defined in your script. Here's a detailed list:

| Function Name | Description                                                 |
|------------|-------------------------------------------------------------|
| handleRequest(request: [HttpRequest](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html), annotations: [Annotations](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/Annotations.html)) | Processes requests between Burp Suite and the server        |
| handleResponse(response: [HttpResponse](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html), annotations: [Annotations](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/Annotations.html)) | Processes responses between Burp Suite and the server       |
| handleProxyRequest(request: [HttpRequest](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html), annotations: [Annotations](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/Annotations.html)) | Processes requests between the client and Burp Suite        |
| handleProxyResponse(response: [HttpResponse](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html), annotations: [Annotations](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/Annotations.html)) | Processes responses between the client and Burp Suite       |
| urlPrefixAllowed(urls) | Sets allowed URL prefixes for the current PyBurp tab        |
| registerContextMenu(menus) | Registers custom context menu items                         |
| processPayload(str) | Registers custom Intruder payload processor                 |
| handleInteraction(interaction: [Interaction](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/collaborator/Interaction.html)) | Polls Collaborator server |
| passiveAudit(baseRequestResponse: [HttpRequestResponse](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/HttpRequestResponse.html)) | Performs passive scanning                                   |
| activeAudit(baseRequestResponse: [HttpRequestResponse](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/HttpRequestResponse.html), auditInsertionPoint: [AuditInsertionPoint](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/scanner/audit/insertionpoint/AuditInsertionPoint.html)) | Performs active scanning                                    |
| handleWsTextMsg(message: [TextMessage](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/websocket/TextMessage.html)) | Processes text WebSocket messages                           |
| handleWsBinMsg(message: [BinaryMessage](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/websocket/BinaryMessage.html)) | Processes binary WebSocket messages                         |
| handleProxyWsTextMsg(message: [InterceptedTextMessage](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/proxy/websocket/InterceptedTextMessage.html)) | Processes text WebSocket messages in proxy                  |
| handleProxyWsBinMsg(message: [InterceptedBinaryMessage](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/proxy/websocket/InterceptedBinaryMessage.html)) | Processes binary WebSocket messages in proxy                |
| finish() | Called when the script is stopped                           |

### Examples
PyBurp includes a variety of example scripts to demonstrate its capabilities:

| File                                                                      | Description |
|---------------------------------------------------------------------------|-------------|
| [`env_init.py`](src/main/resources/examples/env_init.py)                  | Automatically runs before each user script execution, providing common classes and methods |
| [`bambdas.py`](src/main/resources/examples/bambdas.py)                                       | Quick information extraction from Proxy history |
| [`chrome_devtools_protocol.py`](src/main/resources/examples/chrome_devtools_protocol.py)     | Remote Chrome DevTools method invocation |
| [`collaborator.py`](src/main/resources/examples/collaborator.py)                             | Collaborator usage example |
| [`customise_context_menu.py`](src/main/resources/examples/customise_context_menu.py)         | Context menu registration examples |
| [`default.py`](src/main/resources/examples/default.py)                                       | Basic request/response modification demo |
| [`encryptedCompleteBody.py`](src/main/resources/examples/encryptedCompleteBody.py)           | Complete HTTP body encryption handling |
| [`encryptedCompleteBodyAes.py`](src/main/resources/examples/encryptedCompleteBodyAes.py)     | AES encryption for complete HTTP body |
| [`encryptedJsonParam.py`](src/main/resources/examples/encryptedJsonParam.py)                 | JSON parameter encryption handling |
| [`encryptedJsonParamRpc.py`](src/main/resources/examples/encryptedJsonParamRpc.py)           | JSON parameter encryption with RPC |
| [`encryptedQueryForm.py`](src/main/resources/examples/encryptedQueryForm.py)                 | Query String encryption handling |
| [`fuzz_params.py`](src/main/resources/examples/fuzz_params.py)                               | Comprehensive parameter fuzzing |
| [`highlight_interesting_http.py`](src/main/resources/examples/highlight_interesting_http.py) | HTTP request/response highlighting |
| [`passive_active_scan.py`](src/main/resources/examples/passive_active_scan.py)               | Active and passive scanning examples |
| [`process_intruder_payload.py`](src/main/resources/examples/process_intruder_payload.py)     | Custom Intruder payload processing |
| [`race_condition.py`](src/main/resources/examples/race_condition.py)                         | Race condition testing |
| [`rpc_debug.py`](src/main/resources/examples/rpc_debug.py)                                   | RPC testing |
| [`save_subdomain_to_sqlite.py`](src/main/resources/examples/save_subdomain_to_sqlite.py)     | Subdomain collection from Proxy history |
| [`signatureHeader.py`](src/main/resources/examples/signatureHeader.py)                       | Header signature handling |
| [`traffic_redirector.py`](src/main/resources/examples/traffic_redirector.py)                 | HTTP request redirection |
| [`urls_from_file.py`](src/main/resources/examples/urls_from_file.py)                         | Multi-threaded URL request processing |
| [`urls_from_file2.py`](src/main/resources/examples/urls_from_file2.py)                       | Thread pool-based URL request processing |
| [`websocket.py`](src/main/resources/examples/websocket.py)                                   | WebSocket message handling and modification |
| [`use_pip2_packages.py`](src/main/resources/examples/use_pip2_packages.py)                   | Third-party package usage examples |

### Quick Start

#### Context Menu Registration
To register context menu items, define a `registerContextMenu` function that takes a menu collection as a parameter. The `register` method accepts three parameters: menu name, associated function name, and menu type (`MenuType`). The menu type determines when and where the menu item appears, and what data is passed to the associated function.

Available menu types:
- `CARET`: Menu appears at cursor position in Repeater tool, returns a string to insert at the cursor
- `SELECTED_TEXT`: Menu appears when text is selected, processes the selected text and can modify it
- `REQUEST`: Menu appears only in request context, receives the current [HttpRequest](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html) as parameter
- `REQUEST_RESPONSE`: Menu appears only when both request and response are available, receives [HttpRequestResponse](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/HttpRequestResponse.html) as parameters
- `MESSAGE_EDITOR`: Menu appears in message editor context, receives the [MessageEditorHttpRequestResponse](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/ui/contextmenu/MessageEditorHttpRequestResponse.html) instance for modification

When using `MESSAGE_EDITOR` as the menu type, the following two utility methods are also available:
* getSelectedText([editor](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/ui/contextmenu/MessageEditorHttpRequestResponse.html)), Retrieves the selected text, returning a [ByteArray](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/ByteArray.html) instance.
* replaceSelectedText([editor](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/ui/contextmenu/MessageEditorHttpRequestResponse.html), "new string")，Replaces the selected text in editor, returning an [HttpRequest](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html) instance.

See [`customise_context_menu.py`](src/main/resources/examples/customise_context_menu.py) for examples.

#### RPC Integration
PyBurp supports method invocation through gRPC. You need to implement the service interface defined in [burpextender.proto](https://github.com/cyal1/pyburpRPC/blob/main/burpextender.proto).

For Python users, the service interface is implemented in [pyburp](https://github.com/cyal1/pyburpRPC/). Install it using:
```bash
pip install pyburp
```

Here's a Python demonstration:

1. Run the RPC server with the following code, which provides `test1` and `test2` functions:
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

2. Run the client code in PyBurp as follows:
    ```python
   server = rpc("localhost", 30051)
   result1 = server.callFunc('test1', bytearray("123"))
   result2 = server.callFunc('test2', 3, 4)
   print(result1)
   print(result1.tostring()) # or print(bytearray(result1))
   print(result2)
   server.shutdown()
    ```
**Note:**
1. pyburp supports only the following parameter types: `str`,`bool`,`int`,`float`,`bytes`,`None`.
2. If the server-side exposed method has **only one parameter and it is of type bytes**，in PyBurp, you need to wrap the parameter using [bytearray](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/ByteArray.html#byteArray(java.lang.String)) or place it in `[]`; otherwise, `byte[]` will be treated as a variable-length argument array, with each byte being treated as an individual argument.
3. The `bytes` type returned by the server is of type [array.array('b',initializer)](https://www.jython.org/jython-old-sites/docs/library/array.html#array-efficient-arrays-of-numeric-values) in PyBurp，you can consider it as `byte[]`, except that you need to use `tostring()` to convert it to a string instead of `toString()`.


For more examples, check out [`rpc_debug.py`](src/main/resources/examples/rpc_debug.py) and [`encryptedJsonParamRpc.py`](src/main/resources/examples/encryptedJsonParamRpc.py). You can also find examples of Frida interactions in [server_frida.py](https://github.com/cyal1/pyburpRPC/blob/main/examples/server_frida.py).

## Development Quick Start

### Project Structure
```
PyBurp/
├── src/
    ├── main/
        ├── java/
        │   └── io/github/cyal1/pyburp/
        │       ├── PyBurp.java             # Main extension entry point
        │       ├── PyBurpTab.java          # Extension tab implementation
        │       ├── PyBurpTabs.java         # Tab management
        │       ├── MyHttpHandler.java      # HTTP request/response handler
        │       ├── MyProxyRequestHandler.java  # Proxy request handler
        │       ├── MyProxyResponseHandler.java # Proxy response handler
        │       ├── MyPayloadProcessor.java # Intruder payload processor
        │       ├── MyContextMenuItemsProvider.java  # Context menu provider
        │       ├── MyWebSocketCreatedHandler.java  # WebSocket handler
        │       ├── MyProxyWebSocketCreationHandler.java  # Proxy WebSocket handler
        │       ├── MyInteractionHandler.java  # Collaborator interaction handler
        │       ├── MyScanCheck.java        # Scanner implementation
        │       ├── ContentTypeContextMenu.java  # Content type menu handler
        │       ├── ContentTypeConverter.java  # Content type conversion
        │       ├── Tools.java              # Utility functions
        │       ├── ComboBoxRenderer.java   # UI component
        │       ├── CallFuncClient.java     # gRPC client
        │       ├── CallFuncServiceGrpc.java  # gRPC service
        │       ├── Burpextender.java       # source: burpextender.proto
        │       └── poller/                 # Poller implementation
        └── resources/
            ├── examples/                   # Example scripts
            └── qs.js                       # https://github.com/ljharb/qs

```

Most of the code is based on [PortSwigger's Burp Extensions Montoya API Examples](https://github.com/PortSwigger/burp-extensions-montoya-api-examples).

## Contributions
I welcome contributions from the community to help improve PyBurp. Whether you're fixing bugs, adding features, or improving documentation, your help is greatly appreciated! Here's what I'm focusing on next:

- Simplify the `HttpRequestEditorProvider` and `HttpResponseEditorProvider` registration process
- Add code completion support (see [example](https://github.com/bobbylight/AutoComplete/tree/master/AutoCompleteDemo/src/main/java/org/fife/ui/autocomplete/demo))
- Implement a lightweight search box
- Make hyperlinks clickable
- Create better video demonstrations
- Enhance error handling


Thank you for your support and involvement!

## FAQ
1. Why can't some Python libraries or methods be used in PyBurp?  
   PyBurp uses Jython instead of standard CPython, so not all third-party Python libraries are compatible, but it provides seamless access to Java libraries.

2. How to create `byte[]` compatible byte arrays in Jython?  
   Three methods are available:
   - [`bytestring("asdf")`](https://github.com/cyal1/PyBurp/blob/main/src/main/resources/examples/env_init.py#L128)
   - [`bytearray("asdf").getBytes()`](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/ByteArray.html)
   - [`import array; print(array.array('b', [97, 115, 100, 102]))`](https://www.jython.org/jython-old-sites/docs/library/array.html#array-efficient-arrays-of-numeric-values)

   Note that byte arrays created by these methods should be converted to string using `tostring()` instead of `toString()`.


## Acknowledgments
PyBurp is inspired by [Turbo Intruder](https://github.com/PortSwigger/turbo-intruder/)


