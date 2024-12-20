pipeline {
    agent any
    environment {
        DOCKER_HUB_CREDENTIALS = credentials('dockers')
        IMAGE_NAME = 'samihosni/devopsproject_cicd-app'
        IMAGE_TAG = 'latest'
        NEXUS_VERSION = 'nexus3'
        NEXUS_CREDENTIAL_ID = 'nexus-credentials'
        NEXUS_PROTOCOL = 'http'
        NEXUS_URL = 'localhost:8083'
        NEXUS_REPOSITORY = 'devOpsProject'
        ARTIFACT_VERSION = "${BUILD_NUMBER}"
        SONARQUBE_SERVER = 'SonarQube' // Replace with your SonarQube server name in Jenkins
        SONARQUBE_TOKEN = credentials('sonar-token') // Replace with your SonarQube token ID
    }

    stages {
        stage(' Checkout') {
            steps {
                echo 'Cloning the repository...'
                git url: 'https://github.com/samihosni/devopsProject_CICD.git', branch: 'master'
            }
        }

        stage(' Clean') {
            steps {
                echo 'Cleaning the project...'
                bat 'mvn clean'
            }
        }

        stage(' Compile') {
            steps {
                echo 'Compiling the project...'
                bat 'mvn compile'
            }
        }



        stage(' Unit Tests') {
            steps {
                echo 'Running unit tests...'
                bat 'mvn test -Dtest=TimeSheetTestService'
            }
            post {
                always {
                    echo 'Collecting unit test results...'
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Selenium Tests') {
            steps {
                echo 'Running Selenium tests...'
                script {
                    // Assuming your Selenium tests are in the src/test/java directory
                    // and have a test class like SeleniumTest.java
                    bat 'mvn test -Dtest=SeleniumTest'  // Adjust to your actual test class
                }
            }
            post {
                always {
                    echo 'Collecting Selenium test results...'
                    junit '**/target/surefire-reports/*.xml' // Adjust path if needed
                }
            }
        }




        stage(' Build') {
            steps {
                echo 'Building the project...'
                configFileProvider([configFile(fileId: '1f62a59a-5aea-4522-8445-886f83159aea', variable: 'mavenconfig')]) {
                    bat "mvn -s %mavenconfig% clean deploy -DskipTests=true"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Building Docker Image...'
                bat "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }

        stage(' Push Docker Image') {
            steps {
                echo 'Pushing Docker Image to Docker Hub...'
                script {
                    bat """
                        echo $DOCKER_HUB_CREDENTIALS_PSW | docker login -u $DOCKER_HUB_CREDENTIALS_USR --password-stdin
                        docker push ${IMAGE_NAME}:${IMAGE_TAG}
                        docker logout
                    """
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                echo 'Deploying the application with Docker Compose...'
                script {
                    bat """
                        echo $DOCKER_HUB_CREDENTIALS_PSW | docker login -u $DOCKER_HUB_CREDENTIALS_USR --password-stdin
                        docker-compose up -d
                        echo "Listing running Docker containers..."
                        docker ps
                        docker logout
                    """
                }
            }
        }

        stage('Push Prometheus Metrics') {
            steps {
                script {
                    // Example of sending build data to Prometheus (you can use metrics libraries in your app)
                    // For Jenkins, Prometheus metrics plugin is scraping from /prometheus endpoint automatically

                    // You can trigger a manual push to a custom Prometheus endpoint if needed
                    echo 'Pushing Prometheus metrics for the build.'
                    bat 'curl http://localhost:8081/prometheus/metrics'  // Make sure Prometheus plugin is installed in Jenkins
                }
            }
        }

        stage('Publish to Nexus') {
            steps {
                script {
                    def pom = readMavenPom file: "pom.xml"
                    def artifactPath = findFiles(glob: "target/*.${pom.packaging}")[0].path

                    echo "*** File: ${artifactPath}, group: ${pom.groupId}, packaging: ${pom.packaging}, version: ${pom.version}"

                    nexusArtifactUploader(
                            nexusVersion: NEXUS_VERSION,
                            protocol: NEXUS_PROTOCOL,
                            nexusUrl: NEXUS_URL,
                            groupId: pom.groupId,
                            version: pom.version,
                            repository: NEXUS_REPOSITORY,
                            credentialsId: NEXUS_CREDENTIAL_ID,
                            artifacts: [[artifactId: pom.artifactId, classifier: '', file: artifactPath, type: pom.packaging]]
                    )
                }
            }
        }
    }

    post {
        success {
            echo 'Build and deployment completed successfully!'
            emailext(
                    to: "samy.hosni@gmail.com",
                    subject: " Build SUCCESS! ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    mimeType: 'text/html',
                    body: "The build of ${env.JOB_NAME} was successful. Build URL: ${env.BUILD_URL}"
            )
        }
        failure {
            echo 'Build or deployment failed.'
            emailext(
                    to: "samy.hosni@gmail.com",
                    subject: " Build FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    mimeType: 'text/html',
                    body: "The build of ${env.JOB_NAME} failed. Build URL: ${env.BUILD_URL}"
            )
        }
        always {
            echo 'Cleaning up workspace...'
        }
    }
}
