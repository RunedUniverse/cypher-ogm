pipeline {
	agent any
	stages {
		stage('Initialize') {
			steps {
				sh '''
					echo "PATH = ${PATH}"
					echo "M2_HOME = ${M2_HOME}"
					echo "NEO4J_CONF = ${WORKSPACE}/src/test/resources"
					echo "NEO4J_HOME = /var/lib/neo4j"
				'''
			}
		}

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

		stage('Test') {
			/*pre {
				sh '/usr/share/neo4j/bin/neo4j'
			}*/
			steps {
				dir(path: 'rogm-parser-json') {
					sh 'mvn test'
				}
				dir(path: 'rogm-module-neo4j') {
					sh 'mvn test'
				}
				dir(path: 'rogm-module-decorator') {
					sh 'mvn test'
				}
			}
			post {
				always {
					sh ''
					junit '*/target/surefire-reports/*.xml'
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
