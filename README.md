# AD Users - Active Directory User Management Application

A Spring Boot REST API application for querying and managing Active Directory (LDAP) users across multiple environments (test, production, and default).

## Features

- Multi-environment LDAP support (test, production, default)
- Secure SSL/TLS connections with certificate validation
- User information retrieval (display name, email, groups, account expiration, etc.)
- RESTful API endpoints
- Externalized configuration for easy deployment
- Comprehensive error handling

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Active Directory/LDAP server access
- SSL certificate (trust.p12) for LDAP connections

## Project Structure

```
ad-users-main/
├── src/
│   ├── main/
│   │   ├── java/com/example/adusersdemo/
│   │   │   ├── AdUsersDemoApplication.java          # Spring Boot entry point
│   │   │   ├── controller/
│   │   │   │   └── AdUserController.java            # REST API endpoints
│   │   │   ├── model/
│   │   │   │   └── AdUser.java                      # User data model
│   │   │   ├── service/
│   │   │   │   ├── AdUserService.java               # User service logic
│   │   │   │   ├── LdapConfig.java                  # LDAP configuration
│   │   │   │   └── LdapRouter.java                  # Multi-env LDAP routing
│   │   │   └── util/
│   │   │       └── AdTimeUtil.java                  # Active Directory time utilities
│   │   └── resources/
│   │       └── application.properties               # Application configuration
│   └── test/                                         # Unit tests
├── pom.xml                                           # Maven dependencies
├── .gitignore                                        # Git ignore rules
├── README.md                                         # This file
├── CONFIG_SETUP.md                                   # Configuration setup guide
└── SECURITY_VERIFICATION.md                          # Security audit report
```

## Quick Start

### ⚠️ IMPORTANT: Configuration Required

**All sensitive configuration (LDAP credentials, passwords, certificates) is externalized and NOT included in the repository.**

👉 **[See SETUP_GUIDE.md for complete setup instructions](SETUP_GUIDE.md)** - Follow this guide to:
1. Create your config directory
2. Copy and fill in `application-external.properties` 
3. Place your `trust.p12` certificate
4. Set environment variables
5. Run the application

### Quick Reference

```bash
# Step 1: Create config directory
mkdir C:\Workspaces\adUsersConfig

# Step 2: Copy template and fill in your values
copy application-external.properties.template C:\Workspaces\adUsersConfig\application-external.properties
# Edit the file with your LDAP server details, credentials, etc.

# Step 3: Set environment variable (Windows)
set CONFIG_PATH=C:\Workspaces\adUsersConfig\

# Step 4: Run the application
mvn spring-boot:run
```

For detailed instructions, troubleshooting, and environment-specific setup, see [SETUP_GUIDE.md](SETUP_GUIDE.md).

The application will start on **http://localhost:8080**

## API Endpoints

### Get Users by Environment

**Request:**
```
GET /ad/usersByEnv/{env}
```

**Parameters:**
- `{env}` - Environment name: `test`, `prod`, or `default`

**Examples:**

Test LDAP:
```
http://localhost:8080/ad/usersByEnv/test
```

Production LDAP:
```
http://localhost:8080/ad/usersByEnv/prod
```

Default LDAP:
```
http://localhost:8080/ad/usersByEnv/default
```

**Response:**
```json
[
  {
    "displayName": "John Smith",
    "mail": "john.smith@domain.com",
    "name": "john.smith",
    "sAMAccountName": "jsmith",
    "accountExpires": "2025-12-31T23:59:59",
    "pwdLastSet": "2024-01-15T10:30:00",
    "whenCreated": "2024-01-01T09:00:00.000Z",
    "whenChanged": "2024-03-10T14:20:00.000Z",
    "memberOf": [
      "CN=Admins,OU=Groups,DC=domain,DC=com",
      "CN=Sales,OU=Groups,DC=domain,DC=com"
    ]
  }
]
```

## Configuration Details

### LDAP Configuration Properties

