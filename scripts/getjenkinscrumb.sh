#!/bin/bash

#JENKINS_URL='localhost:8080'
USERNAME=dshurdhi
PASSWORD=TEMPO123!

response_get_crumb=$(curl -s --cookie-jar /tmp/cookies -u "admin:admin" -X GET "http://${JENKINS_URL}/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,\":\",//crumb)")
curl -X POST -H "${response_get_crumb}" --cookie /tmp/cookies -d 'newTokenName=test' "${JENKINS_URL}/me/descriptorByName/jenkins.security.ApiTokenProperty/generateNewToken" -u "${USERNAME}:${PASSWORD}"

