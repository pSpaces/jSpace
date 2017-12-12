# jSpace
jSpace is a Java library that supports programming of Concurrent and Distributed applications with Spaces.

jSpace borns as a fork of jResp (https://github.com/michele-loreti/jResp).


## Getting jSpace
You can download jSpace source code by cloning the GitHub pepository:

```
git clone git@github.com:pSpaces/jSpace.git
```

##Building jSpace
jResp is using the [Maven](https://maven.apache.org/index.html) build automation tool for dependency management and building.

In order to build the project, open a terminal, and execute in the core/directory: gradle build

To build meta-data for Eclipse, open a terminal and execute in the core/ directory: gradle cleanEclipse eclipse

To build a JAR library file without dependencies, open a terminal and execute in the core/ directory: gradle jar

To build a standalone JAR file with all dependencies, open a terminal and execute in the core/ directory: gradle standaloneJar

To see more available build tasks, open a terminal and execute in the core/ directory: gradle tasks
