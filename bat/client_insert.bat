keytool -import -alias ssl.demo.com -keystore cacerts -file C:\Development\deployment\ssl\ca-demo\server.crt

keytool -keystore demo.truststore -keypass demodemo -storepass demodemo -alias DemoCA -import -trustcacerts -file ca.cer

REM keytool -list -v -keystore demo.truststore