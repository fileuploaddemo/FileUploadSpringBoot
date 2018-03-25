FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ARG JAR_FILE=target/fileupload-springboot-0.1.0.jar
ADD ${JAR_FILE} app.jar
RUN sh -c 'touch /app.jar'
EXPOSE 8080 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]