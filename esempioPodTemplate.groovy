def podTemplate = new PodTemplate(
    label: 'my-pod-label',
    name: 'my-pod-name',
    namespace: 'my-pod-namespace',
    nodeSelector: 'my-node-selector',
    instanceCap: 10,
    containers: [
        new ContainerTemplate(
            name: 'my-container-name',
            image: 'my-container-image',
            command: 'my-container-command',
            args: 'my-container-args',
            resourceRequestCpu: '500m',
            resourceRequestMemory: '512Mi',
            resourceLimitCpu: '1',
            resourceLimitMemory: '1Gi',
            alwaysPullImage: false,
            workingDir: 'my-container-working-dir',
            envVars: [
                new KeyValueEnvVar(key: 'MY_ENV_VAR', value: 'my-value')
            ],
            ports: [
                new PortMapping(containerPort: 8080, hostPort: 8080)
            ],
            ttyEnabled: true,
            privileged: false,
            securityContext: new PodSecurityContext(runAsUser: 1000, runAsGroup: 1000, fsGroup: 2000)
        )
    ],
    volumes: [
        new HostPathVolume(mountPath: '/host/path', hostPath: '/path/on/host'),
        new EmptyDirVolume(mountPath: '/empty/dir')
    ],
    serviceAccount: 'my-service-account',
    annotations: [
        new KeyValue(key: 'annotation-key', value: 'annotation-value')
    ],
    imagePullSecrets: ['my-secret'],
    idleMinutes: 5,
    activeDeadlineSeconds: 3600,
    timeoutSeconds: 300
)