# Getting Started with jSpace
Below you can find detailed instructions that show how you can use jSpace
to develop your applications.

## Building and Installing jSpace
You can download jSpace source code either by cloning the GitHub repository:

```
git clone https://gitlab.com/klsslb/jSpace.git
```

This creates a folder (named ```jSpace```) containing all the source code.

You can also download the zip archive of the same folder from this [link](https://gitlab.com/klsslb/jSpace/-/archive/master/jSpace-master.zip).

Gradle can now be used to build and test jSpace. Open a console and enter into the folder ```jSpace``` and type:

```
cd jSpace/common
gradle clean
```

After that, Gradle will download the required packages (like, e.g. Google gson), build the framework, and execute all the tests.

Finally, we have to install jSpace in the local Gradle repository (the precise location of this repository depends on your configuration):

```
gradle build
```

After that jSpace will be available to be used in other Gradleprojects (or within any IDE supporting it).
