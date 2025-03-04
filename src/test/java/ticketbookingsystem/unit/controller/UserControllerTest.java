package ticketbookingsystem.unit.controller;

import com.example.ticketbookingsystem.controller.UserController;
import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.exception.UserAlreadyExistException;
import com.example.ticketbookingsystem.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UserController userController;

    @Test
    void testRegisterUserAccount_success() throws UserAlreadyExistException {
        UserDto userDto = UserDto.builder().email("test@example.com").build();
        when(bindingResult.hasErrors()).thenReturn(false);
        doNothing().when(userService).registerNewUserAccount(any(UserDto.class));

        ResponseEntity<UserDto> response = userController.registerUserAccount(userDto, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userDto, response.getBody());
        verify(userService, times(1)).registerNewUserAccount(any(UserDto.class));
    }

    @Test
    void testRegisterUserAccount_validationErrors() {
        UserDto userDto = UserDto.builder().email("test@example.com").build();
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<UserDto> response = userController.registerUserAccount(userDto, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(userDto, response.getBody());
        verify(userService, never()).registerNewUserAccount(any(UserDto.class));
    }
}