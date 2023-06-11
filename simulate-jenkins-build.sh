#!/usr/bin/bash
#
# Copyright © 2023 VenaNocta (venanocta@gmail.com)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


WORKSPACE=$(pwd)
GLOBAL_MAVEN_SETTINGS=~/.m2/settings.xml
MAVEN_SETTINGS=$WORKSPACE/.mvn/settings.xml
MAVEN_TOOLCHAINS=$WORKSPACE/.mvn/toolchains.xml
REPOS=repo-releases,repo-development
TOOLCHAIN=toolchain-openjdk-1-8-0
INSTALL=install
CLEAR=false
INFO=false

print_help() {
  printf ">>> JENKINS BUILD SIMULATOR\n\n";
  printf "  -w | --workspace       ... set workspace\n"
  printf "                               [default] current working directory\n"
  printf "  -g | --global-settings ... set global path to maven settings.xml\n"
  printf "                               [default] path: ~/.m2/settings.xml\n"
  printf "  -s | --settings        ... set path to maven settings.xml\n"
  printf "                               [default] path: .mvn/settings.xml\n"
  printf "  -T | --toolchains      ... set path to toolchains.xml\n"
  printf "                               [default] path: .mvn/toolchains.xml\n"
  printf "  -r | --repos           ... set active repo-profiles\n"
  printf "                               [default] repo-releases,repo-development\n"
  printf "  -t | --toolchain       ... set active toolchain-profile\n"
  printf "                               [default] toolchain-openjdk-1-8-0\n"
  printf "  -i | --install         ... set active install-profile\n"
  printf "                               [default] install\n\n"
  printf "  -c | --clear-repo      ... clear maven repo\n"
  printf "  -I | --info            ... show info\n"
  printf "  ____________________________________________________________________________\n"
  printf "  -h | --help            ... show this help page\n\n";
}

print_info() {
  printf ">>>  [INFO]\n"
  printf "==  Environment\n"
  printf ">>    WORKSPACE             = "$WORKSPACE"\n"
  printf ">>    GLOBAL_MAVEN_SETTINGS = "$GLOBAL_MAVEN_SETTINGS"\n"
  printf ">>    MAVEN_SETTINGS        = "$MAVEN_SETTINGS"\n"
  printf ">>    MAVEN_TOOLCHAINS      = "$MAVEN_TOOLCHAINS"\n"
  printf ">>    REPOS                 = "$REPOS"\n"
  printf ">>    TOOLCHAIN             = "$TOOLCHAIN"\n"
  printf ">>    INSTALL               = "$INSTALL"\n"
  printf ">>    CLEAR                 = "$CLEAR"\n"
  printf "  ____________________________________________________________________________\n"
  printf "==  Tools\n"
  printf ">>    maven build tool      = "$WORKSPACE/.build/mvn-dev"\n"
  printf "  ____________________________________________________________________________\n\n"
}

while [ True ]; do
if [ "$1" = "--help" -o "$1" = "-h" ]; then
  print_help
  exit 0
elif [ "$1" = "--workspace" -o "$1" = "-w" ]; then
  WORKSPACE=$2
  shift 2
elif [ "$1" = "--global-settings" -o "$1" = "-g" ]; then
  GLOBAL_MAVEN_SETTINGS=$2
  shift 2
elif [ "$1" = "--settings" -o "$1" = "-s" ]; then
  MAVEN_SETTINGS=$2
  shift 2
elif [ "$1" = "--toolchains" -o "$1" = "-T" ]; then
  MAVEN_TOOLCHAINS=$2
  shift 2
elif [ "$1" = "--repos" -o "$1" = "-r" ]; then
  REPOS=$2
  shift 2
elif [ "$1" = "--toolchain" -o "$1" = "-t" ]; then
  TOOLCHAIN=$2
  shift 2
elif [ "$1" = "--install" -o "$1" = "-i" ]; then
  INSTALL=$2
  shift 2
elif [ "$1" = "--clear-repo" -o "$1" = "-c" ]; then
  CLEAR=true
  shift 1
elif [ "$1" = "--info" -o "$1" = "-I" ]; then
  INFO=true
  shift 1
else
  break
fi
done

# start executing
printf ">>> JENKINS BUILD SIMULATOR\n\n";

if $INFO; then
  print_info
fi

# clear
if $CLEAR; then
  printf "=== CLEAR MAVEN REPO\n"
  WORKSPACE=$WORKSPACE GLOBAL_MAVEN_SETTINGS=$GLOBAL_MAVEN_SETTINGS MAVEN_SETTINGS=$MAVEN_SETTINGS MAVEN_TOOLCHAINS=$MAVEN_TOOLCHAINS $WORKSPACE/.build/mvn-dev dependency:purge-local-repository -DactTransitively=false -DreResolve=false -P ${REPOS}
