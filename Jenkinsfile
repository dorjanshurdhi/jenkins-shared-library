@Library('jenkins-shared-library') _

pipeline{
    agent {
        node {
            label 'master'
        }
    }
    stages{
        stage('Test Shared Libs'){
            steps{
                script {
                    def configMap = [
                       'kubernetesPartnerName': "${params.kubernetesPartnerName}",
                      // 'serverCertificate': '''
                      //          COPY VALID KUBERNETES CERTIFICATE HERE:
                      //              ''',
                       'serverUrl': "${params.serverUrl}",
                       'namespace': "${params.namespace}",
                       'credentialsId': "kubernetes-id",
                       'useJenkinsProxy': false,
                       'skipTlsVerify': false,
                       'jenkinsURL': "${params.jenkinsURL}",
                       'jenkinsTunnel': "${params.jenkinsTunnel}",
                       'podTemplate': [
                           'label': 'kubeagent',
                           'name': 'kube-agent',
                           'namespace': 'devops-tools',
                           'container': [ 
                                'name': 'jnlp',
                                'image': 'ghcr.io/acn-backstage-demo/partner-devportal-library/jenkins-agent:1.1.0',
                                'alwaysPullImage': true,
                                'privileged': true,
                                'workingDir': 'home/jenkins/agent',
                                'command': 'sleep',
                                'args': '36000',
                                'ttyEnabled': true',
                                'envVars': [
                                    ['key': 'MY_ENV_VAR', 'value': 'my-value']
                                ]
                            ]
                        ]
                    ]
                    kubernetes(configMap)                
                }
            }
        }
    }
}