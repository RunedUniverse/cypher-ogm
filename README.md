# ROGM by RunedUniverse
ROGM is an OGM (**O**bject oriented **G**raph **M**odel) library for use in Java projects.<br>
It features an extensive Buffer solution and focuses keeping a minimal memory footprint.<br>
Whilst not compromising of functionality and usability. We recommend using it in non-Spring projects!

**Interested ?**<br>
*Hop over to our [Wiki](https://github.com/RunedUniverse/rogm/wiki) or straight to the [Crash Course](https://github.com/RunedUniverse/rogm/wiki/Tutorial)*

## Distribution through Maven
### Repository
#### RunedUniverse: Releases
```xml
<repository>
  <id>runeduniverse-releases</id>
  <url>https://nexus.runeduniverse.net/repository/maven-releases/</url>
</repository>
```

### Dependencies
In projects with APIs it is recommended to only include the CORE in the API <br>
and include the Modules only in the actual core-programm.<br>
Additionally a Bill of Materials (BOM) is provided to help you choose the most stable version!

```xml
<!-- ROGM BOM -->
<dependency>
  <groupId>net.runeduniverse.lib.rogm</groupId>
  <artifactId>rogm-bom</artifactId>
  <version>2.1.1</version>
</dependency>
```
```xml
<!-- ROGM CORE -->
<dependency>
  <groupId>net.runeduniverse.lib.rogm</groupId>
  <artifactId>rogm-core</artifactId>
  <version>2.1.1</version>
</dependency>
```
```xml
<!-- Parser: JSON -->
<dependency>
  <groupId>net.runeduniverse.lib.rogm.parser</groupId>
  <artifactId>rogm-parser-json</artifactId>
  <version>2.1.1</version>
</dependency>
```
```xml
<!-- Language: Cypher -->
<dependency>
  <groupId>net.runeduniverse.lib.rogm.lang</groupId>
  <artifactId>rogm-lang-cypher</artifactId>
  <version>2.1.1</version>
</dependency>
```
```xml
<!-- Module: Neo4j -->
<dependency>
  <groupId>net.runeduniverse.lib.rogm.modules</groupId>
  <artifactId>rogm-modules-neo4j</artifactId>
  <version>2.1.1</version>
</dependency>
```
