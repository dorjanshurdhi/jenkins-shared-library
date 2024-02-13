import jenkins.model.*
import jenkins.model.Jenkins
import hudson.slaves.*
import org.csanchez.jenkins.plugins.kubernetes.*
import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials

def call(Map<String, String> configMap) {
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
            // Altre configurazioni di PodTemplate...
            cloud.setTemplates([podTemplate])
        }

        // Aggiungi o aggiorna il cloud Kubernetes alla configurazione globale di Jenkins
        def clouds = jenkinsInstance.clouds
        boolean found = false
        for (c in clouds) {
            if (c instanceof KubernetesCloud) {
                if (c.name.equals('kubernetes')) {
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
        //jenkinsInstance.clouds = clouds
        jenkinsInstance.save()
        
        println("Plugin Kubernetes configurato con successo!")
    } else {
        println("Il plugin Kubernetes non è installato.")
    }
}
