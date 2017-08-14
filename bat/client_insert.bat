keytool -import -alias ssl.demo.com -keystore cacerts -file C:\Development\deployment\ssl\ca-demo\server.crt

keytool -keystore demo.truststore -keypass 012345 -storepass 012345 -alias DemoCA -import -trustcacerts -file DemoCA.cer

REM keytool -list -v -keystore demo.truststore