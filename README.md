# PyBurp
[中文版](./README-ZH.md)

PyBurp is a powerful Burp Suite extension that enables you to write simple Python code to dynamically modify HTTP requests and responses. It is especially useful for handling scenarios where HTTP requests and responses are encrypted, as it supports remote invocation of encryption and decryption methods in Chrome or mobile applications.

In addition, PyBurp supports:
* Complex nested JSON, Query String, and XML Content-Type conversions
* Dynamic registration of custom context menus
* Saving relevant information from HTTP history to an SQLite database
* Chrome DevTools Protocol
* gRPC
* ...

**Video**

Interaction with Chrome： [https://youtu.be/FRCnZ8a7UGI](https://youtu.be/FRCnZ8a7UGI)    
Interaction with Frida： [https://youtu.be/zfvNqd5VmY0](https://youtu.be/zfvNqd5VmY0)

For method calls in mobile applications, PyBurp relies on Frida for remote interaction.

> Please note that the above features have been thoroughly tested on Burp Suite v2024.5.4. It is recommended to use this version or a later one for optimal compatibility and stability.

## Installation
Download from the [Release](https://github.com/cyal1/PyBurp/releases) page or install directly from the BApp Store.

To use gRPC or invoke methods in mobile applications, you also need to install [pyburp](https://github.com/cyal1/PyBurpRpc/).
```bash
pip install pyburp
```
<img src="./images/pyburp.png">

## Usage Guide

### Predefined Functions
PyBurp includes several predefined functions. When you define these functions in your script, they are automatically registered in Burp Suite with the corresponding functionality. The detailed predefined functions are listed in the table below:

| function name                                                                                                                                                                                                                                                                                                                | Description                                                                                                                                                                                                                   |
|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| handleRequest([request](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html), [annotations](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/Annotations.html))                                                 | Handles requests between Burp Suite and the server                                                                                                                                                                            |
| handleResponse([response](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html), [annotations](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/Annotations.html))                                             | Handles responses between Burp Suite and the server                                                                                                                                                                           |
| handleProxyRequest([request](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html), [annotations](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/Annotations.html))                                            | Handles requests between the client and Burp Suite                                                                                                                                                                            |
| handleProxyResponse([response](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/responses/HttpResponse.html), [annotations](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/Annotations.html))                                        | Handles responses between the client and Burp Suite                                                                                                                                                                           |
| urlPrefixAllowed(urls)                                                                                                                                                                                                                                                                                                       | Sets URL prefixes that the current PyBurp tab is allowed to handle, using `urls.add(url)` to add prefixes. Used with the above 4 methods.                                                                                     |
| registerContextMenu(menus)                                                                                                                                                                                                                                                                                                   | See [Registering Context Menus](#jump)                                                                                                                                                                                        |
| handleInteraction([interaction](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/collaborator/Interaction.html))                                                                                                                                                                           | Polls the Collaborator server. This method automatically registers a Collaborator client, and Payloads can be obtained via `getOOBCanary()`. Example script: [collaborator.py](./src/main/resources/examples/collaborator.py) |
| passiveScan([baseRequestResponse](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/HttpRequestResponse.html))                                                                                                                                                                 | Passive scanning                                                                                                                                                                                                              |
| activeScan([baseRequestResponse](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/HttpRequestResponse.html), [auditInsertionPoint](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/scanner/audit/insertionpoint/AuditInsertionPoint.html)) | Performs active scanning                                                                                                                                                                                                      |
| finish()                                                                                                                                                                                                                                                                                                                     | Called when the script stops                                                                                                                                                                                                  |

> After running the script, you can view the registration details in the Details tab of the Extensions in Burp Suite.

### Examples
To help you get familiar with using predefined functions, PyBurp includes several common scripts, as listed in the table below:

| file name                                                                                    | Description                                                                                                                                             |
|----------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| [api_documentation.py](./src/main/resources/examples/api_documentation.py)                   | Provides documentation of PyBurp’s predefined and built-in functions. **No need to run**                                                                |
| [notes.md](./src/main/resources/examples/notes.md)                                           | Save some Payloads (this file is not saved, please create your own). **No need to run**                                                                 |
| [env_init.py](./src/main/resources/examples/env_init.py)                                     | Environment initialization, includes common classes and methods executed automatically in each PyBurp tab. **No need to run**                           |
| [bambdas.py](./src/main/resources/examples/bambdas.py)                                       | Quickly find interesting content from Proxy history                                                                                                     |
| [chrome_devtools_protocol.py](./src/main/resources/examples/chrome_devtools_protocol.py)     | Remote invocation of Chrome DevTools methods                                                                                                            |
| [collaborator.py](./src/main/resources/examples/collaborator.py)                             | Example for Collaborator                                                                                                                                |
| [customise_context_menu.py](./src/main/resources/examples/customise_context_menu.py)         | Example of registering context menus with items like NoSQL injection, race conditions, and authorization bypasses                                       |
| [default.py](./src/main/resources/examples/default.py)                                       | Default script demonstrating simple request and response modification                                                                                   |
| [encryptedCompleteBody.py](./src/main/resources/examples/encryptedCompleteBody.py)           | Example for handling encrypted HTTP body                                                                                                                |
| [encryptedCompleteBodyAes.py](./src/main/resources/examples/encryptedCompleteBodyAes.py)     | Example for handling AES encrypted HTTP body                                                                                                            |
| [encryptedJsonParam.py](./src/main/resources/examples/encryptedJsonParam.py)                 | Example for handling encrypted JSON parameters                                                                                                          |
| [encryptedJsonParamRpc.py](./src/main/resources/examples/encryptedJsonParamRpc.py)           | Example of handling encryption and decryption using RPC.                                                                                                |
| [encryptedQueryForm.py](./src/main/resources/examples/encryptedQueryForm.py)                 | Example of handling encryption and decryption for query strings.                                                                                        |
| [highlight_interesting_http.py](./src/main/resources/examples/highlight_interesting_http.py) | Highlights interesting requests and adds notes. It is generally recommended to use passive scanning.                                                    |
| [passive_active_scan.py](./src/main/resources/examples/passive_active_scan.py)               | Example of active and passive scanning.                                                                                                                 |
| [race_condition.py](./src/main/resources/examples/race_condition.py)                         | Race condition examples (single-packet for HTTP2, last-byte sync for HTTP1.1).                                                                          |
| [rpc_debug.py](./src/main/resources/examples/rpc_debug.py)                                   | gRPC testing.                                                                                                                                           |
| [save_subdomain_to_sqlite.py](./src/main/resources/examples/save_subdomain_to_sqlite.py)     | Collects subdomains from Proxy HTTP history and saves them to a database or file.                                                                       |
| [signatureHeader.py](./src/main/resources/examples/signatureHeader.py)                       | Handles signatures in headers.                                                                                                                          |
| [urls_from_file.py](./src/main/resources/examples/urls_from_file.py)                         | Reads URLs from a file and send requests (Python threading).                                                                                            |
| [urls_from_file2.py](./src/main/resources/examples/urls_from_file2.py)                       | Reads URLs from a file and send requests（[built-in RequestPool](https://github.com/cyal1/PyBurp/blob/main/src/main/resources/examples/env_init.py#L46)） |
| [use_pip2_packages.py](./src/main/resources/examples/use_pip2_packages.py)                   | Example of using Python third-party libraries. Not all third-party libraries are compatible with Jython.                                                |

> Please note that modifications to the built-in example script files will not be saved.
###  Registering Context Menu
<span id="jump"></span>
To register context menu items in your code, you first need to define a function named `registerContextMenu` that accepts a `menus` as a parameter.  
Then, call the `register` method of the `menus` object to register specific menu items. The `register` method takes three parameters: the name of the menu, the name of the function associated with the menu item (which is called when the menu item is clicked), and the `MenuType`. The table below shows the `MenuType` options and the requirements for associated functions:

| MenuType         | Function                                                                                                                                                                                                                                                                                                                      |
|------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| CARET            | No parameters, returns a string to be inserted at the current cursor position                                                                                                                                                                                                                                                 |
| SELECTED_TEXT    | Accepts the selected string as a parameter and returns the processed string.<br/>In writeable HTTP message editor, it replaces the selected string; otherwise, it displays the returned string in a popup.                                                                                                                    |
| REQUEST          | Accepts an [HttpRequest](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html) parameter with no return value，used when only an HTTP request is needed.                                                                                                  |
| REQUEST_RESPONSE | Accepts an [HttpRequestResponse](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/HttpRequestResponse.html) parameter with no return value, used when both HTTP request and response are needed.                                                                               |
| MESSAGE_EDITOR   | Accepts a [MessageEditorHttpRequestResponse](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/ui/contextmenu/MessageEditorHttpRequestResponse.html) parameter. This is used when modifying content in HTTP message editor is required; typically, using `SELECTED_TEXT` is more convenient. |

The following code demonstrates how to register a `purify headers` context menu item that removes extraneous request headers from an HTTP message editor request:
```python
def removeBoringHeaders(editor):
    request = editor.requestResponse().request()
    editor.setRequest(request.withRemovedHeader("Sec-Ch-Ua")\
                             .withRemovedHeader("Sec-Ch-Ua-Mobile")\
                             .withRemovedHeader("Sec-Ch-Ua-Platform")\
                             .withRemovedHeader("Sec-Fetch-Site")\
                             .withRemovedHeader("Sec-Fetch-Mode")\
                             .withRemovedHeader("Sec-Fetch-Dest")\
                             .withRemovedHeader("Priority")
                             )

def registerContextMenu(menus):
    menus.register("purify headers", removeBoringHeaders, MenuType.MESSAGE_EDITOR)
```
When using `MESSAGE_EDITOR` as the menu type, the following two utility methods are also available:
* getSelectedText([editor](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/ui/contextmenu/MessageEditorHttpRequestResponse.html)), Retrieves the selected text, returning a [ByteArray](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/ByteArray.html) object.
* replaceSelectedText([editor](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/ui/contextmenu/MessageEditorHttpRequestResponse.html), "new string")，Replaces the selected text in editor, returning an [HttpRequest](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/http/message/requests/HttpRequest.html) object.

For more examples, please refer to [customise_context_menu.py](./src/main/resources/examples/customise_context_menu.py).

###  RPC
PyBurp allows calling methods provided by other programs via gRPC. You need to implement the service interfaces defined in [burpextender.proto](https://github.com/cyal1/pyburpRPC/blob/main/burpextender.proto) for these calls.。

For Python users, the related service interfaces are already implemented in [pyburp](https://github.com/cyal1/pyburpRPC/). You can install and use this library via `pip install pyburp`.  
For other programming languages, generate code from the [burpextender.proto](https://github.com/cyal1/pyburpRPC/blob/main/burpextender.proto) file and implement the corresponding service interfaces.

Here’s a Python demonstration:

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
1. PyBurp supports only the following parameter types: `str`,`bool`,`int`,`float`,`bytes`.
2. If the server-side exposed method has **only one parameter and it is of type bytes**，in PyBurp, you need to wrap the parameter using [bytearray](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/ByteArray.html#byteArray(java.lang.String)) or place it in `[]`; otherwise, `byte[]` will be treated as a variable-length argument array, with each byte being treated as an individual argument.
3. The `bytes` type returned by the server is of type [array.array('b',initializer)](https://www.jython.org/jython-old-sites/docs/library/array.html#array-efficient-arrays-of-numeric-values) in PyBurp，It can be converted to a string using `tostring()` or to a [ByteArray](https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/core/ByteArray.html) using the `bytearray` method.

## Contributions
We welcome contributions from the community for improvements, issue reporting, or code contributions to PyBurp.

## Questions
1. Why are some Python libraries or methods not available in PyBurp？  
   PyBurp uses the Jython interpreter rather than standard CPython, so not all third-party Python libraries are compatible. However, it can seamlessly access Java libraries.

## Acknowledgements
Much of PyBurp’s inspiration comes from [Turbo Intruder](https://github.com/PortSwigger/turbo-intruder/)


