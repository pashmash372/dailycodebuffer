FROM openjdk:11

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} server-registry.jar

ENTRYPOINT ["java","-jar","/server-registry.jar"]

EXPOSE 8761