package pickmeup.UnitTests;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pickmeup.user.User;
import pickmeup.user.UserController;
import pickmeup.user.UserRepository;

/**
 * The type User tests.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepo;
    private User alex;
    
    
    /**
     * The Username a.
     */
    String username_A;
    
    /**
     * Sets up.
     */
// write test cases here
    @Before
    public void setUp() {
        alex = new User(23, "alex22");
        username_A = "ALEX22";
        String password = DigestUtils.sha256Hex("johnnyBravo25");//common-codecs "sha2-256" tool used for encryption of sent password
        alex.setPassword(password);

        Mockito.when(userRepo.findByUsernameIgnoreCase(alex.getUsername().toUpperCase())).thenReturn(alex);
        Mockito.when(userRepo.findByUsernameIgnoreCase(alex.getUsername())).thenReturn(alex);
        
        Mockito.when(userRepo.findByEmailIgnoreCase(alex.getUsername())).thenReturn(null);
    }
    
    /**
     * When find by name then return user.
     */
    @Test
    public void whenFindByName_thenReturnUser() {
        // when
        User found = userRepo.findByUsernameIgnoreCase(username_A);

        // then
        assertThat(found.getUsername()).isEqualTo(alex.getUsername());
    }
    
    /**
     * Find by username endpoint test 1.
     *
     * @throws Exception the exception
     */
    @Test
    public void findByUsername_endpoint_test1() throws Exception { //return 1 when user found
        /// user/findByUsername/{username}
        String url = "/user/findByUsername/" + username_A;
    mvc.perform(MockMvcRequestBuilders.get(url)
    .contentType(MediaType.APPLICATION_JSON))
    .andExpect(MockMvcResultMatchers.status().isOk())
    .andExpect(MockMvcResultMatchers.jsonPath("$").value("1"));
   
    }
    
    /**
     * Find by username endpoint test 2.
     *
     * @throws Exception the exception
     */
    @Test
    public void findByUsername_endpoint_test2() throws Exception { //return 0 when user not found
        /// user/findByUsername/{username}
        String url = "/user/findByUsername/" + 
        "candyCash";
    mvc.perform(MockMvcRequestBuilders.get(url)
    .contentType(MediaType.APPLICATION_JSON))
    .andExpect(MockMvcResultMatchers.status().isOk())
    .andExpect(MockMvcResultMatchers.jsonPath("$").value("0"));
        //password = DigestUtils.sha256Hex(input.getPassword());//common-codecs "sha2-256" tool used for encryption of sent password

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
    //password = DigestUtils.sha256Hex(password);//common-codecs "sha2-256" tool used for encryption of sent password
    
   
    JSONObject jsonObj = new JSONObject();

    jsonObj.put("username", username);
    jsonObj.put("password", password);

    mvc.perform(MockMvcRequestBuilders.post(url)
    .content(jsonObj.toString())
    .contentType(MediaType.APPLICATION_JSON))
    .andExpect(MockMvcResultMatchers.status().isOk())
    .andExpect(MockMvcResultMatchers.jsonPath("$").value(2));

    String wrongUsername = "alx22";
    //password = DigestUtils.sha256Hex(password);//common-codecs "sha2-256" tool used for encryption of sent password
    
   
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