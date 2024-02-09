
def call() {
    //def crumbResponse = sh(script: "curl -s -X GET http://${SERVER}/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,\":\",//crumb) --user admin:admin", returnStdout: true).trim()
    //def crumb = sh(script: "echo \"${crumbResponse}\" | grep -oP '(?<=<crumb>).*(?=<\\/crumb>)' | awk -F: '{print \$2}'", returnStdout: true).trim()
    //return crumb
    //def url = "https://localhost:8080/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,":",//crumb)'"

    sh 'pwd'
    sh 'ls -la'
    //sh(script: "sh ../scripts/getjenkinscrumb.sh")
}
