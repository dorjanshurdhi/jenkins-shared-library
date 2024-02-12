import jenkins.model.*
import jenkins.model.Jenkins
import hudson.slaves.*
import org.csanchez.jenkins.plugins.kubernetes.*

def configureKubernetesPlugin(String serverUrl, String credentialsId, String namespace) {

    def jenkinsInstance = Jenkins.getInstance()


    def kubernetesPlugin = jenkinsInstance.getPlugin('kubernetes')

    if (kubernetesPlugin != null) {

        def cloud = new KubernetesCloud('kubernetes-test')
        cloud.setServerUrl(serverUrl)
        cloud.setNamespace(namespace)

        def credentials = new StringBinding(credentialsId)
        cloud.setCredentialsId(credentialsId)

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

        jenkinsInstance.clouds = clouds
        jenkinsInstance.save()
        
        println("Plugin Kubernetes configurato con successo!")
    } else {
        println("Il plugin Kubernetes non è installato.")
    }
}