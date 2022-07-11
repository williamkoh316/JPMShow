# JPMShow

JPMshow is a spring boot application using Java 8, it is using H2 database which is an embedded, open-source, and in-memory database.

Because this is an in-memory application, therefore there is no persistence storage for this application.
If presistence storage is required, add "spring.datasource.url=jdbc:h2:file:<directory to save data>" to the application.properties file.

JPMShow cater to 2 types of user roles & their requirements - Admin and Buyer

Junit tests are provided in the project for the test-driven approach to understanding the implementation of the application.


-----------------------------------------------------------------------------------------------------------------------------------------
In order to access the respective user's commands, users have to login into their user role.

Commands implemented for Login:

1. Login: to login to the JPMShow
login <user role> 
CLI Input Example: JPMShow (logout)>login admin
CLI Output: Login as admin

2. Logout: to logout from the JPMShow
logout
CLI Input Example: JPMShow (logout)>logout 
CLI Output: GoodBye, see you again! 

Conditions: 
Only 2 user roles are implemented for the login - admin and buyer.
After login in to a user role, if the user needs to switch to another user role, the user needs to logout first before relogin to another user role.
After login, the user role will be indicated in the PromptProvider (Example: JPMShow (admin)>).
logout command will be available after the user login to a user role.


-----------------------------------------------------------------------------------------------------------------------------------------
User Role: Admin – The users should be able to setup and view the list of shows and seat allocations.

Commands implemented for Admin :
1. Setup: to create/update show (Default Cancellation Window = 2min)
setup  <Show Number> <Number of Rows> <Number of seats per row>  <(Optional)Cancellation window in minutes>  
CLI Input Example: JPMShow (admin)>setup 123 8 9
CLI Output: Create show: 123

2. View: to view seat allocation
view <Show Number>
CLI Input Example: JPMShow (admin)>view 123 
CLI Output: Ticket: sold for Show number:123
Ticket number: 1 Phone number: 88888888 Seat number: A1    

Conditions: 
Only admin user role can access admin commands.
The limitation for show setup is that the max seats per row are 10 and the max rows are 26.
The default Cancellation Window for the booked ticket is 2min if the parameter (Cancellation window) is not provided during the setup command.


-----------------------------------------------------------------------------------------------------------------------------------------
User Role: Buyer – The users should be able to retrieve a list of available seats for a show, select 1 or more seats, buy and cancel tickets. 
Commands implemented for Buyer :

1. Availability: to display the available seat for the Show
availability  <Show Number>   
CLI Input Example: JPMShow (buyer)>availability 123 
CLI Output: [A1, A2, A3, ...]

2. Book: To book seat(s)
book  <Show Number> <Phone#> <Comma separated list of seats> 
CLI Input Example: JPMShow (buyer)>book 123 88888888 A1 
CLI Output: Show number: 123 Ticket number: 1 Seat number: A1

3. Cancel: To cancel ticket
cancel  <Ticket#>  <Phone#>
CLI Input Example: JPMShow (buyer)>cancel 1 88888888 
CLI Output: Cancelled Ticket Number: 1

Conditions: 
Only buyer user role can access to buyer commands.
User need to provide 8 digits phone number for booking.
After booking, User can cancel the seats within a time window of 2 minutes (configurable). Cancellation after that is not allowed.
Only one booking per phone# is allowed per show.


-----------------------------------------------------------------------------------------------------------------------------------------
How to run JPMshow application:
1. mvn clean and install the project
2. Open the command prompt
3. cd to the < application jar file folder e.g. inside the target folder >
4. run "java -jar jpmshow-0.0.1-SNAPSHOT.jar" 

How to access to H2 database console:
1. open the internet browser
2. Go to http://localhost:8080/h2-console
3. Enter the credential: username: sa, password: password
