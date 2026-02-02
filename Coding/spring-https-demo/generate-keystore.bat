@echo off
REM Script to generate self-signed certificate and convert to JKS format for Windows
REM This script generates a keystore.jks file that can be used by Spring Boot

echo Generating self-signed certificate and JKS keystore...

REM Generate a private key and certificate
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes -subj "/C=US/ST=State/L=City/O=Chuwa/CN=localhost"

REM Convert to PKCS12 format (required intermediate step)
openssl pkcs12 -export -in cert.pem -inkey key.pem -out keystore.p12 -name spring-https-demo -password pass:changeit

REM Convert PKCS12 to JKS format
keytool -importkeystore -srckeystore keystore.p12 -srcstoretype PKCS12 -srcstorepass changeit -destkeystore keystore.jks -deststoretype JKS -deststorepass changeit -destkeypass changeit

REM Move keystore to resources directory
move keystore.jks src\main\resources\

REM Clean up temporary files
del key.pem cert.pem keystore.p12

echo.
echo Keystore generated successfully at src\main\resources\keystore.jks
echo Password: changeit
echo Alias: spring-https-demo
