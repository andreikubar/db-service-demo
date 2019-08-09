import application.DatabaseApplication;
import com.google.common.collect.ImmutableList;
import model.Database;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

@SpringBootTest(classes = DatabaseApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DatabaseApplicationTest {

    private static final String TABLE_NAME = "table1";
    private static final ImmutableList<String> VALUES = ImmutableList.of("val1", "val2");
    private static final ImmutableList<String> UPDATE_VALUES = ImmutableList.of("field_1", "new_val1", "field_2", "new_val2");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Database databaseService;

    @Test
    public void databaseServiceTest() {

        int id = databaseService.insert(TABLE_NAME, VALUES);
        List<String> queryResult = databaseService.select(TABLE_NAME, id);
        Assertions.assertArrayEquals(VALUES.toArray(), queryResult.toArray());
        boolean rowsUpdated = databaseService.update(TABLE_NAME, UPDATE_VALUES, id);
        Assertions.assertTrue(rowsUpdated);
        queryResult = databaseService.select(TABLE_NAME, id);
        Assertions.assertEquals("new_val1", queryResult.get(0));
    }

    @Test
    public void httpTest() {
        String hostname = "http://localhost:" + port;
        ResponseEntity<Integer> response = restTemplate.exchange(hostname + "/database" + "/" + TABLE_NAME,
                HttpMethod.POST,
                new HttpEntity<>(VALUES), Integer.class);
        int id = response.getBody();
        Assertions.assertEquals(200, response.getStatusCodeValue());
        ResponseEntity<Boolean> updateResponse = restTemplate.exchange(hostname + "/database/" + TABLE_NAME + "/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(UPDATE_VALUES), Boolean.class);
        Assertions.assertTrue(updateResponse.getBody());
        List<String> queryResult = restTemplate.exchange(hostname + "/database" + "/" + TABLE_NAME + "/" + id,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<String>>(){}).getBody();
        Assertions.assertEquals(UPDATE_VALUES.get(1), queryResult.get(0));
    }
}
