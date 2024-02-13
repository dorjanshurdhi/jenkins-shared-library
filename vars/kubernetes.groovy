import jenkins.model.*
import jenkins.model.Jenkins
import hudson.slaves.*
import org.csanchez.jenkins.plugins.kubernetes.*
import com.cloudbees.plugins.credentials.CredentialsProvider
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

        // Ottieni le credenziali dall'archivio di Jenkins
      ////  def credentials = CredentialsProvider.findCredentialById(configMap['credentialsId'], StandardUsernamePasswordCredentials.class, Jenkins.getInstance(), Collections.emptyList())
////
      ////  if (credentials == null) {
      ////      println("Le credenziali con ID ${configMap['credentialsId']} non sono state trovate.")
      ////      return
      ////  }

      //  cloud.setCredentialsId(credentials.id)

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

            //container.setArgs(String args)
            //container.setCommand(String command)
            //container.setResourceRequestCpu(String resourceRequestCpu)
            //container.setResourceRequestMemory(String resourceRequestMemory)
            //container.setResourceLimitCpu(String resourceLimitCpu)
            //container.setResourceLimitMemory(String resourceLimitMemory)
            //container.setResourceRequestEphemeralStorage(String resourceRequestEphemeralStorage)
            //container.setRunAsGroup(String runAsGroup)
            //container.setRunAsUser(String runAsUser)
            //container.setShell(String shell)
            //container.setTtyEnabled(boolean ttyEnabled)
            //container.setEnvVars(List<TemplateEnvVar> envVars)


            //    // Monta il persistent volume claim nel container
            //    volumeMounts: [
            //        [
            //            name: podTemplateData['volume'].name,
            //            mountPath: podTemplateData['volume'].mountPath
            //        ]
            //    ]
            
           podTemplate.getContainers().add(container)

            // Definisci il persistent volume claim nel podTemplate
           //podTemplate.setVolumes([
           //    [
           //        name: podTemplateData['volume'].name,
           //        persistentVolumeClaim: [
           //            claimName: podTemplateData['volume'].persistentVolumeClaim.claimName
           //        ]
           //    ]
           //])


            cloud.setTemplates([podTemplate])
        }

        // Aggiungi o aggiorna il cloud Kubernetes alla configurazione globale di Jenkins
        def clouds = jenkinsInstance.clouds
        //boolean found = false
        //for (c in clouds) {
        //    if (c instanceof KubernetesCloud) {
        //        if (c.name.equals(configMap['kubernetes-partner-name'])) {
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
        //jenkinsInstance.clouds = clouds
        jenkinsInstance.save()
        
        println("Plugin Kubernetes configurato con successo!")
    } else {
        println("Il plugin Kubernetes non è installato.")
    }
}
