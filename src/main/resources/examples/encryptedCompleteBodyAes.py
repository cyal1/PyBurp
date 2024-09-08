# https://docs.oracle.com/en/java/javase/17/docs/api/java.base/javax/crypto/Cipher.html
# https://docs.oracle.com/en/java/javase/17/docs/specs/security/standard-names.html
from javax.crypto import Cipher
from javax.crypto.spec import SecretKeySpec, IvParameterSpec


def create_cipher(key, iv, mode):
    key_spec = SecretKeySpec(key, 'AES')
    cipherTransformation = "AES/CBC/PKCS5Padding"
    cipher = Cipher.getInstance(cipherTransformation)
    if iv is None:
        cipher.init(mode, key_spec)
    else:
        cipher.init(mode, key_spec, IvParameterSpec(iv))
    return cipher


def encrypt(plain_text, key, iv):
    cipher = create_cipher(key, iv, Cipher.ENCRYPT_MODE)
    plain_text_bytes = bytestring(plain_text)
    encrypted_bytes = cipher.doFinal(plain_text_bytes)
    return encrypted_bytes


def decrypt(encrypted_bytes, key, iv):
    cipher = create_cipher(key, iv, Cipher.DECRYPT_MODE)
    decrypted_bytes = cipher.doFinal(encrypted_bytes)
    return decrypted_bytes


key, iv = 'aaaaaaaaaaaaaaaa', 'aaaaaaaaaaaaaaaa'

# curl -XGET -x http://127.0.0.1:8080 -d 'rny6gpOl+IVUoGM/4ej44g==' 'https://httpbin.org/base64/Z05JK1hhcVdHMXVRcUlGWXM1aWVvMFRqQ0oyYUxSay9FN0t0eUlTQXZWbz0='

def urlPrefixAllowed(urls):
    urls.add("https://httpbin.org/base64/")


def handleRequest(request, annotations):
    print("handleRequest", request.bodyToString())
    result = encrypt(request.bodyToString().replace("world", "PyBurp"), key, iv)
    return request.withBody(base64encode(bytearray(result))), annotations


def handleProxyRequest(request, annotations):
    print("handleProxyRequest", request.bodyToString())
    result = decrypt(base64decode(request.body()).getBytes(), key, iv)
    return request.withBody(bytearray(result)), annotations


def handleResponse(response, annotations):
    print("handleResponse", response.bodyToString())
    result = decrypt(base64decode(response.body()).getBytes(), key, iv)
    return response.withBody(bytearray(result)), annotations


def handleProxyResponse(response, annotations):
    print("handleProxyResponse", response.bodyToString())
    result = encrypt(response.bodyToString(), key, iv).tostring()
    return response.withBody(base64encode(result)), annotations


def enc(text):
    return base64encode(encrypt(text, key, iv).tostring())

def dec(text):
    return decrypt(base64decode(text).getBytes(), key, iv).tostring()

def registerContextMenu(menus):
    menus.register("aes encrypt", enc, MenuType.SELECTED_TEXT)
    menus.register("aes decrypt", dec, MenuType.SELECTED_TEXT)

