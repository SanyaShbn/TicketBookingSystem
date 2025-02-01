package ticketbookingsystem.integration;

import com.example.ticketbookingsystem.config.WebMvcConfig;
import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.entity.User;
import com.example.ticketbookingsystem.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ticketbookingsystem.test_config.TestJpaConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig
@ContextConfiguration(classes = {TestJpaConfig.class, WebMvcConfig.class})
@ActiveProfiles("test")
@WebAppConfiguration
public class UserControllerIT {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void testRegisterUserAccount() throws Exception {
        UserDto userDto = UserDto.builder()
                .email("test@example.com")
                .password("password")
                .build();

        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .flashAttr("user", userDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertEquals(1, userRepository.count());
    }

    @Test
    public void testRegisterUserAccountWithException() throws Exception {
        userRepository.save(User.builder().email("test@example.com").password("password").build());

        UserDto userDto = UserDto.builder()
                .email("test@example.com")
                .password("password")
                .build();

        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .flashAttr("user", userDto))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeExists("errors"));
    }
}