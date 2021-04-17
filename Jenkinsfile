pipeline {
	agent any
	stages {
		stage('Initialize') {
			steps {
				sh '''
					echo "PATH = ${PATH}"
					echo "M2_HOME = ${M2_HOME}"
				'''
			}
		}
/* JUMP BUILD FOR TEST PURPUOSES
		stage('Build CORE') {
			steps {
				dir(path: 'rogm-core') {
					sh 'mvn -DskipTests clean compile install'
				}
			}
		}

		stage('Build TEST-LIB') {
			steps {
				dir(path: 'rogm-test') {
					sh 'mvn -DskipTests clean compile install'
				}
			}
		}

		stage('Build Parser') {
			parallel {
				stage('JSON') {
					steps {
						dir(path: 'rogm-parser-json') {
							sh 'mvn -DskipTests clean compile install'
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
							sh 'mvn -DskipTests clean compile install'
						}
					}
				}
				stage('Decorator') {
					steps {
						dir(path: 'rogm-module-decorator') {
							sh 'mvn -DskipTests clean compile install'
						}
					}
				}
			}
		}
*/
		stage('Test') {
			steps {
				dir(path: 'rogm-parser-json') {
					sh 'mvn test'
				}
				dir(path: 'rogm-module-neo4j') {
					sh '''
						# setup environment
						# start Neo4J
						export JENKINS_ROGM_TEST_NEO4J_ID=$(docker run -d \
																--volume=${WORKSPACE}/src/test/resources/neo4j-conf:/var/lib/neo4j/conf \
																--volume=/var/run/neo4j-jenkins-rogm:/run \
																neo4j)
						export JENKINS_ROGM_TEST_NEO4J_IP=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' ${JENKINS_ROGM_TEST_NEO4J_ID})

						# run tests
						mvn test

						# stop database
						docker stop ${JENKINS_ROGM_TEST_NEO4J_ID}

						# clean environment
						unset JENKINS_ROGM_TEST_NEO4J_ID
						unset JENKINS_ROGM_TEST_NEO4J_IP
					'''
				}
				dir(path: 'rogm-module-decorator') {
					sh 'mvn test'
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
				sh 'mvn deploy'
				archiveArtifacts artifacts: '*/target/*.jar', fingerprint: true
			}
		}

	}
	tools {
		maven 'Maven 3.6.3'
		jdk 'OpenJDK 8'
	}
}
