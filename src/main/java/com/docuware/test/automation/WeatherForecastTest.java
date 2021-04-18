package com.docuware.test.automation;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.joda.time.DateTime;
import org.json.JSONObject;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class WeatherForecastTest {

	String baseURI = "https://webapplication120210406190511.azurewebsites.net/WeatherForecast";
	
	@Test
	public void retrieveWeatherForecast_WithInvalidId() {
		Response response = getWeatherForecast("1029384756");
		Assert.assertEquals(response.getStatusCode(), 404, "Given id should not retrive any weather forecast");
		
	}
	
	@Test
	public void retrieveWeatherForecast_WithValidId() {
		Response postResponse = postWeatherForecast(2, 34,"Monday");
		Assert.assertEquals(postResponse.getStatusCode(), 201, "WeatherForecast has been successfully added");
		String forecastId = postResponse.getBody().jsonPath().getString("id");
		Response getresponse = getWeatherForecast(forecastId);
		verifyResponse(getresponse, 200, 2, 34, "Monday");
		
	}

	@Test
	public void createSuccessfulWeatherForecast() {
		Response postResponse = postWeatherForecast(0, 32,"Monday");
		verifyResponse(postResponse, 201,0, 32,"Monday");
	}
	
	@Test()
	public void updateWeatherForecast_WithValidId() {
		Response postResponse = postWeatherForecast(1, 33,"Monday");
		Assert.assertEquals(postResponse.getStatusCode(), 201, "WeatherForecast has been successfully added");
		String forecastId = postResponse.getBody().jsonPath().getString("id");
		Response updateResponse = updateWeatherForecast(forecastId, 2, 34, "Monday");
		verifyResponse(updateResponse, 204, 1, 33,"Monday");
	}
	
	@Test
	public void updateWeatherForecast_WithInvalidId() {
		Response response = updateWeatherForecast("1029384756", 1, 33,"Monday");
		
		//expected 404, but api gives 201
		Assert.assertEquals(response.getStatusCode(), 404, "Given id should not update any weather forecast");
		
	}
	
	@Test()
	public void deleteWeatherForecast_WithValidId() {
		Response postResponse = postWeatherForecast(1, 33,"Monday");
		Assert.assertEquals(postResponse.getStatusCode(), 201, "WeatherForecast has been successfully added");
		String forecastId = postResponse.getBody().jsonPath().getString("id");
		Response deleteResponse = deleteWeatherForecast(forecastId);
		Assert.assertEquals(deleteResponse.getStatusCode(), 204, "Given id should delete the weather forecast");
	}
	
	@Test
	public void deleteWeatherForecast_WithInvalidId() {
		Response response = deleteWeatherForecast("1029384756");
		Assert.assertEquals(response.getStatusCode(), 404, "Given id should not delete any weather forecast");
		
	}

	public Response getWeatherForecast(String id) {

		RestAssured.baseURI = baseURI;
		RequestSpecification httpRequest = RestAssured.given();
		Response response = httpRequest.request(Method.GET, "/" + id);
		return response;
		
	}
	
	public Response deleteWeatherForecast(String id) {

		RestAssured.baseURI = baseURI;
		RequestSpecification httpRequest = RestAssured.given();
		Response response = httpRequest.request(Method.DELETE, "/" + id);
		return response;
		
	}
	
	public Response postWeatherForecast(int tempC, int tempF, String summary) {

		RestAssured.baseURI = baseURI;
		RequestSpecification httpRequest = RestAssured.given();
		JSONObject requestParams = new JSONObject();
		DateTime currentTimeStamp = new DateTime();
		requestParams.put("date", currentTimeStamp.toLocalDateTime()); 
		requestParams.put("temperatureC", tempC);
		requestParams.put("temperatureF", tempF);
		requestParams.put("summary", summary);
		httpRequest.header("Content-Type", "application/json");
		httpRequest.body(requestParams.toString());
		Response response = httpRequest.request(Method.POST);
		return response;
	}
	
	public Response updateWeatherForecast(String id, int tempC, int tempF, String summary) {

		RestAssured.baseURI = baseURI;
		RequestSpecification httpRequest = RestAssured.given();
		JSONObject requestParams = new JSONObject();
		DateTime currentTimeStamp = new DateTime();
		requestParams.put("id", id); 
		requestParams.put("date", currentTimeStamp.toLocalDateTime()); 
		requestParams.put("temperatureC", tempC);
		requestParams.put("temperatureF", tempF);
		requestParams.put("summary", summary);
		httpRequest.header("Content-Type", "application/json");
		httpRequest.body(requestParams.toString());
		Response response = httpRequest.request(Method.POST);
		return response;
	}
	
	private void verifyResponse(Response response, int statusCode, int tempC, int tempF, String summary) {
		Assert.assertEquals(response.getStatusCode(), statusCode, "Given id should retrive the weather forecast");
		Assert.assertEquals(response.getBody().jsonPath().getInt("temperatureC"), tempC, "Value for temperature C is incorrect");
		Assert.assertEquals(response.getBody().jsonPath().getInt("temperatureF"), tempF, "Value for temperature F is incorrect");
		Assert.assertEquals(response.getBody().jsonPath().getString("summary"), summary, "Value for summary is incorrect");
	}

}
