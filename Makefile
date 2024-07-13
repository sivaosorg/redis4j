# Define the directory where the JAR file is located after build
BUILD_DIR=./plugin/build
LOG_DIR=logs
# Declare targets as phony to avoid conflicts with files of the same name
.PHONY: build test jar clean

build:
	clear
	rm -rf $(BUILD_DIR)
	./gradlew jar

clean:
	./gradlew clean

jar: build

list-task:
	./gradlew tasks

test:
	./gradlew test

groovy:
	./gradlew build

tree:
	# Create logs directory if not exists
	mkdir -p $(LOG_DIR)
	# Generate project structure and save it to logs/project_structure.txt
	tree -I ".gradle|.idea|build|logs" > ./$(LOG_DIR)/project_structure.txt
