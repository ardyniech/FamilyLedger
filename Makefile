.PHONY: help build test lint clean check release wrapper

help:
	@echo "FamilyLedger Developer Makefile"
	@echo "================================"
	@echo "make build      - Build debug APK"
	@echo "make test       - Run all local JVM unit and Robolectric tests"
	@echo "make lint       - Run Android lint analysis"
	@echo "make check      - Run lint and unit tests together"
	@echo "make release    - Assemble release APK/Bundle"
	@echo "make clean      - Clean build caches and outputs"
	@echo "make wrapper    - Regenerate Gradle wrapper files"

build:
	./gradlew assembleDebug

test:
	./gradlew testDebugUnitTest

lint:
	./gradlew lint

check: lint test

release:
	./gradlew assembleRelease

clean:
	./gradlew clean

wrapper:
	gradle wrapper --gradle-version 9.3.1
