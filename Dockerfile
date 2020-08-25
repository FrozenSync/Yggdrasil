FROM openjdk:11 AS builder
COPY /src/ /app/src/
COPY build.gradle.kts /app/
COPY gradle.properties /app/
COPY settings.gradle /app/
COPY gradlew /app/
COPY /gradle/ /app/gradle/
WORKDIR /app/
RUN ./gradlew shadowJar

FROM openjdk:11
LABEL maintainer="leroytruong@protonmail.com"
COPY --from=builder /app/build/libs/Yggdrasil-1.0-SNAPSHOT-all.jar /Yggdrasil.jar
CMD java -jar /Yggdrasil.jar
