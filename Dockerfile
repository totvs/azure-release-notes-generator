FROM openjdk:12-alpine

COPY entrypoint.sh target/azure-release-notes-generator.jar /
ENTRYPOINT ["/entrypoint.sh"]
