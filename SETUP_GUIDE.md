# AD Users Application - Setup Guide

This guide explains how to set up and run the AD Users application with externalized configuration (passwords, LDAP credentials, and other sensitive data kept outside the codebase).

## Quick Start (5 minutes)

### Step 1: Create Config Directory
Create a directory to store your configuration files:
```
Windows (Command Prompt):
mkdir C:\Workspaces\adUsersConfig

Windows (PowerShell):
New-Item -ItemType Directory -Path C:\Workspaces\adUsersConfig

Mac/Linux:
mkdir -p ~/adUsersConfig
```

### Step 2: Copy and Fill Configuration Template
Copy the template to your config directory:
```
Windows:
copy application-external.properties.template C:\Workspaces\adUsersConfig\application-external.properties

Mac/Linux:
cp application-external.properties.template ~/adUsersConfig/application-external.properties
```

Edit `C:\Workspaces\adUsersConfig\application-external.properties` (or `~/adUsersConfig/` on Mac/Linux) and replace all placeholders:
- `SPRING_LDAP_URLS` - Your LDAP server (e.g., `ldaps://your-ldap-server:636`)
- `SPRING_LDAP_BASE` - Your base DN (e.g., `DC=yourcompany,DC=com`)
- `SPRING_LDAP_USERNAME` - Service account CN
- `SPRING_LDAP_PASSWORD` - Service account password
- Other configuration values as needed

### Step 3: Place Certificate File
Copy your `trust.p12` file to the config directory:
```
C:\Workspaces\adUsersConfig\trust.p12    (Windows)
~/adUsersConfig/trust.p12                (Mac/Linux)
```

### Step 4: Set Environment Variable
Set the `CONFIG_PATH` environment variable to point to your config directory.

**Windows (Command Prompt) - Temporary (current session only):**
```cmd
set CONFIG_PATH=C:\Workspaces\adUsersConfig\
```

**Windows (PowerShell) - Temporary (current session only):**
```powershell
$env:CONFIG_PATH = "C:\Workspaces\adUsersConfig\"
```

**Windows (Permanent) - Set in System Environment Variables:**
1. Press `Windows Key + X` → Select "System"
2. Click "Advanced system settings"
3. Click "Environment Variables"
4. Click "New" under "User variables"
5. Variable name: `CONFIG_PATH`
6. Variable value: `C:\Workspaces\adUsersConfig\`
7. Click OK and restart your terminal

**Mac/Linux (Temporary - current session only):**
```bash
export CONFIG_PATH=~/adUsersConfig/
```

**Mac/Linux (Permanent - add to ~/.bashrc or ~/.zshrc):**
```bash
export CONFIG_PATH=~/adUsersConfig/
```
Then run: `source ~/.bashrc` (or `~/.zshrc`)

### Step 5: Run the Application

**Option A: Maven (Recommended for development)**
```bash
mvn clean spring-boot:run
```

**Option B: Build and run JAR**
```bash
# Build the application
mvn clean package

# Run the JAR file
java -jar target/ad-users-demo-0.0.1-SNAPSHOT.jar
```

**Option C: IDE (IntelliJ IDEA, VS Code, Eclipse)**
- Ensure environment variable is set before starting the IDE
- Run/Debug the application normally from the IDE

## Configuration Directory Structure

Your config directory should look like this:
```
C:\Workspaces\adUsersConfig\
├── application-external.properties    ← Configuration values (NEVER commit)
└── trust.p12                         ← SSL certificate (NEVER commit)
```

## Troubleshooting

### Problem: "application-external.properties not found"
**Cause:** CONFIG_PATH environment variable is not set or pointing to wrong location

**Solution:**
1. Verify CONFIG_PATH is set:
   ```cmd
   echo %CONFIG_PATH%              (Windows)
   echo $CONFIG_PATH               (Mac/Linux)
   ```
2. Ensure the file exists at that path
3. Restart your terminal/IDE after setting the variable

### Problem: "Could not resolve placeholder in string"
**Cause:** Properties in `application-external.properties` have wrong format

**Solution:**
1. Check that property names match Spring Boot format (e.g., `spring.ldap.username`)
2. Ensure all values are filled in (no `YOUR_*` placeholders remain)
3. Check for typos in property names

### Problem: SSL Certificate verification fails
**Cause:** Trust store not found or password incorrect

**Solution:**
1. Verify `trust.p12` exists in CONFIG_PATH directory
2. Check `spring.ldap.ssl.trust-store-password` matches the certificate password
3. Verify path format: `file:C:\path\to\trust.p12` (absolute path)

### Problem: LDAP connection fails
**Cause:** Incorrect LDAP credentials or server URL

**Solution:**
1. Verify `spring.ldap.urls` format: `ldaps://server.domain:636`
2. Test credentials with LDAP client or AD admin
3. Ensure service account has LDAP read permissions

