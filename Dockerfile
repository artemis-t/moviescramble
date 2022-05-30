FROM openjdk:8u191-jre-alpine

VOLUME /tmp

# For docker-stack healthcheck
RUN apk add curl

# set timezone to UTC
RUN apk add --no-cache tzdata && \
    ln -sf /usr/share/zoneinfo/UTC /etc/localtime

# use moviescramble.jar file
COPY target/moviescramble.jar moviescramble.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/urandom", \
    "-XX:+CrashOnOutOfMemoryError", "-Xms2560m", "-Xmx2560m", \
    "-jar", "/moviescramble.jar"]
