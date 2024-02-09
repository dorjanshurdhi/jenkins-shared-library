def call(String kubeConfig, String kubeCredentialId, String namespace) {
    pipeline {
        agent any
        environment {
            KUBECONFIG = kubeConfig
        }
        stages {
            stage('Configure Kubernetes') {
                steps {
                    kubernetesCluster(
                        cloud: 'my-kubernetes',
                        defaultPodTemplate: 'my-pod-template',
                        credentialsId: kubeCredentialId,
                        namespace: namespace
                    )
                }
            }
        }
    }
}