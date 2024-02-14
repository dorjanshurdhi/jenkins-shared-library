import jenkins.model.*
import hudson.slaves.*
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.csanchez.jenkins.plugins.kubernetes.*
import com.cloudbees.hudson.plugins.folder.*
import com.cloudbees.plugins.credentials.CredentialsMatchers
import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials


def call(Map<String, Object> configMap) {
    // Ottieni l'istanza di Jenkins
    def jenkinsInstance = Jenkins.getInstance()

    // Ottieni il plugin Kubernetes
    def kubernetesPlugin = jenkinsInstance.getPlugin('kubernetes')


    // Verifica se il plugin è installato
    if (kubernetesPlugin != null) {
        // Verifica se esiste già un cloud Kubernetes con lo stesso nome
        def kubernetesName = configMap['kubernetesPartnerName']
        def existingCloud = jenkinsInstance.clouds.find { cloud ->
         cloud instanceof org.csanchez.jenkins.plugins.kubernetes.KubernetesCloud && cloud.name == kubernetesName
        }

        // Se esiste già un cloud Kubernetes con lo stesso nome, stampa un messaggio e interrompi lo script
        if (existingCloud) {
            println "Il cloud Kubernetes '${kubernetesName}' è già configurato."
            unstable(message: "Il cloud Kubernetes '${kubernetesName}' è già configurato.")
            currentBuild.result = 'UNSTABLE'
            return
        } else {
            //crea nuova cartella per il partner
            jenkinsInstance.createProject(Folder.class, kubernetesName)
        }

        // Imposta la configurazione del plugin Kubernetes
        def cloud = new KubernetesCloud(configMap['kubernetesPartnerName'])
        cloud.setServerUrl(configMap['serverUrl'])
        cloud.setNamespace(configMap['namespace'])
        cloud.setUseJenkinsProxy(configMap['useJenkinsProxy'])
        //cloud.setServerCertificate(configMap['serverCertificate'])
        cloud.setCredentialsId(configMap['credentialsId'])
        cloud.setSkipTlsVerify(configMap['skipTlsVerify'])
        cloud.setJenkinsUrl(configMap['jenkinsURL'])
        cloud.setJenkinsTunnel(configMap['jenkinsTunnel'])
        //cloud.setConnectionTimeout(configMap['connectionTimeout'])
        //cloud.setReadTimeout(configMap['readTimeout'])
        //cloud.setConcurrencyLimit(configMap['concurrencyLimit'])
        
        def podLabels = []
        def label1 = new PodLabel('jenkins', 'slave')
        def label2 = new PodLabel('jenkins', 'agent')
        podLabels.add(label1)
        podLabels.add(label2)
        cloud.setPodLabels(podLabels)

        // Configura podTemplate se presente nei dati
        if (configMap.containsKey('podTemplate')) {
            def podTemplateData = configMap['podTemplate']
            def podTemplate = new PodTemplate()
            podTemplate.setLabel(podTemplateData['label'])
            podTemplate.setName(podTemplateData['name'])
            podTemplate.setNamespace(podTemplateData['namespace'])
            //podTemplate.setNodeSelector(podTemplateData['nodeSelector'])
            //podTemplate.setInstanceCap(podTemplateData['instanceCap'] as int)
            
            // Configura il container nel podTemplate
            def containerData = podTemplateData['container']
            def container = new ContainerTemplate(
                containerData['name'],
                containerData['image']
            )
            container.setAlwaysPullImage(containerData['alwaysPullImage'])
            container.setPrivileged(containerData['privileged'])
            container.setWorkingDir(containerData['workingDir'])
            //container.setCommand(containerData['command'])
            //container.setArgs(containerData['args'])
            container.setTtyEnabled(containerData['ttyEnabled'])
            
            //container.setResourceRequestCpu(String resourceRequestCpu)
            //container.setResourceRequestMemory(String resourceRequestMemory)
            //container.setResourceLimitCpu(String resourceLimitCpu)
            //container.setResourceLimitMemory(String resourceLimitMemory)
            //container.setResourceRequestEphemeralStorage(String resourceRequestEphemeralStorage)
            //container.setRunAsGroup(String runAsGroup)
            //container.setRunAsUser(String runAsUser)
            //container.setShell(String shell)
            //container.setEnvVars(List<TemplateEnvVar> envVars)
            // Creare una lista di oggetti TemplateEnvVar
            
            podTemplate.getContainers().add(container)
            cloud.setTemplates([podTemplate])
        }

        // Aggiungi o aggiorna il cloud Kubernetes alla configurazione globale di Jenkins
        // Per permettere di fare modifiche al cluter già configurato scommentare il codice sotto e commentare il
        // blocco che va dalla riga 26-31
        def clouds = jenkinsInstance.clouds
        //boolean found = false
        //for (c in clouds) {
        //    if (c instanceof KubernetesCloud) {
        //        if (c.name.equals(configMap['kubernetesPartnerName'])) {
        //            clouds.remove(c)
        //            clouds.add(cloud)
        //            found = true
        //            break
        //        }
        //    }
        //}
        //if (!found) {
        //    clouds.add(cloud)
        //}
        clouds.add(cloud)

        // Salva le modifiche alla configurazione globale di Jenkins
        jenkinsInstance.save()
        
        println("Plugin Kubernetes configurato con successo!")
    } else {
        println("Il plugin Kubernetes non è installato.")
    }
}