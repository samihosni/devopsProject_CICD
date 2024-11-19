pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/samihosni/devopsProject_CICD'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install'  // Pour un projet Maven
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'  // Pour exécuter les tests
            }
        }

        stage('Deploy') {
            steps {
                sh './deploy.sh'  // Pour déployer sur le serveur
            }
        }
    }
}
