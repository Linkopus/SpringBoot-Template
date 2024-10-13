# Define the Maven command depending on the OS
ifeq ($(OS),Windows_NT)
  MVN_CMD = mvnw.cmd
else
  MVN_CMD = ./mvnw
endif

.PHONY: dev lint lint-fix coverage test build install

# Start development mode
dev:
	$(MVN_CMD) spring-boot:run

# Run linter check (Spotless + Checkstyle)
lint:
	$(MVN_CMD) spotless:check checkstyle:check

# Run linter with auto-fix
lint-fix:
	$(MVN_CMD) spotless:apply checkstyle:check

# Run code coverage
coverage:
	$(MVN_CMD) clean verify

# Run tests
test:
	$(MVN_CMD) test

# Build the project
build:
	$(MVN_CMD) clean install

# Install the project to the local Maven repository
install:
	$(MVN_CMD) initialize install
