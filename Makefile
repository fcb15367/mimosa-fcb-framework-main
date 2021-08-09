# RELEASE defines the release-version fob the bundle.
VERSION ?=

##@ General

help: ## Display this help.
	@awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  make \033[36m<target>\033[0m\n"} /^[a-zA-Z_0-9-]+:.*?##/ { printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2 } /^##@/ { printf "\n\033[1m%s\033[0m\n", substr($$0, 5) } ' $(MAKEFILE_LIST)

##@ Develop

format: ## Format the source code.
	mvn process-sources -e

clean: ## Remove files generated at build-time.
	mvn -Psoftleader-maven-central -e clean

compile: clean  ## Clean and compile the source code.
	mvn -Psoftleader-maven-central -e compile

test: clean ## Clean and test the compiled code.
	mvn -Psoftleader-maven-central -e test

##@ Delivery

new-version: ## Update version.
ifeq ($(strip $(BOT)),)
	$(error VERSION is required)
endif
	mvn -Psoftleader-maven-central versions:set -DnewVersion=$(VERSION)
	mvn -Psoftleader-maven-central versions:commit

deploy: ## Pack w/o unit testing, and deploy to remote repository.
	mvn -Psoftleader-maven-central deploy -e -DskipTests
