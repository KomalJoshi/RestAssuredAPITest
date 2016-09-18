package com.EEAutomationExercise.testAutomation;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;



public class EEHotelBookingTest{
public Response createBooking(String myJason)
{
 RestAssured.baseURI  = "http://hotel-test.equalexperts.io/booking";
    // we want all the details for failed tests
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        System.out.println("The Base Url is: " + RestAssured.baseURI );
        System.out.println("The Jason String is: " + myJason );
   
        Response r = RestAssured.
        given().
           contentType("application/json; charset=UTF-8").
            body(myJason).
        when().
            post("");
    String body = r.getBody().asString();
    System.out.println("Status code is "+ r.statusCode());
    System.out.println("The response body is :" + body);
        return r;
}

public Response deleteBooking(String myJason)
{
 
    RestAssured.baseURI  = "http://hotel-test.equalexperts.io/booking";
    // we want all the details for failed tests
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        System.out.println("The Base Url is: " + RestAssured.baseURI );
        System.out.println("The Jason String is: " + myJason );
   
        // Creating a booking so that we can delete the same booking to make the test fail safe
        Response r = createBooking(myJason);
   
        // printing data for logging
    String body = r.getBody().asString();
    System.out.println("Status code is "+ r.statusCode());
    System.out.println("The response body is :" + body);
   
    // we need to get the Json path to extract booking id.
    JsonPath jsonPath = new JsonPath(body);
    int bookingid = jsonPath.getInt("bookingid");
    System.out.println("booking id is: "+ bookingid);
    System.out.println("Now we will delete the above entry");
   
    // We need to set the authorisation before deletion
    // We could check the username = admin and password= password123 was plain text in fiddler
    Response response = RestAssured.
               given().
                 authentication().
                 preemptive().
                 basic("admin", "password123"). 
               when().
                 delete("/"+bookingid);
   
    String body1 = response.getBody().asString();	          
    System.out.println("The deletion response is: "+ response.statusCode());
    System.out.println("The response body is :" + body1);
        return response;
}

@Test
public void statusCodeIsReturnedForbookingCreation()
{
String myJason = "{\"firstname\":\"12324342432545\",\"lastname\":\"Joshi\",\"totalprice\":\"5001\",\"depositpaid\":\"true\",\"bookingdates\":{\"checkin\":\"2016-09-18\",\"checkout\":\"2016-09-29\"}}";
    System.out.println("==========statusCodeIsReturnedForbookingCreation()=============");
Response r = createBooking(myJason);
    Assert.assertTrue(r.statusCode()==200);
}
@Test
public void errorCodeIsReturnedForEmptyTextValue()
{
String myJason = "{\"firstname\":\"\",\"lastname\":\"Joshi\",\"totalprice\":\"5001\",\"depositpaid\":\"true\",\"bookingdates\":{\"checkin\":\"2016-09-18\",\"checkout\":\"2016-09-29\"}}";
System.out.println("==========errorCodeIsReturnedForEmptyTextValue()=============");
Response r = createBooking(myJason);
    Assert.assertFalse("The string should not be created and status code is "+r.statusCode(), r.statusCode()==200 );
}
// This test should fail as the satus code expected should be 200 for successful deletion 
@Test
public void checkDataIsDeletedByStatusCode(){
String myJason = "{\"firstname\":\"Deletion\",\"lastname\":\"Joshi\",\"totalprice\":\"5001\",\"depositpaid\":\"true\",\"bookingdates\":{\"checkin\":\"2016-09-18\",\"checkout\":\"2016-09-29\"}}";
System.out.println("==========checkDataIsDeletedByStatusCode()=============");
Response r = this.deleteBooking(myJason);
    Assert.assertTrue("The Status code expected is 200 but is "+r.statusCode(), r.statusCode()==200);
    
}
//This test should fail as the response body should contain either OK or Deleted
@Test
public void checkResponseBodyOfDeletedDataIsOK(){
String myJason = "{\"firstname\":\"Deletion\",\"lastname\":\"Joshi\",\"totalprice\":\"5001\",\"depositpaid\":\"true\",\"bookingdates\":{\"checkin\":\"2016-09-18\",\"checkout\":\"2016-09-29\"}}";
System.out.println("==========checkResponseBodyOfDeletedDataIsOK()=============");
Response r = this.deleteBooking(myJason);
    String body = r.getBody().asString();
    Assert.assertTrue("The response expected is OK or Deleted but is "+ body, body.equalsIgnoreCase("OK") || body.equalsIgnoreCase("Deleted") );
    
}
}
