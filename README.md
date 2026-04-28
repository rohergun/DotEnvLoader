[![Build](https://github.com/rohergun/DotEnvLoader/actions/workflows/build.yml/badge.svg)](https://github.com/rohergun/DotEnvLoader/actions/workflows/build.yml)


## DotEnvLoader
A lightweight Java library for loading .env files and making their values accessible within your application.

### Installation:
Add the following dependency to your pom.xml:
```xml
<dependency>
    <groupId>io.github.rohergun</groupId>
    <artifactId>dotenv-loader</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Usage
Create a .env file in your project root:
```
DATABASE_URL=jdbc:postgresql://localhost:5432/mydb
API_KEY=supersecretkey
SERVER_PORT=8080
```
Load it at application startup:
```
// Load from project root (default)
DotEnvLoader.load();

// Load from a custom path
DotEnvLoader.load("/path/to/custom.env");

// Access values
String dbUrl   = EnvContext.get("DATABASE_URL");
String port    = EnvContext.get("SERVER_PORT", "8080"); // with default fallback
```
EnvContext.get() checks the .env store first, then falls back to the real OS environment,<br>
then returns null (or the supplied default).

### .env Syntax
```
# Each line is KEY=value
FAV_SNACK=donuts
USERNAME=coolusername
EMPTY_VAL=

# Keys must be SCREAMING_SNAKE_CASE
DATABASE_URL=jdbc:postgresql://localhost:5432/mydb
```
- Keys must be SCREAMING_SNAKE_CASE — uppercase letters, digits, and underscores only
- Values can be any characters, including = signs
- Empty values are allowed (KEY=)
- Blank lines are ignored
- If the file is missing it is silently skipped — no error is thrown

### Error Handling
A DotEnvParseException is thrown if any non-empty line is malformed.<br>
The exception includes the file name, line number, and the offending line to make debugging straightforward:
```
Parse error in '.env' at line 3: missing '=' separator — expected KEY=value format
offending line: "badline"
```

### Building from Source:
```bash
git clone https://github.com/rohergun/DotEnvLoader.git
cd dotenv-loader
mvn verify
```