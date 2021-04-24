# ROGM by RunedUniverse
ROGM is an OGM (**O**bject oriented **G**raph **M**odel) library for use in Java projects.
We recommend using it in non-Spring projects!

## Distribution through Maven
### Repository
```xml
<repository>
  <id>runeduniverse-releases</id>
  <url>https://nexus.runeduniverse.net/repository/maven-releases/</url>
</repository>
```
### Dependencies
In projects with APIs it is recommended to only include the CORE in the API
and implement the Modules only in the actual programm!

```xml
<!-- ROGM CORE -->
<dependency>
  <groupId>net.runeduniverse.libs.rogm</groupId>
  <artifactId>core</artifactId>
  <version>2.0.13</version>
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
