// Jenkins version of .github/workflows/test.yml.
// Needs "JDK17" and "Maven3" set up under Manage Jenkins > Tools, and the JUnit plugin.
pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    // Check the live site every morning, same as the GitHub schedule.
    triggers {
        cron('H 6 * * *')
    }

    parameters {
        string(name: 'TAGS', defaultValue: '(@smoke or @regression) and not @known-bug and not @blocked',
               description: 'Cucumber tags to run, e.g. @smoke or @known-bug')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Live site checks') {
            steps {
                sh "mvn -B test -Dtest=RunCucumberTest -Dheadless=true -Dcucumber.filter.tags='${params.TAGS}'"
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/cucumber-reports/**', allowEmptyArchive: true
        }
    }
}
