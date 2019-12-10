package pickmeup.IntegrationTests;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pickmeup.Application;
import pickmeup.user.User;
import pickmeup.user.UserController;

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
public class UserRestControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    private User alex;
    
    
    /**
     * The Username a.
     */
    String username_A;

    @Autowired
    private UserController userControl;
    
    /**
     * Sets up.
     */
// write test cases here
    @Before
    public void setUp() {
        alex = new User(23, "alex22");
        username_A = "ALEX22";
        String password = "johnnyBravo25";
        alex.setPassword(password);
        alex.setEmail("alex@alex.com");

        userControl.createUser(alex);
    }
    
    /**
     * Validate user endpoint test.
     *
     * @throws Exception the exception
     */
    @Test
    public void validateUser_endpoint_test() throws Exception { //return 1 + LoggedInUser_json when user found
    String url = "/user/validateUser/";
    
    String username = "alex22";
    String password = "johnnyBravo25";
    
   
    JSONObject jsonObj = new JSONObject();

    jsonObj.put("username", username);
    jsonObj.put("password", password);

    mvc.perform(MockMvcRequestBuilders.post(url)
    .content(jsonObj.toString())
    .contentType(MediaType.APPLICATION_JSON))
    .andExpect(MockMvcResultMatchers.status().isOk())
    .andExpect(MockMvcResultMatchers.jsonPath("$").isString());
    
   
    }
    
    
    /**
     * Validate user endpoint test 2.
     *
     * @throws Exception the exception
     */
    @Test
    public void validateUser_endpoint_test2() throws Exception { //fail
    String url = "/user/validateUser/";
    
    String username = "alex22";
    String password = "JohnnyBravo25";
   
    JSONObject jsonObj = new JSONObject();

    jsonObj.put("username", username);
    jsonObj.put("password", password);

    mvc.perform(MockMvcRequestBuilders.post(url)
    .content(jsonObj.toString())
    .contentType(MediaType.APPLICATION_JSON))
    .andExpect(MockMvcResultMatchers.status().isOk())
    .andExpect(MockMvcResultMatchers.jsonPath("$").value(2));

    String wrongUsername = "alx22";
    
   
    jsonObj = new JSONObject();

    jsonObj.put("username", wrongUsername);
    jsonObj.put("password", password);

    mvc.perform(MockMvcRequestBuilders.post(url)
    .content(jsonObj.toString())
    .contentType(MediaType.APPLICATION_JSON))
    .andExpect(MockMvcResultMatchers.status().isOk())
    .andExpect(MockMvcResultMatchers.jsonPath("$").value("0"));
    
   
    }
 
}