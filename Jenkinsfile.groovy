pipeline {
    agent any
    environment {
        // Add Nexus credentials and Docker Hub credentials
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub')
        NEXUS_CREDENTIALS = credentials('nexus-credentials ')  // Add Nexus credentials
        IMAGE_NAME = 'samihosni/devopsproject_cicd-app '
        IMAGE_TAG = 'latest'
        NEXUS_URL = 'http://localhost:8083'  // Set your Nexus server URL
        NEXUS_REPOSITORY = 'maven-releases'  // Set the Nexus repository to deploy (e.g., maven-releases or maven-snapshots)
    }


    stages {
        stage('📥 Checkout') {
            steps {
                echo 'Cloning the repository...'
                git url: 'https://github.com/samihosni/devopsProject_CICD.git', branch: 'master'
            }
        }

        stage('🧹 Clean') {
            steps {
                echo 'Cleaning the project...'
                bat 'mvn clean'
            }
        }

        stage('⚙️ Compile') {
            steps {
                echo 'Compiling the project...'
                bat 'mvn compile'
            }
        }

        stage('🏗️ Build') {
            steps {
                echo 'Building the project...'
                bat 'mvn clean deploy -DskipTests'
            }
        }

        stage('🐳 Build Docker Image') {
            steps {
                echo 'Building Docker Image...'
                bat "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
            }
        }

        stage('📤 Push Docker Image') {
            steps {
                echo 'Pushing Docker Image to Docker Hub...'
                script {
                    bat "echo $DOCKER_HUB_CREDENTIALS_PSW | docker login -u $DOCKER_HUB_CREDENTIALS_USR --password-stdin"
                    bat "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                    bat "docker logout"
                }
            }
        }

        stage('🚀 Deploy with Docker Compose') {
            steps {
                echo 'Deploying the application with Docker Compose...'
                script {
                    bat "echo $DOCKER_HUB_CREDENTIALS_PSW | docker login -u $DOCKER_HUB_CREDENTIALS_USR --password-stdin"
                    bat 'start docker-compose down'
                    bat 'start docker-compose up -d'
                    bat "docker logout"
                }
            }
        }

        // Add Nexus Deployment Stage
        stage('🔄 Deploy to Nexus') {
            steps {
                script {
                    echo 'Deploying the artifact to Nexus...'
                    // Ensure Maven deploys to Nexus repository
                    bat """
                        mvn clean deploy -DskipTests \
                        -DnexusUrl=${NEXUS_URL} \
                        -DrepositoryId=${NEXUS_REPOSITORY} \
                        -Durl=${NEXUS_URL}/repository/${NEXUS_REPOSITORY}/
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Build and analysis completed successfully!'
            emailext(
                    to: "samy.hosni@gmail.com",
                    subject: "🎉 Build SUCCESS !: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    mimeType: 'text/html',
                    body: """
                    <html>
                        <body style="background: url('https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS90sm-yM3GhbgHIE-mTLUBrYYMsoZDiCj50yw&usqp=CAU');">
                            <div style="background-color: rgba(255, 255, 255, 0.85); padding: 20px; border-radius: 10px;">
                                <h2 style="color: #4CAF50;">🎉 Jenkins Build Succeeded!</h2>
                                <p>Bonjour Mr. Sami El HOSNI,</p>
                                <p>Le build de votre projet s'est terminé avec succès. Voici les détails :</p>
                                <ul style="list-style: none; padding: 0;">
                                    <li><strong>Project:</strong> ${env.JOB_NAME}</li>
                                    <li><strong>Build Number:</strong> ${env.BUILD_NUMBER}</li>
                                    <li><strong>Status:</strong> <span style="color:green;"><strong>SUCCESS</strong></span></li>
                                    <li><strong>Branch:</strong> ${env.GIT_BRANCH}</li>
                                    <li><strong>Commit:</strong> ${env.GIT_COMMIT}</li>
                                    <li><strong>Build Duration:</strong> ${currentBuild.durationString}</li>
                                </ul>
                                <p>Plus d'informations :</p>
                                <ul style="list-style: none; padding: 0;">
                                    <li><a href="${env.BUILD_URL}console" style="color: #1E90FF;">Console Output</a></li>
                                    <li><a href="${env.BUILD_URL}changes" style="color: #1E90FF;">Changes</a></li>
                                </ul>
                            </div>
                        </body>
                    </html>
                """
            )
        }
        failure {
            echo 'Build or analysis failed.'
            emailext(
                    to: "samy.hosni@gmail.com",
                    subject: "🚨 Build FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    mimeType: 'text/html',
                    body: """
                    <html>
                        <body style="background: url('https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS90sm-yM3GhbgHIE-mTLUBrYYMsoZDiCj50yw&usqp=CAU');">
                            <div style="background-color: rgba(255, 255, 255, 0.85); padding: 20px; border-radius: 10px;">
                                <h2 style="color: #FF0000;">🚨 Jenkins Build Failed!</h2>
                                <p>Bonjour Sami El HOSNI,</p>
                                <p>Le build de votre projet a échoué. Voici les détails :</p>
                                <ul style="list-style: none; padding: 0;">
                                    <li><strong>Project:</strong> ${env.JOB_NAME}</li>
                                    <li><strong>Build Number:</strong> ${env.BUILD_NUMBER}</li>
                                    <li><strong>Status:</strong> <span style="color:red;"><strong>FAILURE</strong></span></li>
                                    <li><strong>Branch:</strong> ${env.GIT_BRANCH}</li>
                                    <li><strong>Commit:</strong> ${env.GIT_COMMIT}</li>
                                    <li><strong>Build Duration:</strong> ${currentBuild.durationString}</li>
                                </ul>
                                <p>Plus d'informations :</p>
                                <ul style="list-style: none; padding: 0;">
                                    <li><a href="${env.BUILD_URL}console" style="color: #1E90FF;">Console Output</a></li>
                                    <li><a href="${env.BUILD_URL}changes" style="color: #1E90FF;">Changes</a></li>
                                    <li><a href="${env.BUILD_URL}testReport" style="color: #1E90FF;">Test Results</a></li>
                                </ul>
                                <p>Merci de vérifier les journaux de build pour plus de détails.</p>
                            </div>
                        </body>
                    </html>
                """
            )
        }
        always {
            echo 'Cleaning up...'
        }
    }
}
