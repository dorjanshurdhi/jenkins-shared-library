import java.time.Instant

def call(Map<String, Object> configMap) {
    
    def VERSION = Instant.now().toEpochMilli()
    pipeline {
        agent {
            node {
                label 'rust-agent'
            }
        }
    
        stages {
            stage('Build') {
                steps {
                    sh 'mkdir -p container/tmp'
                    sh 'wasm-pack build entity'
                    sh 'cp -r entity/pkg container/tmp'
                }
            }  
    
            stage('SonarQube Analysis') {
                steps {
                    script {
                        withSonarQubeEnv([intallationName: "SonarQube", envOnly: true]) {
                            dir('entity'){
                                sh 'cargo clean'
                                sh 'cargo clippy --message-format=json > my-clippy-report.json'
                                sh 'sonar-scanner -Dproject.settings=./sonar-project.properties'
                            }
                        }
                    }
                }
            }
    
            stage('Unit Tests') {
                steps {
                    dir('entity'){
                        sh 'cargo test'
                    }
                } 
            }

            stage('Build container') {
                steps {
                    script 
                        sh "buildah --version"
                        sh "buildah  bud -f ./container/Dockerfile -t ${configMap.harborRemoteHost}/${configMap.entityName}/${configMap.appName}:${VERSION}"
                    }
                    
                }
            }
    
            stage('Push to local harbor') {
                steps {
                    script {
                        withCredentials([usernamePassword(credentialsId: ${configMap.harborCredentialsId}, usernameVariable: 'HARBOR_USERNAME', passwordVariable: 'HARBOR_PASSWORD')]) {
                            sh "buildah login --tls-verify=false ${configMap.harborLocalHost} -u ${HARBOR_USERNAME} -p ${HARBOR_PASSWORD}"
                            sh "buildah push --tls-verify=false ${configMap.harborLocalHost}/${configMap.entityName}/${configMap.appName}:${VERSION}"
                        }
                    }
                }
            }
    
            stage('Techdocs generation') {
                steps {
                    sh 'techdocs-cli generate --source-dir . --output-dir tmp/techdocs --no-docker --verbose'
                }
            }
        
            stage('Techdocs publish') {
                steps {
                    script {
                        withCredentials([usernamePassword(credentialsId: ${configMap.awsCredentialsId}, usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) { 
                            sh "techdocs-cli publish --publisher-type 'awsS3' --directory tmp/techdocs --awsEndpoint ${configMap.minioBucketEndpoint} --storage-name ${configMap.minioBucketName} --awsS3ForcePathStyle true --entity ${configMap.entityNamespace}/${configMap.entityKindPath}/${configMap.appName}"
                        }
                    }
                }
            }
    
            stage('Avvia Job di Sign Images') {
                steps {
                    script {
                        def signImageParams = [
                            string(name: 'VERSION', value: "${VERSION}")
                        ]
                        build job: 'sign-partner-devportal-rust-component-test', parameters: signImageParams
                        return
                    }
                }
            }
        }
    }
}
