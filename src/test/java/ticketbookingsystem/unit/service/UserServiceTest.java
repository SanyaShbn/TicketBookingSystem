package ticketbookingsystem.unit.service;

import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.entity.Role;
import com.example.ticketbookingsystem.entity.User;
import com.example.ticketbookingsystem.exception.UserAlreadyExistException;
import com.example.ticketbookingsystem.mapper.UserMapper;
import com.example.ticketbookingsystem.repository.UserRepository;
import com.example.ticketbookingsystem.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final String USERNAME = "test@example.com";

    private static final String USER_PASSWORD = "password";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .email(USERNAME)
                .password(BCrypt.hashpw(USER_PASSWORD, BCrypt.gensalt()))
                .role(Role.USER)
                .build();

        userDto = UserDto.builder()
                .email(USERNAME)
                .password(USER_PASSWORD)
                .build();
    }

    @Test
    public void testLoginSuccess() {
        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        Optional<UserDto> result = userService.login(USERNAME, USER_PASSWORD);

        assertTrue(result.isPresent());
        assertEquals(USERNAME, result.get().getEmail());
    }

    @Test
    public void testLoginFailure() {
        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());

        Optional<UserDto> result = userService.login(USERNAME, "wrongpassword");

        assertFalse(result.isPresent());
    }

    @Test
    public void testFindByEmail() {
        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        Optional<UserDto> result = userService.findByEmail(USERNAME);

        assertTrue(result.isPresent());
        assertEquals(USERNAME, result.get().getEmail());
    }

    @Test
    public void testRegisterNewUserAccountSuccess() throws UserAlreadyExistException {
        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.empty());
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        assertDoesNotThrow(() -> userService.registerNewUserAccount(userDto));
    }

    @Test
    public void testRegisterNewUserAccountFailure() {
        when(userRepository.findByEmail(USERNAME)).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistException.class, () -> userService.registerNewUserAccount(userDto));
    }
}