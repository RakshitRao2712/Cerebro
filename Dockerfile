# ---- Stage 1: Build the Spring Boot app ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# ---- Stage 2: Runtime image with Terraform CLI installed ----
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Install Terraform CLI
# RUN apt-get update && apt-get install -y wget unzip gnupg software-properties-common \
#     && wget -O terraform.zip https://releases.hashicorp.com/terraform/1.16.0/terraform_1.16.0_linux_amd64.zip \
#     && unzip terraform.zip -d /usr/local/bin \
#     && rm terraform.zip \
#     && apt-get clean

RUN apt-get update && apt-get install -y wget unzip gnupg software-properties-common python3-pip \
    && wget -O terraform.zip https://releases.hashicorp.com/terraform/1.9.0/terraform_1.9.0_linux_amd64.zip \
    && unzip terraform.zip -d /usr/local/bin \
    && rm terraform.zip \
    && pip3 install --break-system-packages ansible ansible-lint \
    && apt-get clean

# Copy the built jar from Stage 1
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]