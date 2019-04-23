# Yellow Duck

This is the test assignment accomplishment for creation of the REST-ful Web service. The assignment itself can be found in the RubberDuck_assignment.md. Advices for building and running the application are in the building_running.md.

## Architecture

The application consists of three components, or layers: **rest**, **logic** and **data** and is built with Spring Boot. The core component is **logic**, two other components are plug-ins for it.

This means, particularly, that **rest** component can be easily replaced with, for example, MVC Web component without any changes in the **logic** component. The **data** component is a simple in-memory implementation of the `LendingDataSource` interface residing in the **logic** component. The **data** component references **logic** component, which means that it can be replaced with any other implementation without any changes in the **logic**. It can be replaced with e.g. a data layer, making a connection to a relational database.

The **rest** component is so called "main" partition of the application. It contains `@SpringBootApplication` class with the main() method. That is why its \*.pom file contains the reference to not only **logic** component to which it has code references, but also to **data** layer to allow the Spring component scan and and the application packaging. This component packages the whole application combining also the **logic** and **data** components into one artifact.

## Packaging and Running

As a Spring Boot application, this one is packaged in a self-executing **jar** which can be run directly from the command line. Alternatively, the packaging can be changed to **war** to be able to deploy the application into a running Web server like Tomcat.