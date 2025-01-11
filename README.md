# Medicine Application

## Overview
Medicine Application is a Spring Boot project designed to demonstrate a backend application for the interview.
This guide explains how to build and run the application both as a standalone Spring Boot app and as a Dockerized application.

---

## Prerequisites
To run the application, you need the following tools installed locally:
1. **Java 21**
2. **Maven 3.8+**
3. **Docker** (including Docker Compose)

If Docker is not installed, you can download it from the official website: [Docker Install Guide](https://docs.docker.com/get-docker/).

---

## Building and Running the Application

### 1. Running as a Standalone Spring Boot App

Simply click "run" in your IDE or follow the steps below to run the application from the command line.
You will need mongoDB running on your local machine! That's why docker-compose is recommended.

#### Steps:
1. Build the application using Maven:
    ```bash
    mvn clean install
2. Run the application:
    ```bash
    mvn spring-boot:run
3. The application API will be available at:
    ```
    http://localhost:8080/swagger-ui/index.html

### 2. Running as a Dockerized Application

#### Steps:
1. Build the application JAR file:
    ```bash
    mvn clean package
   
2. Build and start the Docker containers (application and database) using Docker Compose:
    ```bash
    docker-compose up --build
   
3. The application API will be available at:
    ```
    http://localhost:8080/swagger-ui/index.html

### Docker debugging
If you want to debug your local application which is running in docker container you create a remote debugger configuration in your IDE.
1. Add a new remote configuration in your IDE (choose Remote JVM Debugging)
2. Ensure that's your configuration: ```-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005```
3. in docker-compose there's already debug port exposed (5005) and agent is registered```- _JAVA_OPTIONS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005```
4. Run docker-compose as usual
5. Run your remote debugger configuration in your IDE

### MongoDB
To connect to mongoDB use:
1. ``` mongo --host mongodb --port 27017 -u root -p password --authenticationDatabase admin```
2. ```use medicine```
3. example: ```db.drugApplication.find().pretty()```
