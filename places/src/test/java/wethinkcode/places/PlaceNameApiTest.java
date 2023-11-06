package wethinkcode.places;

import java.io.IOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.*;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * *Functional* tests of the PlaceNameService.
 */
public class PlaceNameApiTest
{
    public static final int TEST_PORT = 7777;

    private static PlaceNameService server;
    private static HttpClient httpClient;
    String provinces = serverUrl()+"/provinces";

    @BeforeAll
    public static void startServer() throws IOException{
        server = new PlaceNameService().initialise();
        server.start(TEST_PORT); 
        Routes.configureRoutes(server.getServer(), server.getPlaces());
        httpClient = HttpClients.createDefault();

    }

    @AfterAll
    public static void stopServer(){
        server.stop();
    }

    public JSONObject stringToJsonObject( JsonNode jsonResponse ){
        return jsonResponse.getObject();
    }
    @Test
    public void getProvincesJson() throws ClientProtocolException, IOException{
        HttpGet request = new HttpGet(provinces);
        org.apache.http.HttpResponse response = httpClient.execute(request);
        HttpEntity ent = response.getEntity();
        String jsonString = EntityUtils.toString(ent);
        JSONArray jsonObject = new JSONArray(jsonString);
        assertEquals( 9, jsonObject.length() );

    }

    @Test
    public void getTownsInAProvince_provinceExistsInDb() throws ClientProtocolException, IOException{
        String req = serverUrl() + "/towns/KwaZulu-Natal" ;
        HttpGet request = new HttpGet(req);
        org.apache.http.HttpResponse response = httpClient.execute(request);
        HttpEntity ent = response.getEntity();
        String jsonString = EntityUtils.toString(ent);
        JSONArray jsonObject = new JSONArray(jsonString);
        assertEquals( 659, jsonObject.length() );

    }

    @Test
    public void getTownsInAProvince_noSuchProvinceInDb() throws ClientProtocolException, IOException{
        String req = serverUrl() + "/towns/Oregon" ;
        HttpGet request = new HttpGet(req);
        org.apache.http.HttpResponse response = httpClient.execute(request);
        String responseBody = EntityUtils.toString(response.getEntity());
        assertTrue(responseBody.contains("No towns found for province Oregon"));
            }

    private String serverUrl(){
        return "http://localhost:" + TEST_PORT;
    }
}
