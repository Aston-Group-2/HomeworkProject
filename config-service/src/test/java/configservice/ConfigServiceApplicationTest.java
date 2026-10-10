package configservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Проверяет, что Config Server успешно стартует (context load).
 */
@SpringBootTest
class ConfigServiceApplicationTest {

    @Test
    void contextLoads() {
        // Если контекст не поднялся — тест упадёт
    }
}
