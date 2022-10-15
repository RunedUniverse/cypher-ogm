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
						docker.image('neo4j:latest').withRun(
								'-p 172.16.0.1:7474:7474 ' +
								'-p 172.16.0.1:7687:7687 ' +
								'--volume=${WORKSPACE}/src/test/resources/neo4j:/var/lib/neo4j/conf:z ' +
								'--volume=/var/run/neo4j-jenkins-rogm:/run:z'
							) { c ->
							
							/* Wait until database service is up */
							sh 'echo waiting for Neo4J to start'
							sh 'until $(curl --output /dev/null --silent --head --fail http://172.16.0.1:7474); do sleep 5; done'
		
							docker.image('neo4j:latest').inside("--link ${c.id}:database") {
								/* Prepare Database */
								// cypher-shell -u neo4j -p neo4j -f '/var/lib/neo4j/conf/setup.cypher'
								sh	'echo Neo4J online > setting up database'
								sh	'JAVA_HOME=/opt/java/openjdk cypher-shell -a "neo4j://database:7687" -u neo4j -p neo4j -f "./src/test/resources/neo4j/setup.cypher"'
								}
							
							/* Run tests */
							sh '''
								echo 'database loaded > starting tests'
								printenv | sort
								mvn -P test-junit-jupiter,test-db-neo4j -Ddbhost=172.16.0.1 -Ddbuser=neo4j -Ddbpw=neo4j
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
