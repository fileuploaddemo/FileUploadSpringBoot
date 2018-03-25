# FileUploadSpringBoot
A file upload web site with Spring Boot

[![Build Status](https://travis-ci.org/fileuploaddemo/FileUploadSpringBoot.svg?branch=master)](https://travis-ci.org/fileuploaddemo/FileUploadSpringBoot)

https://fileupload-springboot.herokuapp.com/

```sh
mvn compile

mvn spring-boot:run

./mvnw package && java -jar target/fileupload-springboot-0.1.0.jar

docker build -t codeyu/fileupload-springboot .

docker run --rm -it -p 8080:8080 codeyu/fileupload-springboot:latest
```
