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
        // Imposta la configurazione del plugin Kubernetes
        def cloud = new KubernetesCloud(configMap['kubernetes-partner-name'])
        cloud.setServerUrl(configMap['serverUrl'])
        cloud.setNamespace(configMap['namespace'])

    ////   // Ottieni le credenziali dall'archivio di Jenkins
    ////   def credentials = CredentialsProvider.findCredentialById(configMap['credentialsId'], StandardUsernamePasswordCredentials.class, Jenkins.getInstance(), Collections.emptyList())

    ////   if (credentials == null) {
    ////       println("Le credenziali con ID ${configMap['credentialsId']} non sono state trovate.")
    ////       return
    ////   }

    ////   cloud.setCredentialsId(credentials.id)

        // Configura podTemplate se presente nei dati
        if (configMap.containsKey('podTemplate')) {
            def podTemplateData = configMap['podTemplate']
            def podTemplate = new PodTemplate()
            podTemplate.setLabel(podTemplateData['label'])
            podTemplate.setName(podTemplateData['name'])
            podTemplate.setNamespace(podTemplateData['namespace'])
            podTemplate.setNodeSelector(podTemplateData['nodeSelector'])
            podTemplate.setInstanceCap(podTemplateData['instanceCap'] as int)
            
            // Configura i containers
            def containers = podTemplateData['containers'] as List<Map<String, Object>>
            containers.each { containerData ->
            println("Container name: ${containerData['name']}")
                def container = new ContainerTemplate(
                    name: containerData['name'],
                    image: containerData['image'],
                    //command: containerData['command'],
                    // args: containerData['args'],
                    //resourceRequestCpu: containerData['resourceRequestCpu'],
                    //resourceRequestMemory: containerData['resourceRequestMemory'],
                    //resourceLimitCpu: containerData['resourceLimitCpu'],
                    //resourceLimitMemory: containerData['resourceLimitMemory'],
                    alwaysPullImage: containerData['alwaysPullImage'] as boolean,
                    workingDir: containerData['workingDir'],
                    envVars: containerData['envVars'] as List<Map<String, String>>,
                    //ports: containerData['ports'] as List<Map<String, Integer>>,
                    //ttyEnabled: containerData['ttyEnabled'] as boolean,
                    privileged: containerData['privileged'] as boolean,
                    //securityContext: containerData['securityContext'] as Map<String, Object>
                )
                podTemplate.getContainers().add(container)
            }

      ////      // Configura i volumes
      ////      def volumes = podTemplateData['volumes'] as List<Map<String, String>>
      ////      volumes.each { volumeData ->
      ////          def volume
      ////          if (volumeData['type'] == 'HostPathVolume') {
      ////              volume = new HostPathVolume(mountPath: volumeData['mountPath'], hostPath: volumeData['hostPath'])
      ////          } else {
      ////              volume = new EmptyDirVolume(mountPath: volumeData['mountPath'])
      ////          }
      ////          podTemplate.getVolumes().add(volume)
      ////      }

      ////      podTemplate.setServiceAccount(podTemplateData['serviceAccount'])
      ////      podTemplate.setAnnotations(podTemplateData['annotations'] as Map<String, String>)
      ////      podTemplate.setImagePullSecrets(podTemplateData['imagePullSecrets'] as List<String>)
      ////      podTemplate.setIdleMinutes(podTemplateData['idleMinutes'] as int)
      ////      podTemplate.setActiveDeadlineSeconds(podTemplateData['activeDeadlineSeconds'] as int)
      ////      podTemplate.setTimeoutSeconds(podTemplateData['timeoutSeconds'] as int)
            
            cloud.setTemplates([podTemplate])
        }

        // Aggiungi o aggiorna il cloud Kubernetes alla configurazione globale di Jenkins
        def clouds = jenkinsInstance.clouds
        boolean found = false
        for (c in clouds) {
            if (c instanceof KubernetesCloud) {
                if (c.name.equals(configMap['kubernetes-partner-name'])) {
                    clouds.remove(c)
                    clouds.add(cloud)
                    found = true
                    break
                }
            }
        }
        if (!found) {
            clouds.add(cloud)
        }

        // Salva le modifiche alla configurazione globale di Jenkins
        jenkinsInstance.clouds = clouds
        jenkinsInstance.save()
        
        println("Plugin Kubernetes configurato con successo!")
    } else {
        println("Il plugin Kubernetes non è installato.")
    }
}
