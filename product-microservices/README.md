# Building product components microservices using spring boot and spring cloud.

## The framework we used are Spring Boot, Spring Cloud, Docker, Reactjs, Kubernetes, RabbitMQ, Kafka, MySQL, MongoDB, Redis and other frameworks.

* The microservices are built with a reactive approach using Spring Boot and project reactor (webflux)
* It is resilient and scalable with the help of Spring Cloud resilient4J and Spring Cloud gateway
* It uses OAuth 2.0/OIDC and Spring Security to protect public APIs
* It implements Docker to bridge the gap between development, testing, staging and production.
* It leverages Kubernetes to deploy and manage each microservice container in each environment.
* It applies Istio for improved security, observability and traffic management.
* It uses ReactJs (Hooks), Redux and NodeJs as part of the services.

## Build this project
This project is created little by little with each branch for difficulty level. Clone the branch you need to build and run `mvn build`


### Docker Setup
* Create a new spring profile in the application.yaml file called 'docker' as ```spring.config.activate:on-profile: - docker
  server.port: 8080``` for each of the microservices.

* Create a Dockerfile at the root folder of each microservice.
* Create a docker-compose.yaml file at the root of the parent.
* Run the following command:
```shell
./mvn clean install
docker-compose build
docker-compose up -d
```