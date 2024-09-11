

def processPayload(payload):
    return base64encode(payload)


# process payload with remote function
"""
server = rpc("localhost", 30051)

def processPayload(payload):
    return server.callFunc('encrypt', payload)

def finish():
    server.shutdown()
"""

