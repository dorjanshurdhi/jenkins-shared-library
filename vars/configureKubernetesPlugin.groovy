import jenkins.model.*
import jenkins.model.Jenkins
import hudson.slaves.*
import org.csanchez.jenkins.plugins.kubernetes.*
import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.CredentialsMatchers
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials

def call(String serverUrl, String credentialsId, String namespace) {
    // Ottieni l'istanza di Jenkins
    def jenkinsInstance = Jenkins.getInstance()

    // Ottieni il plugin Kubernetes
    def kubernetesPlugin = jenkinsInstance.getPlugin('kubernetes')

    // Verifica se il plugin è installato
    if (kubernetesPlugin != null) {
        // Imposta la configurazione del plugin Kubernetes
        def cloud = new KubernetesCloud('kubernetes-test')
        cloud.setServerUrl(serverUrl)
        cloud.setNamespace(namespace)

        // Ottieni le credenziali dall'archivio di Jenkins
       // def credentials = CredentialsProvider.findCredentialById(credentialsId, StandardUsernamePasswordCredentials.class, Jenkins.getInstance(), Collections.emptyList())

      //  if (credentials == null) {
      //      println("Le credenziali con ID $credentialsId non sono state trovate.")
      //      return
      //  }

      //  cloud.setCredentialsId(credentials.id)

        // Aggiungi o aggiorna il cloud Kubernetes alla configurazione globale di Jenkins
        def clouds = jenkinsInstance.clouds
        boolean found = false
        for (c in clouds) {
            if (c instanceof KubernetesCloud) {
                if (c.name.equals('kubernetes-test')) {
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
