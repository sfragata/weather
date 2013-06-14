/**
 * 
 */
package com.sfragata.weather.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.sfragata.weather.json.model.WeatherData;

/**
 * @author sfragata
 */
public class OpenWeatherTest {

	private final static Log LOG = LogFactory.getLog(OpenWeatherTest.class);

	private static String jsonOnTheFly;

	private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?mode=json&units=metric&q=";

	private static final String CITY = "Quebec,QC";

	@Before
	public void before() {
		InputStream is = null;
		HttpURLConnection http = null;
		try {
			LOG.info("Searching weather for " + CITY + " ...");
			http = (HttpURLConnection) (new URL(WEATHER_URL
					+ URLEncoder.encode(CITY, "UTF-8")).openConnection());
			http.setRequestMethod("GET");
			http.setDoInput(true);
			http.setDoOutput(true);
			http.connect();
			StringBuffer buffer = new StringBuffer();
			is = http.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = br.readLine()) != null)
				buffer.append(line + "\r\n");

			is.close();
			http.disconnect();
			jsonOnTheFly = buffer.toString();
		} catch (MalformedURLException e) {
			LOG.error("Error", e);
		} catch (IOException e) {
			LOG.error("Error", e);
		}

	}

	@Test
	public void testOpenweatherJackson() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.USE_STATIC_TYPING, true);
		try {
			WeatherData data = mapper
					.readValue(jsonOnTheFly, WeatherData.class);
			LOG.info(data);
		} catch (JsonParseException e) {
			LOG.error("Error", e);
		} catch (JsonMappingException e) {
			LOG.error("Error", e);
		} catch (IOException e) {
			LOG.error("Error", e);
		}
	}

	@Test
	public void testOpenweatherJSON() {
		try {
			JSONObject obj = new JSONObject(jsonOnTheFly);

			LOG.info("Name: " + obj.getString("name"));

			JSONObject coord = obj.getJSONObject("coord");

			LOG.info("Latitude: " + coord.getDouble("lat"));
			LOG.info("Longitude: " + coord.getDouble("lon"));

			JSONObject sys = obj.getJSONObject("sys");
			LOG.info("Country: " + sys.getString("country"));
			LOG.info("Sunrise: " + sys.getLong("sunrise"));
			LOG.info("Sunset: " + sys.getLong("sunset"));

			JSONObject main = obj.getJSONObject("main");
			LOG.info("Temp: " + main.getString("temp"));
			LOG.info("Humidity: " + main.getString("humidity"));
			LOG.info("Pressure: " + main.getString("pressure"));
			LOG.info("Temp_min: " + main.getString("temp_min"));
			LOG.info("Temp_max: " + main.getString("temp_max"));

			JSONObject wind = obj.getJSONObject("wind");
			LOG.info("Speed: " + wind.getDouble("speed"));
			LOG.info("Deg: " + wind.getDouble("deg"));

			JSONObject clouds = obj.getJSONObject("clouds");
			LOG.info("Percent: " + clouds.getDouble("all"));

			JSONObject weather = obj.getJSONArray("weather").getJSONObject(0);
			LOG.info("Id: " + weather.getInt("id"));
			LOG.info("Description: " + weather.getString("description"));
			LOG.info("Main: " + weather.getString("main"));
			LOG.info("Icon url: " + "http://openweathermap.org/img/w/"
					+ weather.getString("icon"));

		} catch (JSONException e) {
			LOG.error("Error", e);
		}
	}
}
