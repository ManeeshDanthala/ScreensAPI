# ScreensAPI
--------------------------How to Deploy-------------------------------------
1.This is a RestApi project that uses jersey framework and is developed on eclipse IDE.
2.Please configure required jars(jersey,tomcat,json) for jersey framework or use maven which will take care of all these and jar file to be added for JSON is json-simple-1.1.1.jar.
3.To deploy the project on any local system it is recommended to import it in eclipse as a MAVEN project for easy configuration after downloading and extracting the project folder.
4.Connect it with tomcat server using eclipse server configurations.
5.Run it using tomcat server which is configured prior to this step.

NOTE:Please download project as maven to make the deployment process easier.

-----------------------How to use the API-----------------------------------

(Assuming that the tomcat is installed in the port 8080)

Note:First register a theatre inorder to perform any actions on that. 

1.Register Movie theatre (screen):
	Request Method: POST
	Request URI: http://localhost:8080/uproject/webapi/screens
	Request Body(sample):
		{"name":"inox","seatInfo":{"A":{"numberOfSeats":10,"aisleSeats":[0,5,6,9] },"B":{"numberOfSeats":15,"aisleSeats":[0,5,6,9]} } }
		That is name,seatInfo,numberOfSeats,aisleSeats these parameter names and the JSON pattern must be as mentioned above to be valid.
	Response Body(sample):
		if correctly added -> "Successfully added theatre" , status code:200 OK
		if any error - > "enter valid request"    ,status code:400 Bad Request
	
	Note: I am restricting from registering a theatre multiple times to prevent duplication.

2.Reserve seats:
	Request Method: POST
	Request URI: http://localhost:8080/uproject/webapi/screens/{screenName}/reserve
	Request Body(sample):
		{"seats":{"A":[1,2],"B":[6,10] }}
		Format should be same as above.{"seats":{"row":[seat numbers]}}
	Response Body(sample):
		if perfectly booked -> "Successfully booked seats" , status code:200 OK
		if tried booking already reserved seats - > "seats already booked. Please try booking available seats"    ,status code:409 Conflict
		if non existing seat numbers are used -> "select valid seat numbers"  , status code:400 Bad Request

3.Get available seats:
	Request Method: GET
	Request URI: http://localhost:8080/uproject/webapi/screens/{screenName}/seats?status=unreserved
	Response Body(sample):
		{"seats":{"A":[0,3,4,5,6,7,8,9],"B":[0,1,2,3,4,5,7,8,9,11,12,13,14]}}
	    status code: 200 OK

4.Optimal possible seats selection:
	Request Method: GET
	Request URI: http://localhost:8080/uproject/webapi/screens/{screenName}/seats?numSeats={x}&choice={seat-row-and-number}
	Response Body(sample):
		if possible ->
			{"availableSeats":{"B":[3,4,5]}}
			status code: 200 OK
		if not possible -> "Booking with given information is not possible"  , status code:404 Not Found
