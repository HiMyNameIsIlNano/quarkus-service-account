# Account Service Project Study Notes

## Starting the application

Once started the application, a webpage with some info about this project can be found under 

```shell
http://localhost:8080
```

This is due to the existence of an `index.html` under the `META-INF/resources` folder. This site looks
pretty interesting in particular the `Dev UI`.

## Defining the first REST Api

In `Quarkus` the default CDI container is called ARC. When creating a controller annotated with `@Path("/api")` the
default scope is `@Singleton`. This can also be seen under `http://localhost:8080/q/dev/io.quarkus.quarkus-arc/beans`. 
In general the info provided under the following [link](http://localhost:8080/q/dev/) can be used in case the `index.html`
page has been removed from the project.

## Quarkus Builds Mode

There are two ways for bundling an application in Quarkus:

- Native Executable
- JVM Executable

## Native Executable

`RSS` (`Resident Set Size`) is the amount of memory occupied by a Java program. In this case the image is translated into a set
of API the hosting Operating System is able to run without the need of the JVM. The filesize of the executable is extremely reduced
in comparison to the JVM executable. A native image starts in less time than a JVM executable. This kind of executables is extremely
indicated for serverless deployment where the cold startup takes an impact on the costs for hosting.

The trick in this kind of executable is that GraalVM performs a lot of dead code elimination and preboot loading so that the image is 
very lean and most of the code to boot the application has already been performed.  

Q: Ahead-of-time vs Just-in-time compilation

## JVM Executable

The application is run inside a JVM. It is necessary to have a JVM installed on the target system 

## Reminders

- `surefire` plugin runs the Unit Tests (e.g. `@QuarkusTest`)
- `failsafe` plugin runs the IT (e.g. `@QuarkusIntegrationTest`)