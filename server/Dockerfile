FROM openjdk:8-alpine
MAINTAINER tf.pack@gmail.com

WORKDIR /app
COPY target/scala-2.12/server*.jar /app/app.jar

CMD ["java", "-jar", "./app.jar", "-XX:+ExitOnOutOfMemoryError"]