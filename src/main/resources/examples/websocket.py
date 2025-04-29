# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/websocket/TextMessage.html

def handleWsTextMsg(textMsg):
    if textMsg.direction() == CLIENT_TO_SERVER and 'password' in textMsg.payload():
        print(type(textMsg.payload()), textMsg.payload().decode())
        return base64encode(textMsg.payload().decode())
    else:  # SERVER_TO_CLIENT
        pass
    return textMsg.payload().decode()


"""

# https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/proxy/websocket/TextMessageReceivedAction.html
def handleProxyWsTextMsg(textMsg):
    if 'password' in textMsg.payload():
        textMsg.annotations().setHighlightColor(HighlightColor.RED)
    if textMsg.direction() == CLIENT_TO_SERVER and 'password' in textMsg.payload():
        return TextMessageReceivedAction.intercept(textMsg)
    return TextMessageReceivedAction.continueWith(textMsg)

"""

