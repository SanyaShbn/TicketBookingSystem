//package ticketbookingsystem.unit.controller;
//
//import com.example.ticketbookingsystem.controller.LoginController;
//import com.example.ticketbookingsystem.security.AccountCredentials;
//import com.example.ticketbookingsystem.security.JwtService;
//import com.example.ticketbookingsystem.security.RefreshToken;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class LoginControllerTest {
//
//    private static final String USERNAME = "username";
//
//    private static final String ACCESS_TOKEN = "accessToken";
//
//    private static final String REFRESH_TOKEN = "refreshToken";
//
//    private static final String BEARER_PREFIX = "Bearer ";
//
//    private static final String REFRESH_TOKEN_HEADER = "Refresh-Token";
//
//    @Mock
//    private JwtService jwtService;
//
//    @Mock
//    private AuthenticationManager authenticationManager;
//
//    @InjectMocks
//    private LoginController loginController;
//
//    @Test
//    void testGetToken_success() {
//        AccountCredentials credentials = new AccountCredentials();
//        Authentication auth = mock(Authentication.class);
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
//        when(auth.getName()).thenReturn(USERNAME);
//        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
//        when(jwtService.getToken(anyString(), any())).thenReturn(ACCESS_TOKEN);
//        when(jwtService.generateRefreshToken(anyString())).thenReturn(REFRESH_TOKEN);
//
//        ResponseEntity<Map<String, String>> response = loginController.getToken(credentials);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(BEARER_PREFIX + ACCESS_TOKEN, response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
//        assertEquals(REFRESH_TOKEN, response.getHeaders().getFirst(REFRESH_TOKEN_HEADER));
//        assertEquals(ACCESS_TOKEN, response.getBody().get("accessToken"));
//        assertEquals(REFRESH_TOKEN, response.getBody().get("refreshToken"));
//    }
//
//    @Test
//    void testRefresh_success() {
//        Map<String, String> body = new HashMap<>();
//        body.put("refreshToken", "oldRefreshToken");
//        when(jwtService.validateRefreshToken(anyString())).thenReturn(true);
//        RefreshToken refreshToken = new RefreshToken();
//        refreshToken.setUsername(USERNAME);
//        when(jwtService.getRefreshToken(anyString())).thenReturn(refreshToken);
//        when(jwtService.getRolesForUser(anyString())).thenReturn(Collections.emptyList());
//        when(jwtService.getToken(anyString(), any())).thenReturn("newAccessToken");
//        when(jwtService.generateRefreshToken(anyString())).thenReturn("newRefreshToken");
//
//        ResponseEntity<Map<String, String>> response = loginController.refresh(body);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals(BEARER_PREFIX + "newAccessToken",
//                response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
//        assertEquals("newRefreshToken", response.getHeaders().getFirst(REFRESH_TOKEN_HEADER));
//        assertEquals("newAccessToken", response.getBody().get("accessToken"));
//        assertEquals("newRefreshToken", response.getBody().get("refreshToken"));
//    }
//
//    @Test
//    void testRefresh_invalidToken() {
//        Map<String, String> body = new HashMap<>();
//        body.put("refreshToken", "invalidRefreshToken");
//        when(jwtService.validateRefreshToken(anyString())).thenReturn(false);
//
//        ResponseEntity<Map<String, String>> response = loginController.refresh(body);
//
//        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
//        assertNull(response.getBody());
//    }
//}