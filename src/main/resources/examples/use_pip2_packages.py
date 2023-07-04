import sys

"""
If we want to use packages installed by PIP, add the local PIP install directory to the sys.path of Jython.

Run the command `python2 -c "import json; import sys; print json.dumps(sys.path)"` on your computer, and replace PIP_PATH below with the result.

Note: Not all PyPI packages can run in Jython, especially those contain C code.
Try to import the package you want to use in Jython shell and test its functions before going further.
"""

PIP_PATH = ["/Library/Python/2.7/site-packages"]

for path in PIP_PATH:
    if path not in sys.path:
        sys.path.append(path)

import requests

# Using a third-party networking library (rather than Burp's) to perform the request, the Logger does not record.
resp = requests.get("https://www.example.com/")
print(resp.text)
