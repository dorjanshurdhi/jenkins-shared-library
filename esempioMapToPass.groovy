def configMap = [
    'serverUrl': 'https://kubernetes.example.com',
    'namespace': 'my-namespace',
    'credentialsId': 'my-credentials-id',
    'podTemplate': [
        'label': 'my-pod-label',
        'name': 'my-pod-name',
        'namespace': 'my-pod-namespace',
        'nodeSelector': 'my-node-selector',
        'instanceCap': '10',
        'containers': [
            [
                'name': 'my-container-name',
                'image': 'my-container-image',
                'command': 'my-container-command',
                'args': 'my-container-args',
                'resourceRequestCpu': '500m',
                'resourceRequestMemory': '512Mi',
                'resourceLimitCpu': '1',
                'resourceLimitMemory': '1Gi',
                'alwaysPullImage': 'false',
                'workingDir': 'my-container-working-dir',
                'envVars': [
                    ['key': 'MY_ENV_VAR', 'value': 'my-value']
                ],
                'ports': [
                    ['containerPort': '8080', 'hostPort': '8080']
                ],
                'ttyEnabled': 'true',
                'privileged': 'false',
                'securityContext': [
                    'runAsUser': '1000',
                    'runAsGroup': '1000',
                    'fsGroup': '2000'
                ]
            ]
        ],
        'volumes': [
            ['mountPath': '/host/path', 'hostPath': '/path/on/host'],
            ['mountPath': '/empty/dir']
        ],
        'serviceAccount': 'my-service-account',
        'annotations': [
            ['key': 'annotation-key', 'value': 'annotation-value']
        ],
        'imagePullSecrets': ['my-secret'],
        'idleMinutes': '5',
        'activeDeadlineSeconds': '3600',
        'timeoutSeconds': '300'
    ]
]

configureKubernetesPlugin(configMap)



===================


// Esempio di utilizzo della funzione con una mappa
def configMap = [
    'kubernetes-partner-name': 'partner-1',
    'serverUrl': 'https://kubernetes.example.com',
    'namespace': 'devops-tools',
    'credentialsId': 'kubernetes-id',
    'podTemplate': [
        'label': 'kubeagent',
        'name': 'kube-agent',
        'namespace': 'devops-tools',
 //       'nodeSelector': 'my-node-selector',
 //       'instanceCap': 10,
        'containers': [
            [
                'name': 'jnlp',
                'image': 'ghcr.io/acn-backstage-demo/partner-devportal-library/jenkins-agent:1.1.0',
 //               'command': 'my-container-command',
 //              'args': 'my-container-args',
 //               'resourceRequestCpu': '500m',
 //               'resourceRequestMemory': '512Mi',
 //               'resourceLimitCpu': '1',
 //               'resourceLimitMemory': '1Gi',
                'alwaysPullImage': true,
                'workingDir': '/home/jenkins/agent',
                'envVars': [
                    ['key': 'MY_ENV_VAR', 'value': 'my-value']
                ]//,
 //               'ports': [
 //                   ['containerPort': 8080, 'hostPort': 8080]
 //              ],
 //               'ttyEnabled': true,
                'privileged': false,
 //               'securityContext': [
 //                   'runAsUser': 1000,
 //                   'runAsGroup': 1000,
  //                  'fsGroup': 2000
   //            ] 
            ]
        ]//,
   //     'volumes': [
   //         ['type': 'HostPathVolume', 'mountPath': '/host/path', 'hostPath': '/path/on/host'],
   //         ['type': 'EmptyDirVolume', 'mountPath': '/empty/dir']
   //     ],
   //     'serviceAccount': 'my-service-account',
   //     'annotations': ['annotation-key': 'annotation-value'],
   //     'imagePullSecrets': ['my-secret'],
   //     'idleMinutes': 5,
   //     'activeDeadlineSeconds': 3600,
   //     'timeoutSeconds': 300
    ]
]

configureKubernetesPlugin(configMap)
