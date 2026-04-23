import json
import BaseHTTPServer
import threading

# ===== 工具注册表 =====
tools = {}

def register_tool(name, func, desc, schema):
    tools[name] = {"func": func, "desc": desc, "inputSchema": schema}


# ===== 示例工具 =====
def add(a, b):
    return a + b

register_tool("add", add, "返回两个数字的和", {"type": "object", "properties": {"a": {"type": "integer", "description": "第一个数字"}, "b": {"type": "integer", "description": "第二个数字"}},"required": ["a", "b"]})

# ===== HTTP 处理器 =====
class Handler(BaseHTTPServer.BaseHTTPRequestHandler):
    def log_message(self, format, *args):
        pass
    def log_request(self, code='-', size='-'):
        pass

    def do_POST(self):
        if self.path != "/mcp":
            self.respond(404, -555, {"error": "Not found"})
            return
        try:
            length = int(self.headers.get("Content-Length", 0))
            body = self.rfile.read(length)
            req = json.loads(body)
            print("request: ",json.dumps(req, ensure_ascii=False))
            method = req.get("method", "no method in body.")
            req_id = req.get("id")
            protocolVersion = req.get("protocolVersion", "2025-06-18")
            params = req.get("params", {})
            if method == "initialize":
                self.respond(200, req_id, {"result": {"serverInfo": {"name": "python", "version": "1.11.0" }, "protocolVersion": protocolVersion, "capabilities": { "prompts": { "listChanged": False }, "resources": {"subscribe": False, "listChanged": False}, "tools": {"listChanged": False}}}})

            # tools/list
            elif method == "tools/list":
                self.respond(200, req_id, {"result": {
                    "tools": [{"name": name,"description": tool["desc"],"inputSchema": tool["inputSchema"],"outputSchema": {
                        "properties": {
                            "result": {
                                "type": "object"
                            }
                        },
                        "required": [
                            "result"
                        ],
                        "type": "object"
                    } # https://medium.com/@lizhuohang.selina/model-context-protocol-mcp-architecture-workflow-and-sample-payloads-de17230f9633
                               } for name, tool in tools.items()]
                }} )

            # tools/call
            elif method == "tools/call":
                tool_name = params.get("name", "UNKNOW")
                args = params.get("arguments", {})
                if tool_name not in tools:
                    self.respond(200, req_id, {"error": {"code": -555,"message": "Tool not found: " + tool_name}})
                    return
                try:
                    result = tools[tool_name]["func"](**args)
                    result = {"content": [{"type": "text", "text": str(result)}]}
                    self.respond(200, req_id, {"result": result})
                except Exception as e:
                    self.respond(200, req_id, {"error": {"code": -555,"message": str(e)}})
            else:
                self.respond(200, req_id, {"error": {"code": -555,"message": "Method not supported: " + method}})
        except Exception as e:
            self.respond(400, "", {"error": str(e)})
        print("-" * 10)

    def respond(self, code, req_id, data):
        self.send_response(code)
        data.update({"jsonrpc": "2.0", "id": req_id})
        self.send_header("Content-Type", "application/json; charset=utf-8")
        body = json.dumps(data, ensure_ascii=False)
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        print("response: ", body)
        self.wfile.write(body)

# ===== MCPServer =====
class MCPServer:
    def __init__(self, host="localhost", port=8000):
        self.host = host
        self.port = port
        self.server = None
    def start(self):
        """启动服务器（在后台线程）"""
        self.server = BaseHTTPServer.HTTPServer((self.host, self.port), Handler)
        self.thread = threading.Thread(target=self.server.serve_forever)
        self.thread.daemon = True
        self.thread.start()
        print("✅ MCP server started on http://{}:{}".format(self.host, self.port))
    def stop(self):
        """停止服务器"""
        if self.server is not None:
            print("🛑 Shutting down MCP server...")
            self.server.shutdown()
            self.server.server_close()
            self.thread.join()
            print("MCP Server stopped.")
# ===== 启动 =====
if __name__ == "__main__":
    mcp_server = MCPServer(port=8000)
    mcp_server.start()


def finish():
    mcp_server.stop()

