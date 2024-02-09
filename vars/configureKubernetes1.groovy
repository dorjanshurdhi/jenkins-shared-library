def call(Map config) {
    pipeline {
        agent any
        environment {
            KUBECONFIG = config.kubeConfig
        }
        stages {
            stage('Configure Kubernetes') {
                steps {
                    kubernetesCluster(
                        cloud: config.cloud,
                        defaultPodTemplate: config.defaultPodTemplate,
                        credentialsId: config.credentialsId,
                        namespace: config.namespace,
                        replicas: config.replicas,
                        // Altre opzioni di configurazione
                    )
                }
            }
        }
    }
}