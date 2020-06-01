FROM azul/zulu-openjdk-alpine:11

WORKDIR /usr/thunder

COPY build/libs/thunder-all.jar /usr/thunder/Thunder.jar
COPY config.txt /usr/thunder/config.txt

EXPOSE 3445

ENTRYPOINT ["java", "-jar", "Thunder.jar"]