| Property | Description | Example |
|----------|-------------|---------|
| `spring.ldap.urls` | LDAP server URL(s) | `ldaps://ldap.example.com:636` |
| `spring.ldap.base` | LDAP base DN | `DC=example,DC=com` |
| `spring.ldap.search.base` | Search base DN | `OU=Users,DC=example,DC=com` |
| `spring.ldap.username` | Service account DN | `CN=svc_account,OU=Service Accounts,DC=example,DC=com` |
| `spring.ldap.password` | Service account password | `password123` |
| `spring.ldap.ssl.trust-store` | Path to truststore | `file:C:\Workspaces\adUsersConfig\trust.p12` |
| `spring.ldap.ssl.trust-store-password` | Truststore password | `trustpass` |

### Multi-Environment Setup

The application supports three environments:

**Test Environment (ad.test.*):**
```properties
ad.test.url=ldaps://test-ldap.example.com:636
ad.test.base=DC=test,DC=example,DC=com
ad.test.searchBase=OU=TestUsers,DC=test,DC=example,DC=com
ad.test.user=CN=test_service_account,...
ad.test.pass=test_password
```

**Production Environment (ad.prod.*):**
```properties
ad.prod.url=ldaps://prod-ldap.example.com:636
ad.prod.base=DC=example,DC=com
ad.prod.searchBase=OU=Users,DC=example,DC=com
ad.prod.user=CN=prod_service_account,...
ad.prod.pass=prod_password
```

**Default Environment (spring.ldap.*):**
```properties
spring.ldap.urls=ldaps://default-ldap.example.com:636
spring.ldap.base=DC=example,DC=com
spring.ldap.search.base=OU=Users,DC=example,DC=com
spring.ldap.username=CN=default_service_account,...
spring.ldap.password=default_password
```

## Security Considerations

### Certificate Management

- The application uses SSL/TLS for secure LDAP connections
- Certificate validation is enabled by default
- truststore file (trust.p12) should be stored securely outside the JAR
- Access to the truststore file should be restricted to the application user

### Credential Management

- **Never commit credentials to Git** - Use environment variables or external config files
- Service account passwords should be store securely
- Rotate credentials regularly
- Monitor service account activity in Active Directory

### Running Securely

```bash
# Set environment variables for credentials (Linux/Mac)
export LDAP_USERNAME="CN=svc_account,OU=Service Accounts,DC=example,DC=com"
export LDAP_PASSWORD="secure_password"
export TRUSTSTORE_PASSWORD="truststore_password"

# Run the application
java -jar target/ad-users-demo-0.0.1-SNAPSHOT.jar
```

## Troubleshooting

### LDAP Connection Issues

**Error:** `Failed to bind`
- Check service account credentials
- Verify LDAP server URL and port
- Ensure SSL certificate is valid for the LDAP server

**Error:** `Certificate validation failed`
- Import the LDAP server's certificate into the truststore
- Verify truststore path in configuration
- Check truststore password

### Application Won't Start

**Error:** `Could not resolve placeholder`
- Ensure all required environment variables are set
- Check application.properties for syntax errors
- Verify external config file path (if using)

**Error:** `Connection refused`
- Verify LDAP server is running and accessible
- Check firewall rules for port access (typically 636 for LDAPS)

### No Users Returned

- Verify the service account has permissions to query the LDAP directory
- Check search base DN matches your directory structure
- Verify LDAP filter in AdUserService matches your user objects

## Development

### Running Tests

```bash
mvn test
```

### Building with Debug Output

```bash
mvn clean package -X
```

### Running with Debug Logging

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--debug"
```

## Dependencies

Key dependencies (see `pom.xml` for full list):

- Spring Boot 2.7.1
- Spring LDAP
- Spring Security LDAP
- UnboundID LDAP SDK
- Lombok

## License

This project is proprietary software developed for internal use.

## Support

For issues or questions:
1. Check `CONFIG_SETUP.md` for configuration help
2. Review `SECURITY_VERIFICATION.md` for security information
3. Check application logs for error messages
4. Consult your LDAP/Active Directory administrator

## Version

**Current Version:** 0.0.1-SNAPSHOT

**Last Updated:** March 12, 2026

## Changelog

### Version 0.0.1-SNAPSHOT
- Initial release
- Multi-environment LDAP support
- SSL/TLS certificate validation
- Comprehensive error handling
- Security hardening completed
- Externalized configuration support
