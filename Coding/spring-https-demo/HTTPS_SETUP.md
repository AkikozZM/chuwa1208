# Spring Security HTTPS Setup with Self-Signed Certificate

## Overview
This document explains how to set up a Spring Security application with HTTPS using a self-signed certificate, and how to test it properly without bypassing TLS/SSL verification.

## Prerequisites
- Java 17 or higher
- Maven 3.6+
- OpenSSL (for certificate generation)
- Keytool (comes with JDK)

## Step 1: Generate Self-Signed Certificate and JKS Keystore

### On Windows:
```bash
generate-keystore.bat
```

### On Linux/Mac:
```bash
chmod +x generate-keystore.sh
./generate-keystore.sh
```

### Manual Steps (if scripts don't work):

1. **Generate private key and certificate:**
```bash
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes \
  -subj "/C=US/ST=State/L=City/O=Chuwa/CN=localhost"
```

2. **Convert to PKCS12 format:**
```bash
openssl pkcs12 -export -in cert.pem -inkey key.pem -out keystore.p12 \
  -name spring-https-demo -password pass:changeit
```

3. **Convert PKCS12 to JKS:**
```bash
keytool -importkeystore -srckeystore keystore.p12 -srcstoretype PKCS12 \
  -srcstorepass changeit -destkeystore keystore.jks -deststoretype JKS \
  -deststorepass changeit -destkeypass changeit
```

4. **Move keystore to resources:**
```bash
# Windows
move keystore.jks src\main\resources\

# Linux/Mac
mv keystore.jks src/main/resources/
```

## Step 2: Application Configuration

The application is configured in `application.properties`:
- **Port**: 8443 (HTTPS)
- **Keystore**: `classpath:keystore.jks`
- **Password**: `changeit`
- **Alias**: `spring-https-demo`

## Step 3: Running the Application

```bash
mvn spring-boot:run
```

The application will start on `https://localhost:8443`

## Step 4: Testing HTTPS API

### Test Endpoint
- **URL**: `https://localhost:8443/api/test`
- **Method**: GET
- **Response**: 200 OK (empty body)

### Testing Without Importing Certificate

#### Why it won't work without importing:
When you make an HTTPS request to a server with a self-signed certificate, the client (browser, Postman, curl) cannot verify the certificate because:
1. The certificate is not signed by a trusted Certificate Authority (CA)
2. The certificate is not in the client's trust store
3. The client cannot verify the certificate chain

**This is expected behavior** - it's a security feature, not a bug. Self-signed certificates are not trusted by default because anyone can create them.

#### Testing Options:

**Option 1: Import Certificate to System Trust Store (Recommended for Development)**

**Windows:**
1. Export the certificate from JKS:
```bash
keytool -export -alias spring-https-demo -keystore src/main/resources/keystore.jks \
  -file cert.cer -storepass changeit
```
2. Import to Windows Trust Store:
   - Double-click `cert.cer`
   - Click "Install Certificate"
   - Choose "Current User" or "Local Machine"
   - Select "Place all certificates in the following store"
   - Click "Browse" → Select "Trusted Root Certification Authorities"
   - Click "Next" → "Finish"

**Linux:**
```bash
# Export certificate
keytool -export -alias spring-https-demo -keystore src/main/resources/keystore.jks \
  -file cert.cer -storepass changeit

# Convert to PEM
openssl x509 -inform DER -in cert.cer -out cert.pem

# Copy to system trust store (requires sudo)
sudo cp cert.pem /usr/local/share/ca-certificates/spring-https-demo.crt
sudo update-ca-certificates
```

**Mac:**
```bash
# Export certificate
keytool -export -alias spring-https-demo -keystore src/main/resources/keystore.jks \
  -file cert.cer -storepass changeit

# Import to Keychain
sudo security add-trusted-cert -d -r trustRoot -k /Library/Keychains/System.keychain cert.cer
```

**Option 2: Use curl with Certificate File**

```bash
# Export certificate first
keytool -export -alias spring-https-demo -keystore src/main/resources/keystore.jks \
  -file cert.cer -storepass changeit

# Convert to PEM
openssl x509 -inform DER -in cert.cer -out cert.pem

# Use with curl
curl --cacert cert.pem https://localhost:8443/api/test
```

**Option 3: Postman - Import Certificate**

1. Export certificate (as shown above)
2. In Postman:
   - Go to Settings → Certificates
   - Add Certificate
   - Host: `localhost`
   - CRT file: Select the exported certificate file
   - Key file: (not needed for server certificate)
   - Passphrase: (leave empty)

**Option 4: Java Application Trust Store**

If testing from another Java application:
```bash
keytool -import -alias spring-https-demo -file cert.cer \
  -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit
```

## Step 5: What We Did to Make HTTPS Work

1. **Generated Self-Signed Certificate**: Created a certificate using OpenSSL with RSA 4096-bit encryption, valid for 365 days, with CN=localhost.

2. **Converted to JKS Format**: 
   - First converted to PKCS12 format (industry standard)
   - Then converted to JKS format (Java KeyStore) which Spring Boot can use

3. **Configured Spring Boot**: 
   - Set `server.ssl.key-store` to point to the JKS file
   - Configured the keystore password and alias
   - Set server port to 8443 (standard HTTPS port)

4. **Configured Spring Security**:
   - Enabled HTTPS requirement for all requests
   - Disabled CSRF for API endpoints (optional, for simplicity)

5. **Created API Endpoint**: Simple GET endpoint that returns empty 200 OK response

## Important Notes

- **Self-signed certificates are NOT secure for production** - they should only be used for development/testing
- **Never bypass TLS/SSL verification** in production code - this defeats the purpose of HTTPS
- **For production**, use certificates from trusted Certificate Authorities (CA) like Let's Encrypt, DigiCert, etc.
- The certificate is valid for `localhost` only - if you need to test from other machines, you'll need to generate a certificate with the appropriate hostname or IP

## Troubleshooting

### Certificate verification failed
- Make sure you've imported the certificate to your system trust store
- Verify the certificate is valid: `keytool -list -v -keystore src/main/resources/keystore.jks -storepass changeit`

### Connection refused
- Check if the application is running on port 8443
- Verify firewall settings

### Wrong password
- Default password is `changeit` as configured in `application.properties`
- To change it, update both the keystore password and `application.properties`
