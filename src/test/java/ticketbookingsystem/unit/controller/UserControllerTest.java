package ticketbookingsystem.unit.controller;

import com.example.ticketbookingsystem.controller.UserController;
import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.exception.UserAlreadyExistException;
import com.example.ticketbookingsystem.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ticketbookingsystem.test_config.TestMvcConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = {TestMvcConfig.class})
public class UserControllerTest {

    MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setViewResolvers(new InternalResourceViewResolver("/WEB-INF/jsp/", ".jsp"))
                .build();
    }

    @Test
    public void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
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
        UserDto userDto = UserDto.builder().build();

        doNothing().when(userService).registerNewUserAccount(any(UserDto.class));

        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .flashAttr("user", userDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    public void testRegisterUserAccountWithException() throws Exception {
        UserDto userDto = UserDto.builder().build();

        doThrow(new UserAlreadyExistException("User already exists")).when(userService)
                .registerNewUserAccount(any(UserDto.class));

        mockMvc.perform(post("/registration")
                        .with(csrf())
                        .flashAttr("user", userDto))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeExists("errors"));
    }
}