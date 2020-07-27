# File-Sharing-System-Ass1
Create a file sharing system back-end using Spring boot and Oracle.

The project was built using Spring Boot with Oracle as Database.

1. Database
    - I use the default local database. To generate the tables in order to test the API, run the employee_file_ass1.sql script file and change the
    application.yml file config to point to your local database.
    
    - To review the Tables and theirs relations, head for the AS1_ERD.png file.
    
2. Start the server
    - Make sure you have maven installed, otherwise research how to install and use it on the internet.
    - At the current folder, user command line: mvn clean install, wait for the maven to install.
    - To start the server, continue to command: mvn spring-boot:run, once the server is on, you will see a line like "Started Assignment1Application in 30.22 seconds (JVM running for 30.831)"
    
3. Test the API
    - All the API in this project can be tested by Postman, install it if you don't have it already installed.
    - The API collection can be listed here: https://www.getpostman.com/collections/427288b739bb49c6f057
    
    I. Employee APIs
    
    a. Create Employee
     - URL: localhost:8080/employee-file-sharing/employee 
     - Method: POST
     - body: {
             	"username": "newUser",
             	"password": "P@ssW0rd"
             }
     - Description: Create a new User
     
    b. Get User Token
     - URL: localhost:8080/employee-file-sharing/employee/token 
     - Method: POST
     - body: {
             	"username": "newUser",
             	"password": "P@ssW0rd"
             }
     - Description: Get the login TOKEN of an existing user, the TOKEN will be used as login information for the rest of the APIs collection.
     
    c. Delete Employee
     - URL: localhost:8080/employee-file-sharing/employee/:employeeID 
     - Method: DELETE
     - params: employeeID (The ID of the employee, get this from get User Token API)
     - header: token (The login TOKEN, get this from get User Token API)
     - Description: Soft delete an user           
    
    II. File APIs (All the APIs must contain token in header)
    
    a. Upload files
     - URL: localhost:8080/employee-file-sharing/employee/:employeeID/file 
     - Method: POST
     - params: employeeID (The ID of the employee, get this from get User Token API)
     - body: files (set the type of the body in Postman as form-data, the "files" can be multiple)
     - Description: Upload files to the folder of an employee
     
     b. Get all File of Employee (Listing)
      - URL: localhost:8080/employee-file-sharing/employee/:employeeID/file 
      - Method: GET
      - params: employeeID (The ID of the employee, get this from get User Token API)
      - Description: Listing all upload file of an employee
      
      c. Delete multiple files
      - URL: localhost:8080/employee-file-sharing/employee/:employeeID/file 
      - Method: DELETE
      - params: employeeID (The ID of the employee, get this from get User Token API)
      - body: Set of files's ID (Get from the Listing API, e.g: [1, 2])
      - Description: Delete multiple files of an employee.
      
      d. Download single file
      - URL: localhost:8080/employee-file-sharing/employee/:employeeID/file/download?fileID= 
      - Method: GET
      - params: employeeID (The ID of the employee, get this from get User Token API), fileID (ID of the downloading file)
      - Description: Download single file of an employee
      
      e. Download and zip multiple files
    - URL: localhost:8080/employee-file-sharing/employee/:employeeID/file/zip-and-download 
    - Method: GET
    - params: employeeID (The ID of the employee, get this from get User Token API)
    - body: Set of files's ID (Get from the Listing API, e.g: [1, 2])
    - Description: Download single file of an employee
    
    III. Sharing APIs (All the APIs must contain token in header)
    
    a. Get all sharing file of an employee
     - URL: localhost:8080/employee-file-sharing/employee/:employeeID/share-file/my-sharing 
     - Method: GET
     - params: employeeID (The ID of the employee, get this from get User Token API)
     - Description: Get all the files that current employee is sharing
     
     b. Get all files sharing to an employee
      - URL: localhost:8080/employee-file-sharing/employee/:employeeID/share-file/sharing-to-me 
      - Method: GET
      - params: employeeID (The ID of the employee, get this from get User Token API)
      - Description: Get all the files that currently shared to current employee
      
     c. Share file
      - URL: localhost:8080/employee-file-sharing/employee/:employeeID/share-file 
      - Method: POST
      - params: employeeID (The ID of the employee, get this from get User Token API)
      - body: {
                  "fileID" : 1 (ID of the sharing file)
                  "sharedEmployeeIDs" : [1, 2] (List of employee ID sharing)
              }
      - Description: Share a file of the current employee to others
      
      d. Stop sharing file
      - URL: localhost:8080/employee-file-sharing/employee/:employeeID/share-file 
      - Method: DELETE
      - params: employeeID (The ID of the employee, get this from get User Token API)
      - body: {
                    "fileID" : 1 (ID of the sharing file)
                    "sharedEmployeeIDs" : [1, 2] (List of employee ID sharing)
                }
      - Description: Stop sharing a file
