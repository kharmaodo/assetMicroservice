pipeline {
    agent  {label 'java'}
    environment {
        SPRING_PROFILES_ACTIVE = 'prod'
        APP_NAME    = 'asset-microservice'
        APP_BASE_FOLDER    = 'assetMicroservice'
        ARTIFACT_ID = readMavenPom(file:'assetMicroservice/pom.xml').getArtifactId()
        ARTIFACT_VERSION = readMavenPom(file:'assetMicroservice/pom.xml').getVersion()
        PACKAGING = readMavenPom(file:'assetMicroservice/pom.xml').getPackaging()
        ARTIFACT_NAME = "${ARTIFACT_ID}-${ARTIFACT_VERSION}.${PACKAGING}"
        DATASOURCE_USERNAME = "selfcare"
        DATASOURCE_PASSWORD = "spMr9yU"
        DATASOURCE_NAME = "selfcare_assets"
        DATASOURCE_HOST = "10.0.80.57"
        APP_SERVER_PORT = "8081"
        APP_SERVER_IP = "10.0.80.58"
    }
    stages {
        stage('Build') {
            steps {
                sh "mvn -f assetMicroservice -P${SPRING_PROFILES_ACTIVE} clean verify -DskipTests"
            }
            post{
                always{
                        archiveArtifacts artifacts: 'assetMicroservice/target/*.jar', fingerprint: true
                }
            }
        }
        stage('Checkout ansible files') {
            steps {
                git credentialsId: 'jenkins', url: 'http://10.0.80.57:3000/Tooling/Ansible.git'
            }
        }

        stage('Deploy app') {
            steps {
                sh 'ansible-playbook -i hosts --extra-vars "remote_server=devenv" deploy-spring-boot.yml'
            }
        }
        stage('Deploy nginx conf') {
            steps {
                sh 'ansible-playbook  -i hosts --extra-vars "remote_server=toolsenv" deploy-nginx-conf.yml'
            }
        }
    }
}
