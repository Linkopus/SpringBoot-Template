# Linkopus SpringBoot Template

This project serves as a template for building Java-based microservices using Spring Boot. The template is designed to simplify the setup and development of microservices for Java applications.

## Requirements

- **JDK**: Version 21
- **Maven**: Version 3.6.3

## Getting Started

To get started with the project, you can use the provided Makefile commands.

### Development Mode

To start the application in development mode, run:

```bash
make dev
```
This command starts the Spring Boot application.

### Running Linter

To check your code for linting issues (Spotless + Checkstyle), run:

```bash
make lint
```

For automatic fixing of linting issues:

```bash
make lint-fix
```

### Running Tests

To run all the tests in the project, execute:

```bash
make test
```

### Code Coverage

To run the tests with code coverage:

```bash
make coverage
```

### Building the Project

To build the project, use the following command:

```bash
make build
```

### Installing the Project Dependencies

To install the project Dependencies :

```bash
make install
```

### Dockerization

To build a Docker image for your SpringBoot microservice, follow the steps below.

Ensure Docker is installed on your system.
Build the Docker image using the following command:

```bash
docker build -t linkopus-springboot-template .
```

To run the containerized application, use the following command:
```bash
docker run -p <port>:<port> linkopus-springboot-template
```