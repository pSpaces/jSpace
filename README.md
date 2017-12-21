# jSpace
jSpace is a Java library that supports programming of Concurrent and Distributed applications with Spaces.

jSpace borns as a fork of jResp (https://github.com/michele-loreti/jResp).

#Getting Started with jSpace
Below you can find detailed instructions that show how you can use jSpace
to develop your applications.

## Requirements
jSpace requires [Java 1.8](https://java.com/) (or higher). Moreover, jSpace is using [Maven](https://maven.apache.org/index.html) for dependency management and building (you can also use one of the IDE that supports it).

## Building and Installing jSpace
You can download jSpace source code either by cloning the GitHub repository:

```
git clone git@github.com:pSpaces/jSpace.git
```

This creates a folder (named ```jSpace```) containing all the source code.

You can also download the zip archive of the same folder from this [link](https://github.com/pSpaces/jSpace/archive/master.zip).

Maven can now be used to build and test jSpace:

```
mvn clean verify
```

After that, Maven will download the required packages (like, e.g. Google gson), build the framework, and execute all the tests.

Finally, we have to install jSpace in the local Maven repository (the precise location of this repository depends on your configuration):

```
mvn install
```

After that jSpace will be available to be used in other Maven projects (or within any IDE supporting it).

## Your first jSpace applications
You can use Maven to generate an empty jSpace project:

```
mvn -B archetype:generate \
  -DarchetypeGroupId=org.apache.maven.archetypes \
  -DgroupId=com.mycompany.myjspaceapp \
  -DartifactId=myjspaceapp
```

The command above generates the skeleton of a Maven project in a folder named ```myjspaceapp```. In particular, inside folder ```src``` you can find the two directories ```main/java``` and ```test/java``` where basic Java files for the app and its tests, respectively.

Maven also generates the ```pom.xml``` describing how building your code and all the needed dependencies. Open this file and add the dependency to jSpace. You should find something of the form:

```
<dependencies>
  <dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>3.8.1</version>
    <scope>test</scope>
  </dependency>
</dependencies>
```

you have to add above the reference to jSpace:

```
<dependencies>
  <dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>3.8.1</version>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>io.github.pspaces.jspace</groupId>
    <artifactId>common</artifactId>
    <version>[0.0,)</version>
  </dependency>
</dependencies>
```

At this point you can change the generated Java code to implement the desired application. In this short guide we simply open:

```
myjspaceapp/src/main/java/com/mycompany/myjspaceapp/App.java
```

Add the following code:

```
package com.mycompany.myjspaceapp;

import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

public class App {

	public static void main(String[] argv) throws InterruptedException {
		Space inbox = new SequentialSpace();

		inbox.put("Hello World!");
		Object[] tuple = inbox.get(new FormalField(String.class));				
		System.out.println(tuple[0]);

	}

}
```
