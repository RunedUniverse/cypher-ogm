pipeline {
	agent any
	stages {

		stage('Build CORE') {
			steps {
				dir(path: 'rogm-core') {
					sh 'mvn dependency:resolve'
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
		stage('Build Languages') {
			parallel {
				stage('Cypher') {
					steps {
						dir(path: 'rogm-lang-cypher') {
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
				stage('System') {
					steps {
						sh 'mvn -P jenkins-test-system'
					}
				}
				stage('Database Neo4J') {
					environment {
						BUILD_TAG_CAPS= sh(returnStdout: true, script: 'echo $BUILD_TAG | tr "[a-z]" "[A-Z]"').trim()
						// start Neo4J
						JENKINS_ROGM_NEO4J_ID= sh(returnStdout: true, script: 'docker run -d --volume=${WORKSPACE}/src/test/resources/neo4j:/var/lib/neo4j/conf --volume=/var/run/neo4j-jenkins-rogm:/run --name=$(echo $BUILD_TAG | tr "[a-z]" "[A-Z]") neo4j').trim()
					}
					steps {
						sh '''
							JENKINS_ROGM_NEO4J_IP=$(docker inspect -f "{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}" $JENKINS_ROGM_NEO4J_ID)
							echo waiting for Neo4J[docker:$BUILD_TAG_CAPS] to start on $JENKINS_ROGM_NEO4J_IP
							until $(curl --output /dev/null --silent --head --fail http://$JENKINS_ROGM_NEO4J_IP:7474); do sleep 5; done
							echo 'Neo4J online > setting up database'
							docker exec $JENKINS_ROGM_NEO4J_ID cat '/var/lib/neo4j/conf/setup.cypher'
							docker exec $JENKINS_ROGM_NEO4J_ID cypher-shell -u neo4j -p neo4j -f '/var/lib/neo4j/conf/setup.cypher'
							echo 'database loaded > starting tests'
							printenv | sort
							mvn -P jenkins-test-db-neo4j -Ddbhost=$JENKINS_ROGM_NEO4J_IP -Ddbuser=neo4j -Ddbpw=neo4j
						'''
					}
					post {
						always {
							// stop Neo4J
							sh '''
								docker stop $JENKINS_ROGM_NEO4J_ID
								docker rm $JENKINS_ROGM_NEO4J_ID
								echo 'Docker: stop|rm: $BUILD_TAG_CAPS'
							'''
						}
					}
				}
			}
			post {
				always {
					junit '*/target/surefire-reports/*.xml'
				}
			}
		}

		stage('Deploy') {
			steps {
				sh 'mvn -P jenkins-deploy'
				archiveArtifacts artifacts: '*/target/*.jar', fingerprint: true
			}
		}

	}
	tools {
		maven 'Maven 3.6.3'
		jdk 'OpenJDK 8'
	}
}
