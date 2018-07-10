# Parking Space Management System

Simple CRUD app providing functionalities requiered for user stories.

## How to run

* use Maven clean install and bash script start.sh
* development branch is automatically deployed on Heroku cloud
https://parkinglotsystem-app.herokuapp.com/

## General requirements
* System will enable car drivers to start a parking meter, stop the parking meter and check the fee
* System automatically calculates the price and displays it when needed
* System will provide support for future addition of payments in other currencies

## Architecture
![image](https://i.imgur.com/ncQC2kq.png)

**Backend architecture description**
* Java server app based on Spring MVC framework
* HTML content served by Thymeleaf template engine
* Access to static content using URLs
* Server sends JSON messages in response to frontend’s requests
* Application data is stored in embedded H2 relational database

**Frontend architecture description**
* Connected with backend via JSON messages
* Basic data is served as static HTML
* Events handled asynchronously using to connect with the backend

## Technologies
**Backend technologies:**
* Java
* Spring Boot
* Spring MVC
* Thymeleaf
* H2 database

**Frontend technologies:**
* HTML/CSS/Javascript
* Bootstrap
* jQuery

## User interaction
System can be accessed using simple web interface. 

**Main page**

![image](https://i.imgur.com/O6tWy3z.png)

**After succesfull registration**

![image](https://i.imgur.com/1lgVygi.png)

**Ticket**

![image](https://i.imgur.com/KE0Is1J.png)

**After succesfull registration of departure**

![image](https://i.imgur.com/kYlDyq4.png)


## User stories

### Story #1  
As a driver, I want to start the parking meter, so I don’t have to pay the fine for the invalid parking.

* driver enters his car's registration plate and clicks register button
* system responds with registration status 

**Acceptance criteria:**
* driver can enter registration plate
* data is sent to server
* server responds with status info

**Definition of done:**
* Passes unit tests

### Story #2 
As a driver, I want to stop the parking meter, so that I pay only for the actual parking time

* driver enters his cars registration plate
* driver clicks checkout button
* system shows payment info
* driver clicks pay button

**Acceptance criteria:**
* driver can enter registration plate
* data is sent to server
* server responds with fee and status info

**Definition of done:**
* Passes unit tests

### Story #3 
As a driver, I want to know how much I have to pay for parking.

* driver enters his cars registration plate
* driver clicks checkout button
* system shows payment info

**Acceptance criteria:**
* driver can enter registration plate
* data is sent to server
* server responds with fee info

**Definition of done:**
* Passes unit tests

## Additional requirements

### Support for other currencies in the future

* in current version client sends always "PLN" as code of requested currency. 
* tariff info is stored using "PLN" currency,
* fee can be converted to any supported currency before being sent to client. 
* adding support for many currencies would require adding possibility to choose currency in user interface and currency record in database

### VIP clients having own tariff

* VIP client's registration plates are stored in database table
* when VIP registration plate is entered, the VIP tariff is chosen

### Testing

* 100% code coverage  
* unit test of methods
* integration tests of GET/POST requests
