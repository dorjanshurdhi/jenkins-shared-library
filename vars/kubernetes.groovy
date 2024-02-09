import org.csanchez.jenkins.plugins.kubernetes.*
import jenkins.model.*

def j = Jenkins.getInstance()

def k = new KubernetesCloud(
  'jenkins-test',
  null,
  'https://130.211.146.130',
  'default',
  'https://citest.lsst.codes/',
  '10', 0, 0, 5
)
k.setSkipTlsVerify(true)
k.setCredentialsId('ec5cf56b-71e9-4886-9f03-42934a399148')

def p = new PodTemplate('centos:6', null)
p.setName('centos6')
p.setLabel('centos6-docker')
p.setRemoteFs('/home/jenkins')

k.addTemplate(p)

p = new PodTemplate('lsstsqre/centos:7-docker', null)
p.setName('centos7')
p.setLabel('centos7-docker')
p.setRemoteFs('/home/jenkins')

k.addTemplate(p)

j.clouds.replace(k)
j.save()