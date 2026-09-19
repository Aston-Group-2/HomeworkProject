package console;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import userservice.services.UserService;

@SpringBootApplication(scanBasePackages = {"console", "userservice"})
@EnableJpaRepositories(basePackages = "userservice.repository")
@EntityScan(basePackages = "userservice.model")
public class Application {
    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        UserService userService = context.getBean(UserService.class);

        System.out.println("BEFORE APP RUN");
        App app = new App();
        app.run(userService);
    }
}
