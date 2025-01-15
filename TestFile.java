package com.example.api.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class BookingApiTests {

    private static final String BASE_URL = "https://restful-booker.herokuapp.com";
    private static int bookingId;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test(priority = 1, description = "Add a new booking and validate the response")
    public void testAddBooking() {
        // Prepare the request body
        Map<String, Object> bookingDates = new HashMap<>();
        bookingDates.put("checkin", "2022-01-01");
        bookingDates.put("checkout", "2024-01-01");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("firstname", "testFirstName");
        requestBody.put("lastname", "lastName");
        requestBody.put("totalprice", 10.11);
        requestBody.put("depositpaid", true);
        requestBody.put("bookingdates", bookingDates);
        requestBody.put("additionalneeds", "testAdd");

        // Send POST request to create a booking
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/booking");

        // Assert response status code and retrieve booking ID
        Assert.assertEquals(response.statusCode(), 200, "Status code should be 200");
        bookingId = response.jsonPath().getInt("bookingid");
        Assert.assertTrue(bookingId > 0, "Booking ID should be greater than 0");

        // Validate response content
        Map<String, Object> bookingDetails = response.jsonPath().getMap("booking");
        Assert.assertEquals(bookingDetails.get("firstname"), "testFirstName");
        Assert.assertEquals(bookingDetails.get("lastname"), "lastName");
    }

    @Test(priority = 2, description = "Retrieve the booking by ID and validate the details")
    public void testGetBookingById() {
        // Send GET request to retrieve the booking
        Response response = RestAssured
                .given()
                .when()
                .get("/booking/" + bookingId);

        // Assert response status code
        Assert.assertEquals(response.statusCode(), 200, "Status code should be 200");

        // Validate response content
        Assert.assertEquals(response.jsonPath().getString("firstname"), "testFirstName");
        Assert.assertEquals(response.jsonPath().getString("lastname"), "lastName");
        Assert.assertEquals(response.jsonPath().getDouble("totalprice"), 10.11);
        Assert.assertEquals(response.jsonPath().getBoolean("depositpaid"), true);
        Assert.assertEquals(response.jsonPath().getString("additionalneeds"), "testAdd");
    }

    @Test(priority = 3, description = "Negative test case for retrieving a non-existing booking")
    public void testGetNonExistingBooking() {
        // Use an invalid booking ID
        int invalidBookingId = 7687678;

        // Send GET request to retrieve a non-existing booking
        Response response = RestAssured
                .given()
                .when()
                .get("/booking/" + invalidBookingId);

        // Assert response status code
        Assert.assertEquals(response.statusCode(), 404, "Status code should be 404 for non-existing booking");
    }
}
