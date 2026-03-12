# Configuration Setup Guide

This project uses externalized configuration for security. All sensitive values (credentials, infrastructure details, certificates) are stored outside the codebase.

## Quick Start

### 1. Copy External Configuration Template
```bash
# Copy the template to your config directory
cp application-external.properties.template C:\Workspaces\adUsersConfig\application-external.properties
```

### 2. Fill in Configuration Values
Edit `C:\Workspaces\adUsersConfig\application-external.properties` and replace all `YOUR_*` placeholders with actual values:

- `YOUR_LDAP_SERVER` - Your LDAP server address
- `YOUR_DOMAIN` - Your Active Directory domain
- `YOUR_SERVICE_ACCOUNT` - Service account for LDAP binding
- `YOUR_SERVICE_ACCOUNT_PASSWORD` - Service account password
- `YOUR_TRUSTSTORE_PASSWORD` - Password for the trust.p12 file

### 3. Copy Certificate File
```bash
# Ensure trust.p12 is in the config directory
# (You already have this at C:\Workspaces\adUsersConfig\trust.p12)
```

### 4. Set Environment Variable
Set a single environment variable pointing to your config directory:

**Windows (Command Prompt):**
```cmd
set CONFIG_PATH=C:\Workspaces\adUsersConfig\
```

**Windows (PowerShell):**
```powershell
$env:CONFIG_PATH="C:\Workspaces\adUsersConfig\"
```

**Linux/Mac:**
```bash
export CONFIG_PATH=/path/to/config/
```

### 5. Run the Application
```bash
mvn spring-boot:run
# OR
java -jar target/ad-users-main-*.jar
```

## Configuration Structure

```
C:\Workspaces\adUsersConfig\
├── application-external.properties    # All configuration values
└── trust.p12                         # SSL truststore certificate
```

## Important Security Notes

- ⚠️ **Never commit `application-external.properties` to GitHub** - it contains passwords and secrets
- ⚠️ **Never commit the `trust.p12` file to GitHub** - it's already externalized
- ✓ The template file `application-external.properties.template` is safe to commit (no secrets)
- ✓ Use `.gitignore` entries to prevent accidental commits of config files

## Environment-Specific Configurations

For different environments (dev, test, prod), create separate config files:

```
C:\Workspaces\adUsersConfig\
├── application-external.properties       # Default/dev config
├── application-external-test.properties  # Test environment
└── application-external-prod.properties  # Production environment
```

To use a specific config, modify the `spring.config.additional-location` property or change the `CONFIG_PATH` environment variable.

## Loading External Configuration

The application automatically loads the external configuration file based on:

```properties
# From src/main/resources/application.properties
spring.config.additional-location=file:${CONFIG_PATH:./config/}application-external.properties
```

This means:
- If `CONFIG_PATH` environment variable is set, use that path
- Otherwise, fall back to `./config/` directory relative to application startup

## Troubleshooting

### Config file not found
```
ERROR: application-external.properties not found
```
**Solution:** Ensure `CONFIG_PATH` environment variable is set correctly and the file exists at that location.

### Certificate validation fails
```
ERROR: PKCS12 keystore error
```
**Solution:** Verify the `trust.p12` file path and password are correct in `application-external.properties`.

### LDAP connection fails
```
ERROR: Failed to bind
```
**Solution:** Check that LDAP credentials and server addresses in `application-external.properties` are correct.

## File Permissions (Linux/Mac)

For production security, restrict file permissions:
```bash
chmod 600 C:\Workspaces\adUsersConfig\application-external.properties
chmod 600 C:\Workspaces\adUsersConfig\trust.p12
```

This ensures only the application owner can read sensitive configuration files.
