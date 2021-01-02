# ROGM by RunedUniverse
ROGM is an OGM (**O**bject oriented **G**raph **M**odel for Graph databases) library for use in Java projects.
We recommend using it in non-Spring projects!

## Distribution through Maven
### Repository
```xml
<repository>
  <id>runeduniverse-releases</id>
  <url>http://runeduniverse.net:8081/repository/maven-releases/</url>
</repository>
```
### Dependencies
In projects with APIs implementing the CORE in the API
and adding the Modules only th the actual programm is recommended!
Otherwise adding the Modules is sufficient!

```xml
<!-- ROGM CORE -->
<dependency>
  <groupId>net.runeduniverse.libs.rogm</groupId>
  <artifactId>core</artifactId>
  <version>2.0.11</version>
</dependency>

<!-- Parser: JSON -->
<dependency>
  <groupId>net.runeduniverse.libs.rogm.parser</groupId>
  <artifactId>json</artifactId>
  <version>2.0.12</version>
</dependency>

<!-- Language: Cypher -->
<dependency>
  <groupId>net.runeduniverse.libs.rogm.lang</groupId>
  <artifactId>cypher</artifactId>
  <version>2.0.11</version>
</dependency>

<!-- Module: Neo4j -->
<dependency>
  <groupId>net.runeduniverse.libs.rogm.modules</groupId>
  <artifactId>neo4j</artifactId>
  <version>2.0.12</version>
</dependency>
```
