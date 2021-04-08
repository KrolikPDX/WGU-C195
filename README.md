# WGU-C195
Course work for WGU's course C195. This application uses Java to connect to a pre-setup SQL server to store data about customers and appointments. 

## Overview
WGU-C195 is a JavaFX app that will ask for user input to add, modify, or delete customer information and appointments. The app requires a pre-setup SQL database that will be used to store data about customers, and appointments defined by the user. This project uses a SQL connector driver to enable communication between the app and the SQL server. This project incorporates:

- Java + JavaFX
- MySQL Server connection
- Add, Modify, and Delete data using SQL
- Lamda code simplification
- Username and Password login 
- Report generation
- Auto language selection based on region 

### How it works
- The project starts by setting up a connection between the user and the SQL server. 
- We check the region of the user and set the language accordingly. *Note: Only available in english or german*
- Ask user for username and password and attempt to authenticate the information with the database.
- Once the user successfully logs in, they will have the options to add, modify, or delete customers and appointments in the database server.

## Getting Started
#### Quick Note
- I will not be providing any assistance for installation and use of this project. 
- The project uses My SQL Connector, which is included in source files under `src/JDBCDriver/mysql-connector-java-8.0.21.jar`

### Prerequisites
- IntelliJ IDEA
- Downloaded JavaFX (link below)
- Java SDK
- SQL server containing tables for customers + appointments

### Installation
1. Clone or download the project to your local machine.
2. Open the project through your IDE.
3. Add JavaFX as a library to your IDE - In IntelliJ, this can be done by pressing CTRL-ALT-SHIFT-S, then click on Libraries. Press the + button, click on Java, navigate to where you saved JavaFX and selected the `lib` folder inside the JavaFX root directory. 
4. Make sure you have your SDK path set.
5. Add `--module-path /<path>/javafx-sdk-11/lib/ --add-modules=javafx.controls` to your VM options, changing the <path> to the JavaFX directory.
6. Change the values of DB_URL, USERNAME, and PASSWORD under `src/SchedulingApp/DAO/DBConnector.java` to your own values.  
7. Build and run. 

## Links
- [JavaFX] -> https://gluonhq.com/products/javafx/
- [IntelliJ IDEA] -> https://www.jetbrains.com/idea/
- [My SQL Connector] -> https://www.w3resource.com/mysql/mysql-java-connection.php
