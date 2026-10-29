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
                    env.BRANCHE = (env.GIT_BRANCH ?: 'main').replaceAll('^origin/', '')
                    env.TAG = "${env.BRANCHE}-${env.BUILD_NUMBER}"
                }
                sh 'git log -1 --pretty=format:"%h - %an : %s"'
            }
        }
        stage('Version') {
            steps {
                script {
                    // version lue dans le pom, ex. 1.1-SNAPSHOT
                    def pomVersion = sh(returnStdout: true, script:
                        'mvn -q help:evaluate -Dexpression=project.version -DforceStdout'
                    ).trim()
                    if (env.BRANCHE == 'main') {
                        // sur main : release unique et immuable, ex. 1.1.27
                        env.VERSION = pomVersion.replace('-SNAPSHOT', '') +
                                      '.' + env.BUILD_NUMBER
                        sh "mvn -B -q versions:set -DnewVersion=${VERSION} " +
                           "-DgenerateBackupPoms=false"
                    } else {
                        // sur une branche : on garde le snapshot
                        env.VERSION = pomVersion
                    }
                }
                echo "Version du livrable : ${VERSION}"
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
        stage('Analyse SonarQube') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh """
                      mvn -B sonar:sonar \\
                        -Dsonar.projectKey=student-management-${BRANCHE} \\
                        -Dsonar.projectName=student-management-${BRANCHE}
                    """
                }
            }
        }
        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage('Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus',
                                                  usernameVariable: 'NEXUS_USER',
                                                  passwordVariable: 'NEXUS_PASS')]) {
                    sh 'mvn -B deploy -DskipTests -s settings-nexus.xml'
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
        stage('Déploiement Kubernetes') {
            when { expression { env.BRANCHE == 'main' } }
            steps {
                // secret, configmap, MySQL : appliqués tels quels (idempotent)
                sh '''
                  kubectl apply -f k8s/mysql-secret.yaml \
                                -f k8s/app-configmap.yaml \
                                -f k8s/mysql.yaml
                '''
                // application : l'image du manifest est remplacée par le tag exact du build
                sh """
                  sed 's#image: .*student-management:.*#image: ${IMAGE}:${TAG}#' \
                      k8s/app.yaml | kubectl apply -f -
                """
                // attend la fin de la mise à jour progressive
                sh 'kubectl rollout status deployment/student-app --timeout=240s'
                sh 'kubectl get pods -l app=student-app -o wide'
            }
        }
    }
    post {
        success {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            echo "Succès : version ${VERSION} sur Nexus, image ${IMAGE}:${TAG} sur Docker Hub."
            echo "Analyse SonarQube : projet student-management-${BRANCHE}"
        }
        failure {
            echo 'Échec : consulter la Console Output du stage en rouge.'
        }
        always {
            sh "docker image rm -f ${IMAGE}:${TAG} || true"
        }
    }
}
