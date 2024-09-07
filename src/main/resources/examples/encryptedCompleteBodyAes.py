import sys
from base64 import b64decode, b64encode

# need run `git clone https://github.com/csm/jycrypto.git` command on `/tmp/` directory
pycryptoLib = "/tmp/jycrypto/lib"

if pycryptoLib not in sys.path:
    sys.path.append(pycryptoLib)

from Crypto.Cipher import AES

def aes_decrypt_ecb(data, key):
    aes = AES.new(key, AES.MODE_ECB)
    decrypted_text = aes.decrypt(b64decode(data))
    decrypted_text = decrypted_text[:-ord(decrypted_text[-1])]
    return decrypted_text

def aes_encrypt_ecb(data, key):
    while len(data) % 16 != 0:
        data += (16 - len(data) % 16) * chr(16 - len(data) % 16)
    data = str.encode(data)
    aes = AES.new(str.encode(key), AES.MODE_ECB)
    return b64encode(aes.encrypt(data))

def aes_encrypt_cbc(data, key, iv):
    while len(data) % 16 != 0:
        data += (16 - len(data) % 16) * chr(16 - len(data) % 16)
    cipher = AES.new(key, AES.MODE_CBC, iv)
    data = cipher.encrypt(data)
    return b64encode(data)

def aes_decrypt_cbc(data, key, iv):
    decipher = AES.new(key, AES.MODE_CBC, iv)
    plaintext = decipher.decrypt(b64decode(data))
    return plaintext


key, iv = "aaaaaaaaaaaaaaaa", "aaaaaaaaaaaaaaaa"


def urlPrefixAllowed(urls):
    urls.add("https://httpbin.org/base64/")

# curl -XGET -x http://127.0.0.1:8080 -d 'rny6gpOl+IVUoGM/4ej44g==' 'https://httpbin.org/base64/Z05JK1hhcVdHMXVRcUlGWXM1aWVvMFRqQ0oyYUxSay9FN0t0eUlTQXZWbz0='

def handleRequest(request, annotations):
    print("handleRequest", request.bodyToString())
    result = aes_encrypt_cbc(request.bodyToString().replace("world", "PyBurp"), key, iv)
    return request.withBody(result), annotations


def handleProxyRequest(request, annotations):
    print("handleProxyRequest", request.bodyToString())
    result = aes_decrypt_cbc(request.bodyToString(), key, iv)
    return request.withBody(result), annotations


def handleResponse(response, annotations):
    print("handleResponse", response.bodyToString())
    result = aes_decrypt_cbc(response.bodyToString(), key, iv)
    return response.withBody(result), annotations


def handleProxyResponse(response, annotations):
    print("handleProxyResponse", response.bodyToString())
    result = aes_encrypt_cbc(response.bodyToString(), key, iv)
    return response.withBody(result), annotations

