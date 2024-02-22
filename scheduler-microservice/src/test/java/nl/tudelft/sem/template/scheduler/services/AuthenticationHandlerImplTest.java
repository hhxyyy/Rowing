package nl.tudelft.sem.template.scheduler.services;

import nl.tudelft.sem.template.scheduler.authentication.AuthManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class AuthenticationHandlerImplTest {

    private transient AuthenticationHandler authenticationHandler;

    @BeforeEach
    public void setup() {
        authenticationHandler = new AuthenticationHandlerImpl();
    }

    @Test
    @WithMockUser(roles = "Admin")
    void isAdminValid() {
        assertTrue(authenticationHandler.isAdmin());
    }

    @Test
    @WithMockUser(roles = "User")
    void isAdminInvalid() {
        assertFalse(authenticationHandler.isAdmin());
    }
}