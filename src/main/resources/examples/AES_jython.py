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
    return str(base64.b64encode(aes.encrypt(data)))

def aes_encrypt_cbc(data, key, iv):
    bs = AES.block_size
    pad = lambda s: s + (bs - len(s) % bs) * chr(bs - len(s) % bs)
    cipher = AES.new(key, AES.MODE_CBC, iv)
    data = cipher.encrypt(pad(data))
    return b64encode(data)

def aes_decrypt_cbc(data, key, iv):
    decipher = AES.new(key, AES.MODE_CBC, iv)
    plaintext = decipher.decrypt(b64decode(data))
    return plaintext


def urlPrefixAllowed(urls):
    urls.add("https://www.example.com/api/")


def handleRequest(request, annotations):
    return request, annotations


def handleProxyRequest(request, annotations):
    return request, annotations

def handleResponse(response, annotations):
    body = aes_decrypt_cbc("V275hhZ6+Ix3fg7ERcM5Jw==", "aaaaaaaaaaaaaaaa", "aaaaaaaaaaaaaaaa")
    return response.withBody(body), annotations


def handleProxyResponse(response, annotations):
    return response, annotations
