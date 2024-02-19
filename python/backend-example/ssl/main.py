'''
This code shows a basic backend example for sending requests to our servers using SSL
For POST requests we have included gathering a JWT, this may not be required. 
You will provide your PRIVATE, PUBLIC and DIGITAL cert.
You can obtain these values following this guide: https://developer.payments.jpmorgan.com/quick-start

Note this is not production code and is supplied to get developers started 
'''
import requests
import signal
import sys
import sys
import json
from dotenv import load_dotenv

import os
sys.path.append('../../sample-authentication-code')
from getJWT import generate_digital_signature
load_dotenv()
from http.server import BaseHTTPRequestHandler, HTTPServer

url = "https://api-mock.payments.jpmorgan.com"

digital_key=os.getenv('DIGITAL_KEY')
public_cert=os.getenv("PUBLIC")
private_cert=os.getenv("PRIVATE")

class ApiProxy:
    def start_server(self):
        class ProxyHTTPRequestHandler(BaseHTTPRequestHandler):
            protocol_version = "HTTP/1.0"

            def do_GET(self):
                print("Received GET request")  # Debugging statement
                self._handle_request("get",requests.get)

            def do_DELETE(self):
                print("Received DELETE request")  # Debugging statement
                self._handle_request("delete", requests.delete)

            def do_POST(self):
                print("Received POST request")  # Debugging statement
                self._handle_request("post", requests.post)

            def do_PUT(self):
                print("Received PUT request")  # Debugging statement
                self._handle_request("put", requests.put)

            def do_PATCH(self):
                print("Received PATCH request")  # Debugging statement
                self._handle_request("patch", requests.patch)

            def _handle_request(self, method,requests_func):
                final_url = url + self.path
                print("Final URL:", final_url) 
                headers = dict(self.headers)

                if method == "post":
                    data = self.rfile.read(int(self.headers["content-length"])).decode('UTF-8')
                    # This can be removed if not requiring a digital signature
                    encoded_data = generate_digital_signature(digital_key, json.loads(data))
                    resp = requests_func(final_url, data=encoded_data, headers=headers, cert=(public_cert, private_cert))

                else:
                    resp = requests_func(final_url, headers=headers, cert=(public_cert, private_cert))
                self.send_response(resp.status_code)
                for key in resp.headers:
                    self.send_header(key, resp.headers[key])
                self.end_headers()
                try:
                    self.wfile.write(resp.content)
                    self.wfile.flush()
                except Exception as e:
                    print("Exception while writing response body:", e)


        server_address = ('', 8001)
        self.httpd = HTTPServer(server_address, ProxyHTTPRequestHandler)
        print('proxy server is running')
        self.httpd.serve_forever()


def exit_now(signum, frame):
    sys.exit(0)

if __name__ == '__main__':
    proxy = ApiProxy()
    signal.signal(signal.SIGTERM, exit_now)
    proxy.start_server()