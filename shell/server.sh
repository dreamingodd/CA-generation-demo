#!/bin/bash
# 签发服务器证书
mkdir server
openssl genrsa -out ./server/server.key
openssl req -new -key ./server/server.key -out ./server/server.csr
openssl ca -in ./server/server.csr -cert ./ca.crt -keyfile ./private/cakey.pem -out ./server/server.crt -days 3650