## Security Best Practices

### ✓ DO:
- Keep `application-external.properties` **only on your local machine**
- Use `.gitignore` to prevent accidental commits (already configured)
- Use strong passwords for service accounts
- Rotate credentials regularly
- Use separate config files for different environments

### ✗ DON'T:
- Never commit `application-external.properties` to Git
- Never commit `trust.p12` to Git
- Never share config files via email or chat
- Never hardcode credentials in the application code
- Never commit the template file with filled-in values

## Using Multiple Environments

For different environments (dev, test, production), create separate config files:

**Config directory structure:**
```
C:\Workspaces\adUsersConfig\
├── application-external.properties       ← Default/local dev
├── application-external-test.properties  ← Test environment
└── application-external-prod.properties  ← Production environment
```

**To use a specific environment:**

Option 1: Change CONFIG_PATH to point to environment-specific config
```cmd
set CONFIG_PATH=C:\Workspaces\adUsersConfig-prod\
```

Option 2: Modify the property name in application.properties (for advanced usage)
```properties
spring.config.additional-location=file:${CONFIG_PATH:./config/}application-external-prod.properties
```

## File Structure and Purpose

### Committed to Git (Safe):
- ✓ `src/main/resources/application.properties` - Minimal, no secrets
- ✓ `application-external.properties.template` - Template with placeholders
- ✓ `SETUP_GUIDE.md` - This file (instructions only)
- ✓ `.gitignore` - Excludes secret files

### NOT committed to Git (Contains Secrets):
- ✗ `C:\Workspaces\adUsersConfig\application-external.properties`
- ✗ `C:\Workspaces\adUsersConfig\trust.p12`
- ✗ Any `.properties` files in the project root or config/ directories

## How It Works

1. **Spring Boot starts** and reads `src/main/resources/application.properties`
2. **Finds property:** `spring.config.additional-location=file:${CONFIG_PATH:./config/}application-external.properties`
3. **Resolves CONFIG_PATH** environment variable or defaults to `./config/`
4. **Loads external configuration** from that location
5. **Merges properties** - external config overrides built-in defaults
6. **Application runs** with full externalized configuration

## Verification

To verify everything is configured correctly:

1. Check CONFIG_PATH is set:
   ```cmd
   echo %CONFIG_PATH%
   ```
   Expected output: `C:\Workspaces\adUsersConfig\`

2. Check file exists:
   ```cmd
   dir C:\Workspaces\adUsersConfig\application-external.properties
   ```
   Expected: File is listed

3. Check for errors during startup:
   ```
   Application should start without "property not found" errors
   Check console logs for "loaded X properties from application-external.properties"
   ```

4. Test LDAP connectivity:
   - Application should successfully bind to LDAP
   - User searches should work

## Additional Resources

- `CONFIG_SETUP.md` - Original setup documentation
- `SECURITY_VERIFICATION.md` - Security configuration details
- `README.md` - Project overview
- Spring Boot docs: https://spring.io/projects/spring-boot

## Questions or Issues?

If setup fails:
1. Check all environment variables are set correctly
2. Verify file paths have no typos
3. Ensure `application-external.properties` has all required properties filled in
4. Check application startup logs for specific error messages
5. Review the Troubleshooting section above
