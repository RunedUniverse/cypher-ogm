pipeline {
	agent any
	tools {
		maven 'maven-latest'
		jdk 'java-1.8.0'
	}
	stages {
		stage('Initialize') {
			steps {
				sh '''
					echo "PATH = ${PATH}"
					echo "M2_HOME = ${M2_HOME}"
				'''
			}
		}
		stage('Update Maven Repo') {
			steps {
				sh 'mvn dependency:resolve'
				sh 'mvn -P install --non-recursive'
				sh 'ls -l target'
			}
		}
		stage('Install Bill of Sources') {
			steps {
				dir(path: 'rogm-sources-bom') {
					sh 'mvn dependency:resolve'
					sh 'mvn -P install --non-recursive'
					sh 'ls -l target'
				}
			}
		}
		stage('Install Bill of Materials') {
			steps {
				dir(path: 'rogm-bom') {
					sh 'mvn -P install --non-recursive'
					sh 'ls -l target'
				}
			}
		}
		stage('Build CORE') {
			steps {
				dir(path: 'rogm-core') {
					sh 'mvn -P install'
					sh 'ls -l target'
				}
			}
		}
		stage('Build Parser') {
			parallel {
				stage('JSON') {
					steps {
						dir(path: 'rogm-parser-json') {
							sh 'mvn -P install'
							sh 'ls -l target'
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
							sh 'mvn -P install'
							sh 'ls -l target'
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
							sh 'mvn -P install'
							sh 'ls -l target'
						}
					}
				}
				//stage('Decorator') {
				//	steps {
				//		dir(path: 'rogm-module-decorator') {
				//			sh 'mvn -P install'
				//		}
				//	}
				//}
			}
		}
		
		stage('License Check') {
			steps {
				sh 'mvn -P license-check,license-prj-utils-approve,license-apache2-approve'
			}
		}
		
		stage('System Test') {
			steps {
				sh 'mvn -P test-junit-jupiter,test-system'
			}
			post {
				always {
					junit '*/target/surefire-reports/*.xml'
				}
				failure {
				    archiveArtifacts artifacts: '*/target/surefire-reports/*.xml'
				}
			}
		}
		stage('Database Test') {
			parallel {
				stage('Neo4J') {
				steps{
				    
					script {
						environment {
							BUILD_TAG_CAPS = sh(returnStdout: true, script: 'echo $BUILD_TAG | tr "[a-z]" "[A-Z]"').trim()
						}
						docker.image('neo4j:latest').withRun(
								'--volume=${WORKSPACE}/src/test/resources/neo4j:/var/lib/neo4j/conf:z ' +
								'--volume=/var/run/neo4j-jenkins-rogm:/run:z'
							) { c ->
							sh 'export JENKINS_ROGM_NEO4J_IP=$(docker inspect -f "{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}" ${c.id} )'
							/* Wait until database service is up */
							sh 'echo waiting for Neo4J[docker:${c.id}]\n\tto start on http://${JENKINS_ROGM_NEO4J_IP}:7474'
							sh 'until $(curl --output /dev/null --silent --head --fail http://${JENKINS_ROGM_NEO4J_IP}:7474); do sleep 5; done'
							/* Prepare Database */
							sh '''
								echo 'Neo4J online > setting up database'
								docker exec ${c.id} cat '/var/lib/neo4j/conf/setup.cypher'
								docker exec ${c.id} cypher-shell -u neo4j -p neo4j -f '/var/lib/neo4j/conf/setup.cypher'
							'''
							/* Run tests */
							sh '''
								echo 'database loaded > starting tests'
								printenv | sort
								mvn -P test-junit-jupiter,test-db-neo4j -Ddbhost=${JENKINS_ROGM_NEO4J_IP} -Ddbuser=neo4j -Ddbpw=neo4j
							'''
						}
					}
				}
				}
			}
			post {
				always {
					junit '*/target/surefire-reports/*.xml'
				}
				failure {
				    archiveArtifacts artifacts: '*/target/surefire-reports/*.xml'
				}
			}
		}

		stage('Deploy') {
			steps {
			    script {
			        switch(GIT_BRANCH) {
			        	case 'master':
			        		sh 'mvn -P repo-releases,deploy-signed -pl -rogm-module-decorator'
			        		break
			        	default:
			        		sh 'mvn -P repo-development,deploy'
			        		break
			    	}
			    }
				archiveArtifacts artifacts: '*/target/*.pom', fingerprint: true
				archiveArtifacts artifacts: '*/target/*.jar', fingerprint: true
				archiveArtifacts artifacts: '*/target/*.asc', fingerprint: true
			}
		}

		stage('Stage at Maven-Central') {
			when {
				branch 'master'
			}
			steps {
				sh 'mvn -P repo-maven-central,deploy-signed -pl -rogm-module-decorator'
			}
		}
	}
	post {
		cleanup {
			cleanWs()
		}
	}
}
