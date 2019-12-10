package pickmeup.IntegrationTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.matches;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pickmeup.Application;
import pickmeup.gameLocations.GameLocations;
import pickmeup.gameLocations.GameLocationsController;

/**
 * The type User rest controller integration test.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, 
classes = Application.class)
@AutoConfigureTestDatabase(
  replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestPropertySource(
  locations = "classpath:application-integrationtest.properties")
public class GameLocationTest {

    @Autowired
    private MockMvc mvc;

    private GameLocations disneyLand;
    private static String TEST_JSON_STRING = "{\"id\":null,\"info\":\"*\",\"verified\":false,\"longt\":-93.0,\"lat\":42.0,\"name\":\"test\"}";
    private static String baseUrl = "/gameLocations";
    private static Gson gson = new Gson();
    
    
    /**
     * The name DisneyLand.
     */
    String n_disneyLand;

    @Autowired
    private GameLocationsController locationsControl;
    
    /**
     * Sets up.
     */
// write test cases here
    @Before
    public void setUp() {       
      n_disneyLand = "DisneyLand";
       disneyLand = new GameLocations(null, null, false); //id,info,verified
       disneyLand.setName(n_disneyLand);
       disneyLand.setLat(33.8120918);
       disneyLand.setLongt(-117.9189742);

    }
    
    /**
     * Validate add endpoint test.
     *
     * @throws Exception the exception
     */
    @Test
    public void addGameLocation_endpoint_test() throws Exception { //return 1 + LoggedInUser_json when user found
    String url = baseUrl + "/addGameLocation";
  

    String jsonObj = null;

    jsonObj = gson.toJson(disneyLand);

  
    ResultActions test = mvc.perform(MockMvcRequestBuilders.post(url)
    .content(jsonObj)
    .contentType(MediaType.APPLICATION_JSON));

    test.andExpect(MockMvcResultMatchers.status().isOk());
    assertTrue(test.andReturn().getResponse().getContentAsString().matches("1 [0-9]+"));

    }
    
    
    /**
     * Validate delete function.
     *
     * @throws Exception the exception
     */
    @Test
    public void DeleteGameLocation() throws Exception { //fail
      String url = baseUrl + "/addGameLocation";
  
    
      ResultActions test = mvc.perform(MockMvcRequestBuilders.post(url)
      .content(TEST_JSON_STRING)
      .contentType(MediaType.APPLICATION_JSON));
  
      test.andExpect(MockMvcResultMatchers.status().isOk());
      assertTrue(test.andReturn().getResponse().getContentAsString().matches("1 [0-9]+") );
     
      String response = test.andReturn().getResponse().getContentAsString();
      Integer locId = Integer.parseInt(response.split(" ")[1]); //get id

      assertEquals("1",locationsControl.deleteLocation(new GameLocations(locId, "", false)));//deletes by id
    
   
    }

     /**
     * Validate find function.
     *
     * @throws Exception the exception
     */
    @Test
    public void findGameLocation() throws Exception { //fail
      String url = baseUrl + "/findIdByCoordinates";
      Double frankParkLat = 42.020994;
      Double frankParkLongt = -93.665201;
      url +="/" + frankParkLat + "/" + frankParkLongt;
    
      ResultActions test = mvc.perform(MockMvcRequestBuilders.get(url));
  
      test.andExpect(MockMvcResultMatchers.status().isOk());
      assertTrue(test.andReturn().getResponse().getContentAsString().contains("Franklin Park") );

    }

      /**
     * Validate update function.
     *
     * @throws Exception the exception
     */
    @Test
    public void updateGameLocation() throws Exception { //fail
      String url = baseUrl + "/findIdByCoordinates";
      Double frankParkLat = 42.020994;
      Double frankParkLongt = -93.665201;
      url +="/" + frankParkLat + "/" + frankParkLongt;
    
      ResultActions test = mvc.perform(MockMvcRequestBuilders.get(url));
  
      test.andExpect(MockMvcResultMatchers.status().isOk());
      assertTrue(test.andReturn().getResponse().getContentAsString().contains("Franklin Park") );
     
      String response = test.andReturn().getResponse().getContentAsString();
      GameLocations frankpark = gson.fromJson(response, GameLocations.class);

      frankpark.setVerified(true);

      
      String jsonObj = null;
      jsonObj = gson.toJson(frankpark);


      String urlUpdate = baseUrl + "/updateGameLocation";
  
    
      ResultActions testUpdate = mvc.perform(MockMvcRequestBuilders.post(urlUpdate)
      .content(jsonObj)
      .contentType(MediaType.APPLICATION_JSON));
  
      testUpdate.andExpect(MockMvcResultMatchers.status().isOk());
      assertTrue(testUpdate.andReturn().getResponse().getContentAsString().matches("1") );
    
   
    }
 

        /**
     * Validate delete function.
     *
     * @throws Exception the exception
     */
    @Test
    public void DeleteGameLocation2() throws Exception { //fail
      String url = baseUrl + "/addGameLocation";
  
    
      ResultActions test = mvc.perform(MockMvcRequestBuilders.post(url)
      .content(TEST_JSON_STRING)
      .contentType(MediaType.APPLICATION_JSON));
  
      test.andExpect(MockMvcResultMatchers.status().isOk());
      assertTrue(test.andReturn().getResponse().getContentAsString().matches("1 [0-9]+") );
     
      String response = test.andReturn().getResponse().getContentAsString();
      Integer locId = Integer.parseInt(response.split(" ")[1]); //get id

      assertEquals("1",locationsControl.deleteLocation(new GameLocations(locId, "", false)));//deletes by id
      //delete again should fail
      assertTrue(locationsControl.deleteLocation(new GameLocations(locId, "", false)).contains("0 - error"));//deletes by id
    
   
    }
}