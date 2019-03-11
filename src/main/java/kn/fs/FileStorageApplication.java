package kn.fs;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class FileStorageApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileStorageApplication.class, args);
    }

    @GetMapping("/")
    public String home() {
        return "File Storage Web UI: /swagger-ui.html";
    }

    @GetMapping("/add")
    @ApiOperation(value = "Sum", notes = "Addition of two numbers (Swagger example)")
    public int add(@ApiParam(required = true, name = "a", value = "Integer number") int a,
                   @ApiParam(required = true, name = "b", value = "Another integer number") int b) {
        return a + b;
    }
}