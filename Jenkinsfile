#!/usr/bin/env groovy

pipeline {
    agent any
    environment {
        def mvn_version = 'M3'
        def pom = readMavenPom file: 'pom.xml'
        def pomVersion = pom.getVersion()
        def uploadSpec = """{
            "files": [
                {
                    "pattern": "target/*.jar",
                    "target": "libs-release-local/br/com/easydoc/berkeleyDBLib/${pomVersion}-${env.BUILD_NUMBER}/"
                },
                {
                    "pattern": "target/*.pom",
                    "target": "libs-release-local/br/com/easydoc/berkeleyDBLib/${pomVersion}-${env.BUILD_NUMBER}/"
                }
            ]
        }"""
    }
    stages {
        stage('Maven Build') {
            steps {
                sh 'mvn clean package -Dmaven.test.skip=true'
            }
        }
        stage('Publish to Artifactory') {
            steps {
                script {
                    def server = Artifactory.newServer url: 'http://nocix01.easydocs.com.br:8081/artifactory', username: 'dev', password: 'AP9FdrbMRBqJPwNCdQfYJ6PJ4qv'
                    server.bypassProxy = true
                    def buildInfo = server.upload spec: uploadSpec
                }
            }
        }
    }
    post { 
        always { 
            cleanWs()
        }
    } 
}
