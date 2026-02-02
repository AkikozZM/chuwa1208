# Spring Security HTTPS Demo

A Spring Boot application demonstrating HTTPS setup with a self-signed certificate.

## Quick Start

### 1. Generate Keystore

**Windows:**
```bash
generate-keystore-simple.bat
```

**Linux/Mac:**
```bash
chmod +x generate-keystore-simple.sh
./generate-keystore-simple.sh
```

**Manual (using keytool):**
```bash
keytool -genkeypair -alias spring-https-demo -keyalg RSA -keysize 4096 -storetype JKS \
  -keystore src/main/resources/keystore.jks -validity 365 -storepass changeit \
  -keypass changeit -dname "CN=localhost, OU=Chuwa, O=Chuwa, L=City, ST=State, C=US"
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

### 3. Test the API

The application will be available at: `https://localhost:8443/api/test`

**Using curl (after importing certificate):**
```bash
curl https://localhost:8443/api/test
```

**Using Postman:**
1. Import the certificate (see HTTPS_SETUP.md for details)
2. Make GET request to `https://localhost:8443/api/test`

## Project Structure

```
spring-https-demo/
├── src/
│   └── main/
│       ├── java/com/chuwa/httpsdemo/
│       │   ├── SpringHttpsDemoApplication.java
│       │   ├── config/
│       │   │   └── SecurityConfig.java
│       │   └── controller/
│       │       └── ApiController.java
│       └── resources/
│           ├── application.properties
│           └── keystore.jks (generated)
├── pom.xml
├── generate-keystore-simple.bat
├── generate-keystore-simple.sh
└── HTTPS_SETUP.md
```

## Important Notes

- **Keystore Password**: `changeit`
- **Certificate Alias**: `spring-https-demo`
- **Port**: 8443 (HTTPS)
- The self-signed certificate must be imported to your system trust store or Postman to avoid certificate verification errors
- See `HTTPS_SETUP.md` for detailed explanation of the setup and testing process

## API Endpoint

- **URL**: `https://localhost:8443/api/test`
- **Method**: GET
- **Response**: 200 OK (empty body)
