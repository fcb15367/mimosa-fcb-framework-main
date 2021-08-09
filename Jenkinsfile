#!/usr/bin/env groovy

pipeline {
  agent {
    docker {
      image 'softleader/maven-git:3.6.2-jdk-11-slim'
      args '-v /opt/jenkins/m2:/root/.m2'
    }
  }

  environment {
    MAVEN_OPTS="-Xmx2048m -XX:MaxMetaspaceSize=256m"
  }

  stages {

    stage('確認環境變數') {
      steps {
        sh 'printenv'
        sh 'java -version'
        sh 'mvn --version'
        echo "${params}"
      }
    }

    stage ('清理環境') {
      steps {
        sh "mvn -Psoftleader-maven-central clean"
      }
    }

    stage('程式編譯') {
      steps {
        sh "mvn -Psoftleader-maven-central compile -e"
      }
    }

    stage('檢查程式碼排版') {
      steps {
        sh '[ ! -z "$(git status -s)" ] && exit 1 || echo "Good to go!"'
      }
    }

    stage('單元測試') {
      steps {
        sh "mvn -Psoftleader-maven-central -Ptest test -e"
      }
      post {
        always {
          junit "**/target/surefire-reports/**/*.xml"
          step([$class: 'JacocoPublisher', changeBuildStatus: false, exclusionPattern:
            '**/Application.class, **/*DTO.class, **/*Entity.class, **/*Test*.class', inclusionPattern: '**/*.class',
                minimumBranchCoverage: '80', sourcePattern: '**/src'])
        }
      }
    }

    stage('SonarQube') {
      steps {
        withSonarQubeEnv('sonar') {
          sh 'mvn -Psoftleader-maven-central -Padvanced-test sonar:sonar -Dsonar.exclusions=**/target/** -Dsonar.coverage.exclusions=**/Application.java,**/*DTO.java,**/*Entity.java -Dsonar.pitest.mode=reuseReport'
        }
      }
    }
  }
}
