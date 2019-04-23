# Building and Running the Application

## Prerequisites:

* Java 8 or 9 is installed on your computer,
* JAVA_HOME environment variable points to this installation,
* Maven 3.2+ is installed on your computer.

## Building the project

1. Unpack the project into a folder on your computer.
2. Run the command prompt and step into the project folder.
3. Enter the command:
```
   mvn clean package
```

## Running the project

1. Run the command prompt and step into the project folder.
2. Enter the command:
```
   java -jar rest/target/lending-rest-0.1.0.jar
```
3. Use "Advanced REST client" to test the following endpoints:

   [POST   http://localhost:8080/lending/add-order?id=1&q=5](http://localhost:8080/lending/add-order?id=1&q=5)  
   [GET    http://localhost:8080/lending/my-position?id=1  ](http://localhost:8080/lending/my-position?id=1  )  
   [GET    http://localhost:8080/lending/queue             ](http://localhost:8080/lending/queue             )  
   [GET    http://localhost:8080/lending/next-delivery     ](http://localhost:8080/lending/next-delivery     )  
   [DELETE http://localhost:8080/lending/cancel-order?id=1 ](http://localhost:8080/lending/cancel-order?id=1 )

## Alternatively, build and run in IntelliJ IDEA:

1. Create Application Run/Debug configuration:  
   Main class: com.yellowduck.lending.rest.Application  
   Working directory: [path]\YellowDuck  
   Use classpath or module: rest  
2. Run or debug the project with the configuration.