fi

# start building
printf ">>>  [BUILD]\n"

cd $WORKSPACE
# parent
printf "=== BUILD : ROGM PARENT\n"
WORKSPACE=$WORKSPACE GLOBAL_MAVEN_SETTINGS=$GLOBAL_MAVEN_SETTINGS MAVEN_SETTINGS=$MAVEN_SETTINGS MAVEN_TOOLCHAINS=$MAVEN_TOOLCHAINS $WORKSPACE/.build/mvn-dev -P ${REPOS} dependency:resolve --non-recursive
WORKSPACE=$WORKSPACE GLOBAL_MAVEN_SETTINGS=$GLOBAL_MAVEN_SETTINGS MAVEN_SETTINGS=$MAVEN_SETTINGS MAVEN_TOOLCHAINS=$MAVEN_TOOLCHAINS $WORKSPACE/.build/mvn-dev -P ${REPOS},${TOOLCHAIN},${INSTALL} --non-recursive

cd $WORKSPACE/rogm-sources-bom
printf "=== BUILD : ROGM Bill of Sources\n"
WORKSPACE=$WORKSPACE GLOBAL_MAVEN_SETTINGS=$GLOBAL_MAVEN_SETTINGS MAVEN_SETTINGS=$MAVEN_SETTINGS MAVEN_TOOLCHAINS=$MAVEN_TOOLCHAINS $WORKSPACE/.build/mvn-dev -P ${REPOS} dependency:resolve  --non-recursive
WORKSPACE=$WORKSPACE GLOBAL_MAVEN_SETTINGS=$GLOBAL_MAVEN_SETTINGS MAVEN_SETTINGS=$MAVEN_SETTINGS MAVEN_TOOLCHAINS=$MAVEN_TOOLCHAINS $WORKSPACE/.build/mvn-dev -P ${REPOS},${TOOLCHAIN},${INSTALL} --non-recursive

cd $WORKSPACE/rogm-bom
printf "=== BUILD : ROGM Bill of Materials\n"
WORKSPACE=$WORKSPACE GLOBAL_MAVEN_SETTINGS=$GLOBAL_MAVEN_SETTINGS MAVEN_SETTINGS=$MAVEN_SETTINGS MAVEN_TOOLCHAINS=$MAVEN_TOOLCHAINS $WORKSPACE/.build/mvn-dev -P ${REPOS},${TOOLCHAIN},${INSTALL} --non-recursive

cd $WORKSPACE/rogm-core
printf "=== BUILD : ROGM CORE\n"
WORKSPACE=$WORKSPACE GLOBAL_MAVEN_SETTINGS=$GLOBAL_MAVEN_SETTINGS MAVEN_SETTINGS=$MAVEN_SETTINGS MAVEN_TOOLCHAINS=$MAVEN_TOOLCHAINS $WORKSPACE/.build/mvn-dev -P ${REPOS},${TOOLCHAIN},${INSTALL} --non-recursive

cd $WORKSPACE/rogm-parser-json
printf "=== BUILD : ROGM Parser JSON\n"
WORKSPACE=$WORKSPACE GLOBAL_MAVEN_SETTINGS=$GLOBAL_MAVEN_SETTINGS MAVEN_SETTINGS=$MAVEN_SETTINGS MAVEN_TOOLCHAINS=$MAVEN_TOOLCHAINS $WORKSPACE/.build/mvn-dev -P ${REPOS},${TOOLCHAIN},${INSTALL} --non-recursive

cd $WORKSPACE/rogm-lang-cypher
printf "=== BUILD : ROGM Language Cypher\n"
WORKSPACE=$WORKSPACE GLOBAL_MAVEN_SETTINGS=$GLOBAL_MAVEN_SETTINGS MAVEN_SETTINGS=$MAVEN_SETTINGS MAVEN_TOOLCHAINS=$MAVEN_TOOLCHAINS $WORKSPACE/.build/mvn-dev -P ${REPOS},${TOOLCHAIN},${INSTALL} --non-recursive

cd $WORKSPACE/rogm-module-neo4j
printf "=== BUILD : ROGM Module Neo4j\n"
WORKSPACE=$WORKSPACE GLOBAL_MAVEN_SETTINGS=$GLOBAL_MAVEN_SETTINGS MAVEN_SETTINGS=$MAVEN_SETTINGS MAVEN_TOOLCHAINS=$MAVEN_TOOLCHAINS $WORKSPACE/.build/mvn-dev -P ${REPOS},${TOOLCHAIN},${INSTALL} --non-recursive

