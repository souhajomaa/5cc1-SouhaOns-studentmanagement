pipeline {
    agent any
    tools {
        jdk 'JDK17'
        maven 'M2_HOME'
    }
    environment {
        DOCKERHUB_USER = 'souhajomaa1412'
        IMAGE = "${DOCKERHUB_USER}/student-management"
    }
    triggers {
        pollSCM('H/5 * * * *')
    }
    stages {
        stage('Git') {
            steps {
                checkout scm
                script {
                    // "origin/jomaasouha_5cc1" -> "jomaasouha_5cc1"
                    env.BRANCHE = (env.GIT_BRANCH ?: 'main').replaceAll('^origin/', '')
                    env.TAG = "${env.BRANCHE}-${env.BUILD_NUMBER}"
                }
                sh 'git log -1 --pretty=format:"%h - %an : %s"'
                echo "Branche : ${env.BRANCHE} — tag de l'image : ${env.TAG}"
            }
        }
        stage('Build') {
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }
        stage('Test unitaire') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('Docker build') {
            steps {
                sh "docker build -t ${IMAGE}:${TAG} ."
            }
        }
        stage('Docker push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub',
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_PASS')]) {
                    sh 'echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin'
                    sh "docker push ${IMAGE}:${TAG}"
                    script {
                        if (env.BRANCHE == 'main') {
                            sh "docker tag ${IMAGE}:${TAG} ${IMAGE}:latest"
                            sh "docker push ${IMAGE}:latest"
                        }
                    }
                    sh 'docker logout'
                }
            }
        }
        stage('Déploiement') {
            when { expression { env.BRANCHE == 'main' } }
            steps {
                sh 'docker compose pull app'
                sh 'docker compose up -d'
            }
        }
    }
    post {
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            echo "Succès : image ${IMAGE}:${TAG} publiée sur Docker Hub."
        }
        failure {
            echo 'Échec : consulter la Console Output du stage en rouge.'
        }
        always {
            sh "docker image rm -f ${IMAGE}:${TAG} || true"
        }
    }
}
