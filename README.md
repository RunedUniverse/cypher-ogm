# ROGM by RunedUniverse
ROGM is an OGM (**O**bject oriented **G**raph **M**odel) library for use in Java projects.<br>
It features an extensive Buffer solution and focuses keeping a minimal memory footprint.<br>
Whilst not compromising of functionality and usability. We recommend using it in non-Spring projects!

**Interested ?**<br>
*Hop over to our [Wiki](https://github.com/RunedUniverse/rogm/wiki) or straight to the [Crash Course](https://github.com/RunedUniverse/rogm/wiki/Tutorial)*

## Distribution through Maven
### Repository
#### RunedUniverse: Releases
> This contains our locally hosted release artifacts.<br>
> These same artifacts are deployed to Maven Central (Sonatype)

```xml
<repository>
  <id>runeduniverse-releases</id>
  <url>https://nexus.runeduniverse.net/repository/maven-releases/</url>
</repository>
```

### Dependencies
In projects with APIs it is recommended to only include the CORE in the API <br>
and include the Modules only in the actual core-programm.<br>
Additionally a Bill of Materials (BOM) is provided to help you choose the most up-to-date version!

#### ROGM BOM

[![Maven Central](https://img.shields.io/maven-central/v/net.runeduniverse.lib.rogm/rogm-bom.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.runeduniverse.lib.rogm%22%20AND%20a:%22rogm-bom%22)

```xml
<!-- ROGM BOM -->
<dependency>
  <groupId>net.runeduniverse.lib.rogm</groupId>
  <artifactId>rogm-bom</artifactId>
  <version>2.1.2</version>
</dependency>
```

#### ROGM CORE

[![Maven Central](https://img.shields.io/maven-central/v/net.runeduniverse.lib.rogm/rogm-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.runeduniverse.lib.rogm%22%20AND%20a:%22rogm-core%22)

```xml
<!-- ROGM CORE -->
<dependency>
  <groupId>net.runeduniverse.lib.rogm</groupId>
  <artifactId>rogm-core</artifactId>
  <version>2.1.2</version>
</dependency>
```

#### Parser: JSON

[![Maven Central](https://img.shields.io/maven-central/v/net.runeduniverse.lib.rogm.parser/rogm-parser-json.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.runeduniverse.lib.rogm.parser%22%20AND%20a:%22rogm-parser-json%22)

```xml
<!-- Parser: JSON -->
<dependency>
  <groupId>net.runeduniverse.lib.rogm.parser</groupId>
  <artifactId>rogm-parser-json</artifactId>
  <version>2.1.2</version>
</dependency>
```

#### Language: Cypher

[![Maven Central](https://img.shields.io/maven-central/v/net.runeduniverse.lib.rogm.lang/rogm-lang-cypher.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.runeduniverse.lib.rogm.lang%22%20AND%20a:%22rogm-lang-cypher%22)

```xml
<!-- Language: Cypher -->
<dependency>
  <groupId>net.runeduniverse.lib.rogm.lang</groupId>
  <artifactId>rogm-lang-cypher</artifactId>
  <version>2.1.2</version>
</dependency>
```

#### Module: Neo4j

[![Maven Central](https://img.shields.io/maven-central/v/net.runeduniverse.lib.rogm.modules/rogm-modules-neo4j.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.runeduniverse.lib.rogm.modules%22%20AND%20a:%22rogm-modules-neo4j%22)

```xml
<!-- Module: Neo4j -->
<dependency>
  <groupId>net.runeduniverse.lib.rogm.modules</groupId>
  <artifactId>rogm-modules-neo4j</artifactId>
  <version>2.1.2</version>
</dependency>
```
