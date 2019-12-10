package pickmeup.UnitTests;

import java.util.Date;
import java.util.Optional;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pickmeup.game.Game;
import pickmeup.game.GameController;
import pickmeup.game.GameRepository;
import pickmeup.gameLocations.GameLocations;
import pickmeup.gameLocations.GameLocationsRepository;
import pickmeup.user.User;
import pickmeup.user.UserRepository;

@RunWith(SpringRunner.class)
@WebMvcTest(GameController.class)
public class GameTests {
    
    private static final String JSON_GAME = "{\"gameId\":null,\"chatId\":null,\"date\":" +
            "\"2019-11-19T12:00:00\",\"gameLocation\":null,\"p10Id\":null,\"p1Id\":null,\"p2Id\":null,\"p3Id\":null," +
            "\"p4Id\":null,\"p5Id\":null,\"p6Id\":null,\"p7Id\":null,\"p8Id\":null,\"p9Id\":null,\"pMax\":4," +
            "\"players\":null,\"score1\":null,\"score2\":null,\"sport\":\"Soccer\",\"status\":0,\"team1Count\":0," +
            "\"team2Count\":0,\"time\":\"13:00:00\"}";
   private static final String url = "/game/addTeam1Player";
            String url2 = "/game/addTeam2Player";
    
    
    @Autowired
    private MockMvc mvc;
    
    @MockBean
    private GameRepository gameRepo;
    @MockBean
    private GameLocationsRepository gameLocationsRepo;
    @MockBean
    private UserRepository userRepo;
    
    private User janeDoe;
    private User johnDoe;
    private User uncleBob;
    private User auntMay;
    private GameLocations mushroomKingdom = new GameLocations(1,"*", true);
    private Game bball;
    
    @Before
    public void setUp() {
        janeDoe = new User(1, "janeDoe");
        johnDoe = new User(2,"johnDoe");
        uncleBob = new User(3, "uncleBob");
        auntMay = new User(4, "auntMay");
        mushroomKingdom = new GameLocations(1,"*", true);
        mushroomKingdom.setName("mushroomKingdom");
        bball = new Game();
        bball.setGameId(33);
        bball.setDate(new Date());
        bball.setGameLocation(mushroomKingdom);
        bball.setPMax(4);
        bball.setTeam1Count(0);
        bball.setTeam2Count(0);
        bball.setSport("Basketball");

        
        Mockito.when(gameLocationsRepo.getOne(1)).thenReturn(mushroomKingdom);
        
        Mockito.when(gameRepo.findById(33)).thenReturn((Optional<Game>)Optional.of(bball));
        Mockito.when(userRepo.getOne(1)).thenReturn(janeDoe);
        Mockito.when(userRepo.getOne(2)).thenReturn(johnDoe);
        
    }
    
    @Test
    public void newGame() throws Exception{
        Mockito.when(gameRepo.save(Mockito.any())).thenAnswer(new Answer<Game>() {
    
            @Override
            public Game answer( InvocationOnMock invocation ) throws Throwable {
                ((Game) invocation.getArgument(0)).setGameId(1);
                return null;
            }
        });


        String url = "/game/addGame/" + mushroomKingdom.getId();
    
        ResultActions testUrl = mvc.perform(MockMvcRequestBuilders.post(url)
                .content(JSON_GAME)
                .contentType(MediaType.APPLICATION_JSON));
        testUrl.andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value("1 1")); //new game created with 1 for true, and 1 with new gameId
    }

    @Test
    public void addTeam1Player() throws Exception{
        setUp();
        String url = "/game/addTeam1Player";
        String url2 = "/game/addTeam2Player";
        JSONObject json = new JSONObject();
        json.put("gameId", 33);
        json.put("userId", 1);

        //test adding user 1
        ResultActions testUrl = mvc.perform(MockMvcRequestBuilders.post(url)
                .content(json.toString())
                .contentType(MediaType.APPLICATION_JSON));
        testUrl.andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value("true"));

        //test adding user 1 again
        testUrl = mvc.perform(MockMvcRequestBuilders.post(url)
                .content(json.toString())
                .contentType(MediaType.APPLICATION_JSON));
        testUrl.andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value("false"));

         //test adding user 1 to team 2
         testUrl = mvc.perform(MockMvcRequestBuilders.post(url2)
         .content(json.toString())
         .contentType(MediaType.APPLICATION_JSON));
 testUrl.andExpect(MockMvcResultMatchers.status().isOk())
 .andExpect(MockMvcResultMatchers.jsonPath("$").value("false"));

    }

    @Test
    public void addTeam2Player() throws Exception{
        setUp();
        String url = "/game/addTeam1Player";
        String url2 = "/game/addTeam2Player";
        JSONObject json = new JSONObject();
        json.put("gameId", 33);
        json.put("userId", 2);

        //test adding user 1
        ResultActions testUrl = mvc.perform(MockMvcRequestBuilders.post(url2)
                .content(json.toString())
                .contentType(MediaType.APPLICATION_JSON));
        testUrl.andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value("true"));

        //test adding user 1 again
        testUrl = mvc.perform(MockMvcRequestBuilders.post(url2)
                .content(json.toString())
                .contentType(MediaType.APPLICATION_JSON));
        testUrl.andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value("false"));

         //test adding user 1 to team 2
         testUrl = mvc.perform(MockMvcRequestBuilders.post(url)
         .content(json.toString())
         .contentType(MediaType.APPLICATION_JSON));
 testUrl.andExpect(MockMvcResultMatchers.status().isOk())
 .andExpect(MockMvcResultMatchers.jsonPath("$").value("false"));
    }

    @Test
    public void removePlayer() throws Exception{
        setUp();

        String url = "/game/addTeam1Player";
        String url2 = "/game/removePlayer";
        JSONObject json = new JSONObject();
        json.put("gameId", 33);
        json.put("userId", 1);

        //test adding user 1
        ResultActions testUrl = mvc.perform(MockMvcRequestBuilders.post(url)
                .content(json.toString())
                .contentType(MediaType.APPLICATION_JSON));
        testUrl.andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value("true"));

        //test removing user 1
        testUrl = mvc.perform(MockMvcRequestBuilders.post(url2)
                .content(json.toString())
                .contentType(MediaType.APPLICATION_JSON));
        testUrl.andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").value("true"));

         //test removing user 1 again
         testUrl = mvc.perform(MockMvcRequestBuilders.post(url2)
         .content(json.toString())
         .contentType(MediaType.APPLICATION_JSON));
 testUrl.andExpect(MockMvcResultMatchers.status().isOk())
 .andExpect(MockMvcResultMatchers.jsonPath("$").value("false"));

    }

    
}
