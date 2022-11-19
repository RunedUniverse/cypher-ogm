pipeline {
	agent any
	options {
		throttleJobProperty(
			categories: ['runeduniverse-rogm'],
			throttleEnabled: true,
			throttleOption: 'category'
		)
	}
	tools {
		maven 'maven-latest'
	}
	environment {
		PATH = """${sh(
				returnStdout: true,
				script: 'chmod +x $WORKSPACE/.build/*; printf $WORKSPACE/.build:$PATH'
			)}"""

		GLOBAL_MAVEN_SETTINGS = """${sh(
				returnStdout: true,
				script: 'printf /srv/jenkins/.m2/global-settings.xml'
			)}"""
		MAVEN_SETTINGS = """${sh(
				returnStdout: true,
				script: 'printf $WORKSPACE/.mvn/settings.xml'
			)}"""
		MAVEN_TOOLCHAINS = """${sh(
				returnStdout: true,
				script: 'printf $WORKSPACE/.mvn/toolchains.xml'
			)}"""
		REPOS = """${sh(
				returnStdout: true,
				script: 'REPOS=repo-releases; if [ $GIT_BRANCH != master ]; then REPOS=$REPOS,repo-development; fi; printf $REPOS'
			)}"""

		CHANGES_ROGM_PARENT = """${sh(
				returnStdout: true,
				script: '.build/git-check-for-change pom.xml rogm-parent'
			)}"""
		CHANGES_ROGM_BOM = """${sh(
				returnStdout: true,
				script: '.build/git-check-for-change rogm-bom/pom.xml rogm-bom'
			)}"""
		CHANGES_ROGM_SOURCES_BOM = """${sh(
				returnStdout: true,
				script: '.build/git-check-for-change rogm-sources-bom/pom.xml rogm-sources-bom'
			)}"""
		CHANGES_ROGM_CORE = """${sh(
				returnStdout: true,
				script: '.build/git-check-for-change rogm-core/pom.xml rogm-core'
			)}"""
		CHANGES_ROGM_PARSER_JSON = """${sh(
				returnStdout: true,
				script: '.build/git-check-for-change rogm-parser-json/pom.xml rogm-parser-json'
			)}"""
		CHANGES_ROGM_LANG_CYPHER = """${sh(
				returnStdout: true,
				script: '.build/git-check-for-change rogm-lang-cypher/pom.xml rogm-lang-cypher'
			)}"""
		CHANGES_ROGM_MODULE_NEO4J = """${sh(
				returnStdout: true,
				script: '.build/git-check-for-change rogm-module-neo4j/pom.xml rogm-module-neo4j'
			)}"""
		//CHANGES_ROGM_MODULE_DECORATOR = """${sh(
		//		returnStdout: true,
		//		script: '.build/git-check-for-change rogm-module-decorator/pom.xml rogm-module-decorator'
		//	)}"""
	}
	stages {
		stage('Initialize') {
			steps {
				sh 'echo "PATH = ${PATH}"'
				sh 'echo "M2_HOME = ${M2_HOME}"'
				sh 'printenv | sort'
			}
		}
		stage('Update Maven Repo') {
			when {
				anyOf {
					environment name: 'CHANGES_ROGM_PARENT', value: '1'
					environment name: 'CHANGES_ROGM_BOM', value: '1'
					environment name: 'CHANGES_ROGM_SOURCES_BOM', value: '1'
					environment name: 'CHANGES_ROGM_CORE', value: '1'
					environment name: 'CHANGES_ROGM_PARSER_JSON', value: '1'
					environment name: 'CHANGES_ROGM_LANG_CYPHER', value: '1'
					environment name: 'CHANGES_ROGM_MODULE_NEO4J', value: '1'
					environment name: 'CHANGES_ROGM_MODULE_DECORATOR', value: '1'
				}
			}
			steps {
				sh 'mvn-dev -P ${REPOS} dependency:purge-local-repository -DactTransitively=false -DreResolve=false --non-recursive'
				sh 'mvn-dev -P ${REPOS} dependency:resolve --non-recursive'
				sh 'mkdir -p target/result/'
			}
		}
		stage('Install ROGM Parent') {
			when {
				environment name: 'CHANGES_ROGM_PARENT', value: '1'
			}
			steps {
				sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install --non-recursive'
				sh 'mkdir -p target/result/'
				sh 'ls -l target/'
			}
			post {
				always {
					dir(path: 'target') {
						archiveArtifacts artifacts: '*.pom', fingerprint: true
						archiveArtifacts artifacts: '*.asc', fingerprint: true
						sh 'cp *.pom *.asc result/'
					}
				}
			}
		}
		stage('Install Bill of Sources') {
			when {
				environment name: 'CHANGES_ROGM_SOURCES_BOM', value: '1'
			}
			steps {
				dir(path: 'rogm-sources-bom') {
					sh 'mvn-dev -P ${REPOS} dependency:resolve  --non-recursive'
					sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install --non-recursive'
					sh 'ls -l target/'
				}
			}
			post {
				always {
					dir(path: 'rogm-sources-bom/target') {
						archiveArtifacts artifacts: '*.pom', fingerprint: true
						archiveArtifacts artifacts: '*.asc', fingerprint: true
						sh 'cp *.pom *.asc ../../target/result/'
					}
				}
			}
		}
		stage('Install Bill of Materials') {
			when {
				environment name: 'CHANGES_ROGM_BOM', value: '1'
			}
			steps {
				dir(path: 'rogm-bom') {
					sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install --non-recursive'
					sh 'ls -l target/'
				}
			}
			post {
				always {
					dir(path: 'rogm-bom/target') {
						archiveArtifacts artifacts: '*.pom', fingerprint: true
						archiveArtifacts artifacts: '*.asc', fingerprint: true
						sh 'cp *.pom *.asc ../../target/result/'
					}
				}
			}
		}
		stage('Build CORE') {
			when {
				environment name: 'CHANGES_ROGM_CORE', value: '1'
			}
			steps {
				dir(path: 'rogm-core') {
					sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install --non-recursive'
					sh 'ls -l target/'
				}
			}
			post {
				always {
					dir(path: 'rogm-core/target') {
						archiveArtifacts artifacts: '*.pom', fingerprint: true
						archiveArtifacts artifacts: '*.asc', fingerprint: true
						sh 'cp *.pom *.jar *.asc ../../target/result/'
					}
				}
			}
		}
		stage('Build Parser') {
			parallel {
				stage('JSON') {
					when {
						environment name: 'CHANGES_ROGM_PARSER_JSON', value: '1'
					}
					steps {
						dir(path: 'rogm-parser-json') {
							sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install --non-recursive'
							sh 'ls -l target/'
						}
					}
					post {
						always {
							dir(path: 'rogm-parser-json/target') {
								archiveArtifacts artifacts: '*.pom', fingerprint: true
								archiveArtifacts artifacts: '*.jar', fingerprint: true
								archiveArtifacts artifacts: '*.asc', fingerprint: true
								sh 'cp *.pom *.jar *.asc ../../target/result/'
							}
						}
					}
				}
			}
		}
		stage('Build Languages') {
			parallel {
				stage('Cypher') {
					when {
						environment name: 'CHANGES_ROGM_LANG_CYPHER', value: '1'
					}
					steps {
						dir(path: 'rogm-lang-cypher') {
							sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install --non-recursive'
							sh 'ls -l target/'
						}
					}
					post {
						always {
							dir(path: 'rogm-lang-cypher/target') {
								archiveArtifacts artifacts: '*.pom', fingerprint: true
								archiveArtifacts artifacts: '*.jar', fingerprint: true
								archiveArtifacts artifacts: '*.asc', fingerprint: true
								sh 'cp *.pom *.jar *.asc ../../target/result/'
							}
						}
					}
				}
			}
		}
		stage('Build Module') {
			parallel {
				stage('Neo4J') {
					when {
						environment name: 'CHANGES_ROGM_MODULE_NEO4J', value: '1'
					}
					steps {
						dir(path: 'rogm-module-neo4j') {
							sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install --non-recursive'
							sh 'ls -l target/'
						}
					}
					post {
						always {
							dir(path: 'rogm-module-neo4j/target') {
								archiveArtifacts artifacts: '*.pom', fingerprint: true
								archiveArtifacts artifacts: '*.jar', fingerprint: true
								archiveArtifacts artifacts: '*.asc', fingerprint: true
								sh 'cp *.pom *.jar *.asc ../../target/result/'
							}
						}
					}
				}
				//stage('Decorator') {
				//	when {
				//		environment name: 'CHANGES_ROGM_MODULE_DECORATOR', value: '1'
				//	}
				//	steps {
				//		dir(path: 'rogm-module-decorator') {
				//			sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,install --non-recursive'
				//			sh 'ls -l target/'
				//		}
				//	}
				//	post {
				//		always {
				//			dir(path: 'rogm-module-decorator/target') {
				//				archiveArtifacts artifacts: '*.pom', fingerprint: true
				//				archiveArtifacts artifacts: '*.jar', fingerprint: true
				//				archiveArtifacts artifacts: '*.asc', fingerprint: true
				//				sh 'cp *.pom *.jar *.asc ../../target/result/'
				//			}
				//		}
				//	}
				//}
			}
		}

		stage('License Check') {
			when {
				anyOf {
					environment name: 'CHANGES_ROGM_PARENT', value: '1'
					environment name: 'CHANGES_ROGM_BOM', value: '1'
					environment name: 'CHANGES_ROGM_SOURCES_BOM', value: '1'
					environment name: 'CHANGES_ROGM_CORE', value: '1'
					environment name: 'CHANGES_ROGM_PARSER_JSON', value: '1'
					environment name: 'CHANGES_ROGM_LANG_CYPHER', value: '1'
					environment name: 'CHANGES_ROGM_MODULE_NEO4J', value: '1'
					environment name: 'CHANGES_ROGM_MODULE_DECORATOR', value: '1'
				}
			}
			steps {
				sh 'mvn-dev -P ${REPOS},license-check,license-prj-utils-approve,license-apache2-approve'
			}
		}

		stage('System Test') {
			when {
				anyOf {
					environment name: 'CHANGES_ROGM_PARENT', value: '1'
					environment name: 'CHANGES_ROGM_BOM', value: '1'
					environment name: 'CHANGES_ROGM_SOURCES_BOM', value: '1'
					environment name: 'CHANGES_ROGM_CORE', value: '1'
					environment name: 'CHANGES_ROGM_PARSER_JSON', value: '1'
					environment name: 'CHANGES_ROGM_LANG_CYPHER', value: '1'
					environment name: 'CHANGES_ROGM_MODULE_NEO4J', value: '1'
					environment name: 'CHANGES_ROGM_MODULE_DECORATOR', value: '1'
				}
			}
			steps {
				sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,test-junit-jupiter,test-system'
			}
			post {
				success {
					junit '*/target/surefire-reports/*.xml'
				}
				failure {
					junit '*/target/surefire-reports/*.xml'
					archiveArtifacts artifacts: '*/target/surefire-reports/*.xml'
				}
			}
		}
		stage('Database Test') {
			parallel {
			
				stage('Neo4J') {
					when {
						anyOf {
							environment name: 'CHANGES_ROGM_PARENT', value: '1'
							environment name: 'CHANGES_ROGM_BOM', value: '1'
							environment name: 'CHANGES_ROGM_SOURCES_BOM', value: '1'
							environment name: 'CHANGES_ROGM_CORE', value: '1'
							environment name: 'CHANGES_ROGM_PARSER_JSON', value: '1'
							environment name: 'CHANGES_ROGM_LANG_CYPHER', value: '1'
							environment name: 'CHANGES_ROGM_MODULE_NEO4J', value: '1'
							environment name: 'CHANGES_ROGM_MODULE_DECORATOR', value: '1'
						}
					}
					steps{
						script {
							docker.image('neo4j:latest').withRun(
									'-p 172.16.0.1:7474:7474 ' +
									'-p 172.16.0.1:7687:7687 ' +
									'--volume=${WORKSPACE}/src/test/resources/neo4j/conf:/var/lib/neo4j/conf:z ' +
									'--volume=/var/run/neo4j-jenkins-rogm:/run:z'
								) { c ->

								/* Wait until database service is up */
								sh 'echo waiting for Neo4J to start'
								sh 'until $(curl --output /dev/null --silent --head --fail http://172.16.0.1:7474); do sleep 5; done'

								docker.image('neo4j:latest').inside("--link ${c.id}:database") {
									/* Prepare Database */
										sh	'echo Neo4J online > setting up database'
										sh	'JAVA_HOME=/opt/java/openjdk cypher-shell -a "neo4j://database:7687" -u neo4j -p neo4j -f "./src/test/resources/neo4j/setup/setup.cypher"'
									}

								/* Run tests */
								sh 'echo database loaded > starting tests'
								sh 'printenv | sort'
								sh 'mvn-dev -P ${REPOS},toolchain-openjdk-1-8-0,test-junit-jupiter,test-db-neo4j -Ddbhost=172.16.0.1 -Ddbuser=neo4j -Ddbpw=neo4j'
							}
						}
					}
				}

			}
			post {
				success {
					junit '*/target/surefire-reports/*.xml'
				}
				failure {
					junit '*/target/surefire-reports/*.xml'
					archiveArtifacts artifacts: '*/target/surefire-reports/*.xml'
				}
			}
		}

		stage('Package Build Result') {
			when {
				anyOf {
					environment name: 'CHANGES_ROGM_PARENT', value: '1'
					environment name: 'CHANGES_ROGM_BOM', value: '1'
					environment name: 'CHANGES_ROGM_SOURCES_BOM', value: '1'
					environment name: 'CHANGES_ROGM_CORE', value: '1'
					environment name: 'CHANGES_ROGM_PARSER_JSON', value: '1'
					environment name: 'CHANGES_ROGM_LANG_CYPHER', value: '1'
					environment name: 'CHANGES_ROGM_MODULE_NEO4J', value: '1'
					environment name: 'CHANGES_ROGM_MODULE_DECORATOR', value: '1'
				}
			}
			steps {
				sh 'tree -I .mvn/repo'
				dir(path: 'target/result') {
					sh 'ls -l'
					sh 'tar -I "pxz -9" -cvf ../rogm.tar.xz *'
					sh 'zip -9 ../rogm.zip *'
				}
			}
			post {
				always {
					dir(path: 'target') {
						archiveArtifacts artifacts: '*.tar.xz', fingerprint: true
						archiveArtifacts artifacts: '*.zip', fingerprint: true
					}
				}
			}
		}

		stage('Deploy') {
			parallel {
				stage('Develop') {
					stages {
						stage('rogm-parent') {
							when {
								environment name: 'CHANGES_ROGM_PARENT', value: '1'
							}
							steps {
								sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy --non-recursive'
							}
						}
						stage('rogm-bom') {
							when {
								environment name: 'CHANGES_ROGM_BOM', value: '1'
							}
							steps {
								dir(path: 'rogm-bom') {
									sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy --non-recursive'
								}
							}
						}
						stage('rogm-sources-bom') {
							when {
								environment name: 'CHANGES_ROGM_SOURCES_BOM', value: '1'
							}
							steps {
								dir(path: 'rogm-sources-bom') {
									sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy --non-recursive'
								}
							}
						}
						stage('rogm-core') {
							when {
								environment name: 'CHANGES_ROGM_CORE', value: '1'
							}
							steps {
								dir(path: 'rogm-core') {
									sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy --non-recursive'
								}
							}
						}
						stage('rogm-parser-json') {
							when {
								environment name: 'CHANGES_ROGM_PARSER_JSON', value: '1'
							}
							steps {
								dir(path: 'rogm-parser-json') {
									sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy --non-recursive'
								}
							}
						}
						stage('rogm-lang-cypher') {
							when {
								environment name: 'CHANGES_ROGM_LANG_CYPHER', value: '1'
							}
							steps {
								dir(path: 'rogm-lang-cypher') {
									sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy --non-recursive'
								}
							}
						}
						stage('rogm-module-neo4j') {
							when {
								environment name: 'CHANGES_ROGM_MODULE_NEO4J', value: '1'
							}
							steps {
								dir(path: 'rogm-module-neo4j') {
									sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy --non-recursive'
								}
							}
						}
						stage('rogm-module-decorator') {
							when {
								environment name: 'CHANGES_ROGM_MODULE_DECORATOR', value: '1'
							}
							steps {
								dir(path: 'rogm-module-decorator') {
									sh 'mvn-dev -P ${REPOS},dist-repo-development,deploy --non-recursive'
								}
							}
						}
					}
				}

				stage('Release') {
					when {
						branch 'master'
					}
					stages {
						stage('rogm-parent') {
							when {
								environment name: 'CHANGES_ROGM_PARENT', value: '1'
							}
							steps {
								sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-pom-signed --non-recursive'
							}
						}
						stage('rogm-bom') {
							when {
								environment name: 'CHANGES_ROGM_BOM', value: '1'
							}
							steps {
								dir(path: 'rogm-bom') {
									sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-pom-signed --non-recursive'
								}
							}
						}
						stage('rogm-sources-bom') {
							when {
								environment name: 'CHANGES_ROGM_SOURCES_BOM', value: '1'
							}
							steps {
								dir(path: 'rogm-sources-bom') {
									sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-pom-signed --non-recursive'
								}
							}
						}
						stage('rogm-core') {
							when {
								environment name: 'CHANGES_ROGM_CORE', value: '1'
							}
							steps {
								dir(path: 'rogm-core') {
									sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
								}
							}
						}
						stage('rogm-parser-json') {
							when {
								environment name: 'CHANGES_ROGM_PARSER_JSON', value: '1'
							}
							steps {
								dir(path: 'rogm-parser-json') {
									sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
								}
							}
						}
						stage('rogm-lang-cypher') {
							when {
								environment name: 'CHANGES_ROGM_LANG_CYPHER', value: '1'
							}
							steps {
								dir(path: 'rogm-lang-cypher') {
									sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
								}
							}
						}
						stage('rogm-module-neo4j') {
							when {
								environment name: 'CHANGES_ROGM_MODULE_NEO4J', value: '1'
							}
							steps {
								dir(path: 'rogm-module-neo4j') {
									sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
								}
							}
						}
						stage('rogm-module-decorator') {
							when {
								environment name: 'CHANGES_ROGM_MODULE_DECORATOR', value: '1'
							}
							steps {
								dir(path: 'rogm-module-decorator') {
									sh 'mvn-dev -P ${REPOS},dist-repo-releases,deploy-signed --non-recursive'
								}
							}
						}
					}
				}
			}
		}

		stage('Stage at Maven-Central') {
			when {
				branch 'master'
			}
			stages {
				// never add : -P ${REPOS} => this is ment to fail here
				stage('rogm-parent') {
					when {
						environment name: 'CHANGES_ROGM_PARENT', value: '1'
					}
					steps {
						sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-pom-signed --non-recursive'
					}
				}
				stage('rogm-bom') {
					when {
						environment name: 'CHANGES_ROGM_BOM', value: '1'
					}
					steps {
						dir(path: 'rogm-bom') {
							sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-pom-signed --non-recursive'
						}
					}
				}
				stage('rogm-sources-bom') {
					when {
						environment name: 'CHANGES_ROGM_SOURCES_BOM', value: '1'
					}
					steps {
						dir(path: 'rogm-sources-bom') {
							sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-pom-signed --non-recursive'
						}
					}
				}
				stage('rogm-core') {
					when {
						environment name: 'CHANGES_ROGM_CORE', value: '1'
					}
					steps {
						dir(path: 'rogm-core') {
							sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed --non-recursive'
						}
					}
				}
				stage('rogm-parser-json') {
					when {
						environment name: 'CHANGES_ROGM_PARSER_JSON', value: '1'
					}
					steps {
						dir(path: 'rogm-parser-json') {
							sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed --non-recursive'
						}
					}
				}
				stage('rogm-lang-cypher') {
					when {
						environment name: 'CHANGES_ROGM_LANG_CYPHER', value: '1'
					}
					steps {
						dir(path: 'rogm-lang-cypher') {
							sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed --non-recursive'
						}
					}
				}
				stage('rogm-module-neo4j') {
					when {
						environment name: 'CHANGES_ROGM_MODULE_NEO4J', value: '1'
					}
					steps {
						dir(path: 'rogm-module-neo4j') {
							sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed --non-recursive'
						}
					}
				}
				stage('rogm-module-decorator') {
					when {
						environment name: 'CHANGES_ROGM_MODULE_DECORATOR', value: '1'
					}
					steps {
						dir(path: 'rogm-module-decorator') {
							sh 'mvn-dev -P repo-releases,dist-repo-maven-central,deploy-signed --non-recursive'
						}
					}
				}
			}
		}
	}
	post {
		cleanup {
			cleanWs()
		}
	}
}
