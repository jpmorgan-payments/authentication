# Access Token Generator
The purpose of this tool is to retrieve an access token using provided client details

## Required Client Details

The following values need to be updated in Constants.java:
* CLIENT_ID
* RESOURCE
* CERTIFICATE_FILE_PATH
* PRIVATE_KEY_FILE_PATH


## How To Run
Requirements:
* JDK 11 or newer
* Maven

### Run in IDE
1. Update values in Constants.java
2. Run main() in TokenGeneratorApplication
3. Extract access token from console logs

### Run Executable JAR
1. Update values in Constants.java
2. Use Maven to package JAR
3. From Command line: "java -jar [path to JAR]\token-generator-0.0.1-SNAPSHOT-jar-with-dependencies.jar"
4. Extract access token from console logs


