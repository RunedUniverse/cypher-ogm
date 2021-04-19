pipeline {
	agent any
	stages {
		stage('Initialize') {
			steps {
				sh '''
					echo "PATH = ${PATH}"
					echo "M2_HOME = ${M2_HOME}"
					JENKINS_ROGM_NEO4J_RES=${WORKSPACE}/src/test/resources/neo4j
					export JENKINS_ROGM_NEO4J_RES
				'''
			}
		}

		stage('Build CORE') {
			steps {
				dir(path: 'rogm-core') {
					sh 'mvn -P jenkins-install'
				}
			}
		}
		stage('Build TEST-LIB') {
			steps {
				dir(path: 'rogm-test') {
					sh 'mvn -P jenkins-install'
				}
			}
		}
		stage('Build Parser') {
			parallel {
				stage('JSON') {
					steps {
						dir(path: 'rogm-parser-json') {
							sh 'mvn -P jenkins-install'
						}
					}
				}
			}
		}
		stage('Build Module') {
			parallel {
				stage('Neo4J') {
					steps {
						dir(path: 'rogm-module-neo4j') {
							sh 'mvn -P jenkins-install'
						}
					}
				}
				stage('Decorator') {
					steps {
						dir(path: 'rogm-module-decorator') {
							sh 'mvn -P jenkins-install'
						}
					}
				}
			}
		}

		stage('Test') {
			parallel {
				stage('Parser JSON') {
					steps {
						dir(path: 'rogm-parser-json') {
							sh 'mvn -P jenkins-test'
						}
					}
				}
				stage('Module Neo4J') {
					steps {
						// start Neo4J
						sh '''
							JENKINS_ROGM_NEO4J_ID=$(docker run -d --volume=${JENKINS_ROGM_NEO4J_RES}:/var/lib/neo4j/conf --volume=/var/run/neo4j-jenkins-rogm:/run neo4j)
							JENKINS_ROGM_NEO4J_IP=$(docker inspect -f "{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}" ${JENKINS_ROGM_NEO4J_ID})
							#printf ${JENKINS_ROGM_NEO4J_ID} > '${JENKINS_ROGM_NEO4J_RES}/pid'
							# make env vars global
							export JENKINS_ROGM_NEO4J_ID
							export JENKINS_ROGM_NEO4J_IP
							printenv | sort
						'''
						dir(path: 'rogm-module-neo4j') {
							sh 'mvn -P jenkins-test -Ddbhost=${JENKINS_ROGM_NEO4J_IP} -Ddbuser=neo4j -Ddbpw=neo4j'
						}
					}
					post {
						always {
							// stop Neo4J
							sh 'docker stop $(cat ${JENKINS_ROGM_NEO4J_RES}/pid)'
							sh 'unset JENKINS_ROGM_NEO4J_ID'
							sh 'unset JENKINS_ROGM_NEO4J_IP'
						}
					}
				}
				stage('Module Decorator') {
					steps {
						dir(path: 'rogm-module-decorator') {
							sh 'mvn -P jenkins-test'
						}
					}
				}
			}
			post {
				always {
					junit '*/target/surefire-reports/*.xml'
					archiveArtifacts artifacts: '*/*.pom', fingerprint: true
				}
			}
		}

		stage('Deploy') {
			steps {
				sh '''
					mvn -P deploy
					unset JENKINS_ROGM_NEO4J_RES
				'''
				archiveArtifacts artifacts: '*/target/*.jar', fingerprint: true
			}
		}

	}
	tools {
		maven 'Maven 3.6.3'
		jdk 'OpenJDK 8'
	}
}
