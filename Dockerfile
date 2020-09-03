FROM maven:3-openjdk-11-slim AS package

COPY ./ /sources
WORKDIR /sources

RUN mvn clean package -q -B

FROM busybox:1.31.0 AS base

COPY --from=package /sources/target/azure-release-notes-generator.jar /app.jar
RUN unzip app.jar

FROM openjdk:11-jre-slim

COPY --from=base /BOOT-INF/lib /app/lib
COPY --from=base /META-INF /app/META-INF
COPY --from=base /BOOT-INF/classes /app

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-cp", "app:app/lib/*", "io.tjf.releasenotes.Application"]
