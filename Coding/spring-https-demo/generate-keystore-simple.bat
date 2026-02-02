@echo off
REM Simple script to generate JKS keystore using only keytool (no OpenSSL required)
REM This script generates a keystore.jks file that can be used by Spring Boot

echo Generating self-signed certificate and JKS keystore using keytool...

REM Generate keystore with self-signed certificate directly
keytool -genkeypair -alias spring-https-demo -keyalg RSA -keysize 4096 -storetype JKS ^
  -keystore src\main\resources\keystore.jks -validity 365 -storepass changeit ^
  -keypass changeit -dname "CN=localhost, OU=Chuwa, O=Chuwa, L=City, ST=State, C=US"

echo.
echo Keystore generated successfully at src\main\resources\keystore.jks
echo Password: changeit
echo Alias: spring-https-demo
echo.
echo You can now run the application with: mvn spring-boot:run
echo The application will be available at: https://localhost:8443/api/test
