package ks.fs;

import kn.fs.FileStorageApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = FileStorageApplication.class)
public class FileStorageApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() {
    }

    @Test
    public void homeResponse() {
        String body = restTemplate.getForObject("/", String.class);
        assertThat(body).isEqualTo("File Storage Web UI: /swagger-ui.html");
    }

    @Test
    public void addResponse() {
        assertThat(restTemplate.getForObject("/add?a=2&b=3", String.class)).isEqualTo("5");
        assertThat(restTemplate.getForObject("/add?a=10&b=1243", String.class)).isEqualTo("1253");
    }
}
