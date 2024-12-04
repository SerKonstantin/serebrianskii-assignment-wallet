.PHONY: install start run image remove-image check clean

install:
	./gradlew wrapper --gradle-version 8.0
	./gradlew clean build

start: install
	./gradlew bootRun

run:
	./gradlew bootRun

image: install
	docker compose up -d

remove-image:
	docker compose down -v

check:
	./gradlew test checkstyleMain checkstyleTest

clean:
	./gradlew clean
	docker compose down -v
