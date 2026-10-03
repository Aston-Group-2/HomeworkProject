package notificationservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.event.OperationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import notificationservice.controller.NotificationController;
import notificationservice.dto.SendEmailRequest;
import notificationservice.service.EmailService;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendEmail_ShouldReturnOkAndCallService() throws Exception {
        SendEmailRequest request = new SendEmailRequest();
        request.setEmail("user@test.com");
        request.setOperation(OperationType.CREATE);

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(emailService).sendNotification(eq("user@test.com"), eq(OperationType.CREATE));
    }

    @Test
    void sendEmail_WhenInvalidEmail_ShouldReturnBadRequest() throws Exception {
        SendEmailRequest request = new SendEmailRequest();
        request.setEmail("invalid-email");
        request.setOperation(OperationType.DELETE);

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(emailService);
    }

    @Test
    void sendEmail_WhenOperationMissing_ShouldReturnBadRequest() throws Exception {
        SendEmailRequest request = new SendEmailRequest();
        request.setEmail("user@test.com");

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(emailService);
    }
}
