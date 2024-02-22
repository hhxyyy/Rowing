# Rowing application - Group 31B

## 1. Using the databases
- connecting to the databases for any microservice can be done with the credentials which are stored in `/src/main/java/resources/application.properties`
- for viewing the database in IntelliJ, use the database feature visible in the right side-bar, and add any database using the credentials from `src/main/java/resources/application.properties`

## 2. Running the application

### User register and authentication:
  - send a **POST** request to the following URLs: `http://localhost:8081/register` and `http://localhost:8081/authenticate`
  - format the JSON in the same way as the `user-register-auth.json` file

### Get user information:
  - send a **GET** request to the URL `http://localhost:8083/getPersonalInfo`

### Set user information:
  - send a **POST** request to the URL `http://localhost:8083/setPersonalInfo`
  - format the JSON in the same way as the `user-setInfo.json` file

### Create event:
  - send a **POST** request to the URL `http://localhost:8083/createEvent`
  - format the JSON in the same way as the `event-adding.json` file

### Edit event:
  - send a **PATCH** request to the URL `http://localhost:8083/editEvent/{eventId}`
    - where `{eventId}` is replaced with the ID of the event
  - format the JSON in the same way as the `event-editting.json` file

### Delete event:
  - send a **DELETE** request to the URL`http://localhost:8083/cancelEvent/{eventId}`
    - where `{eventId}` is replaced with the ID of the event

### Match request: 
  - add the authentication token received and send a **POST** request to the following path: `http://localhost:8083/request`
  - format the JSON in the same way as the `match-request.json` file
  
### Register user choice:
  - send a **POST** request to the URL `http://localhost:8083/pickEvent`
  - copy one of the events received from the match request and send it as a JSON

### Get all notifications for an user:
  - send a **GET** request to the URL `http://localhost:8083/getNotifications`

### Send decision as an owner:
  - send a **POST** request to the URL `http://localhost:8083/approveApplicant`
  - format the JSON in the same way as the `owner-approval.json` file

### Add a new certificate:
  - _this is only available for admin accounts_
  - send a **POST** request to the URL `http://localhost:8084/addCertificate`
  - format the JSON in the same way as the `add-certificate.json` file

<br>

#### NOTE: for requests with a body, templates are available in the `json-templates` folder

## 3. Documents
  - our solution for assignment 1 can be found in `/docs/Assignment 1`
  - the sprint retrospectives can be found in `/docs/Sprint Retrospectives`
