# payment-processing-system
This a simple payment processing system where user can see the account 
balance, user can deposit money and pay money into account. 
This REST API is written in Java, Hibernate JPA, Spring Boot and 
Spring Security that handles security for this API. 

## Tech Stack 
   - Java: JDK version 21
   - Spring Boot: version 3.5.1
   - Spring Security: version 6.5.1 
   - Spring Data JPA
   - Lombok
   - Maven: version 3.9.10
   - Database: MySQL version 8.0
   - Swagger / Open API documentation: http://localhost:8080/swagger-ui/index.html
   - Junit test: Juint 5 and Mockito
   - Integration test: MockMvc, Mock users are created for Spring Security, MySQLContainer and Docker

## Getting Started

### Prerequisites
To run payment-processing-system, you will be required Java version 21
& version supported IDE, build tool Maven and MySQL database. Make
sure that Lombok is enabled or supported by you IDE. To run Junit
and Integration test cases, you will be required Junit5, Mockito,
MockMvc, MySQLContainer and Docker. 

### Clone the repo
git clone https://github.com/iqbalbcis/payment-processing-system.git

## Build and run
- mvn clean install
- mvn spring-boot:run
- This application will start: http://localhost:8080

## Running Test cases and produce coverage

### Integration test
Before run the integration test, it is required to start the docker
locally earlier

### To run the all test cases
After start the docker, execute below command
- mvn test

### To produce the test coverage
- mvn clean verify
- After that, go to the target folder then go to the site folder then
  go to the jacoco folder then click to the index.html to see the
  test coverage 
