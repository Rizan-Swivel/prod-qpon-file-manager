# File Service
This application provides uploading and downloading files from s3.

## Requirements
* Spring boot 2.2.1
* Maven 3.6 (only to run maven commands)

## Dependencies
All dependencies are available in pom.xml.

## Note
Environment variables available in resources/env.list file are needed to set. Replace <> with correct credential values to run the service.
```
export AWS_ACCESS_KEY_ID=<AWS_ACCESS_KEY_ID>
export AWS_SECRET_ACCESS_KEY=<AWS_SECRET_ACCESS_KEY>
export AWS_REGION=<AWS_REGION>
```
## Configuration
Configure the relevant configurations in application.yml and bootstrap.yml in 
src/main/resources before building the application

## Build
```
mvn clean compile package
```

## Run
```
mvn spring-boot:run
```
or
```
java -jar target/qpon-file-manager-service-0.0.1-SNAPSHOT.jar
```

## Test
```
mvn test
```

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/maven-plugin/)


## License

Copyright (c) Swivel - 
This source code is licensed under the  license. 
