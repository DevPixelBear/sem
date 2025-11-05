FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:17 as prod
#adding my sql into the docker container
RUN yum update -y && \
    yum install -y mysql gss-ntlmssp
WORKDIR /app
#making sure it builds first by adding on a dependency since it was skipping it before for some reason
COPY --from=builder app/target/*-jar-with-dependencies.jar app.jar


ENV DB_HOST=db
ENV DB_PORT=3306
ENV DB_USER=root
ENV DB_PASSWORD=root
ENV DB_NAME=world

ENTRYPOINT ["java", "-jar", "app.jar"]
