def REGISTRY = "host.docker.internal:5050"

def SERVICES = [
    [
        name:     'gateway-service',
        dir:      'gateway-service',
        hasTests: false
    ],
    [
        name:     'problem-service',
        dir:      'problem-service',
        hasTests: true
    ],
]

pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/danyazero/LeetCode.git'
            }
        }

        stage('Build & Deploy') {
            steps {
                script {
                    for (svc in SERVICES) {
                        dir(svc.dir) {
                            stage("${svc.name}: build") {
                                sh '''
                                    ./gradlew clean bootJar --no-daemon
                                    mkdir -p build/dependency
                                    cd build/dependency && jar xf $(ls -t ../libs/*.jar | head -1)
                                '''
                            }

                            if (svc.hasTests) {
                                stage("${svc.name}: test") {
                                    sh './gradlew test --no-daemon'
                                    junit 'build/test-results/**/*.xml'
                                }
                            }

                            stage("${svc.name}: deploy") {
                                sh """
                                    docker build -t ${REGISTRY}/${svc.name}:latest .
                                    docker push ${REGISTRY}/${svc.name}:latest
                                """
                            }
                        }
                    }
                }
            }
        }
    }
}
