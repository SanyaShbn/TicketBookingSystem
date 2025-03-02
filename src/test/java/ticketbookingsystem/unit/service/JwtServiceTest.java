//package ticketbookingsystem.unit.service;
//
//import com.example.ticketbookingsystem.repository.RefreshTokenRepository;
//import com.example.ticketbookingsystem.security.JwtService;
//import com.example.ticketbookingsystem.security.RefreshToken;
//import jakarta.servlet.http.HttpServletRequest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetailsService;
//
//import java.time.Instant;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class JwtServiceTest {
//
//    private static final String USERNAME = "test@gmail.com";
//
//    private static final String PASSWORD = "password";
//
//    private static final List<GrantedAuthority> ROLES = List.of(new SimpleGrantedAuthority("USER"));
//
//    private static final String REFRESH_TOKEN = UUID.randomUUID().toString();
//
//    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 3600000;
//
//    static final String PREFIX = "Bearer";
//
//    @Mock
//    private RefreshTokenRepository refreshTokenRepository;
//
//    @Mock
//    private UserDetailsService userDetailsService;
//
//    @InjectMocks
//    private JwtService jwtService;
//
//    private RefreshToken refreshToken;
//
//    @BeforeEach
//    void setUp() {
//        refreshToken = new RefreshToken();
//        refreshToken.setToken(REFRESH_TOKEN);
//        refreshToken.setUsername(USERNAME);
//        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION_TIME));
//    }
//
//    @Test
//    public void testGenerateRefreshToken() {
//        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);
//
//        String generatedToken = jwtService.generateRefreshToken(USERNAME);
//
//        assertNotNull(generatedToken);
//        assertTrue(generatedToken.matches("^[a-fA-F0-9\\-]{36}$"));
//
//        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
//    }
//
//    @Test
//    public void testGetAuthentication() {
//        String token = jwtService.getToken(USERNAME, ROLES);
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(PREFIX + " " + token);
//
//        Authentication authentication = jwtService.getAuthentication(request);
//
//        assertNotNull(authentication);
//        assertEquals(USERNAME, authentication.getName());
//        assertEquals(ROLES, authentication.getAuthorities());
//    }
//
//    @Test
//    public void testValidateRefreshToken() {
//        when(refreshTokenRepository.findByToken(REFRESH_TOKEN)).thenReturn(Optional.of(refreshToken));
//
//        boolean isValid = jwtService.validateRefreshToken(REFRESH_TOKEN);
//
//        assertTrue(isValid);
//    }
//
//    @Test
//    public void testValidateRefreshTokenExpired() {
//        refreshToken.setExpiryDate(Instant.now().minusMillis(1000));
//        when(refreshTokenRepository.findByToken(REFRESH_TOKEN)).thenReturn(Optional.of(refreshToken));
//
//        boolean isValid = jwtService.validateRefreshToken(REFRESH_TOKEN);
//
//        assertFalse(isValid);
//    }
//
//    @Test
//    public void testGetRefreshToken() {
//        when(refreshTokenRepository.findByToken(REFRESH_TOKEN)).thenReturn(Optional.of(refreshToken));
//
//        RefreshToken result = jwtService.getRefreshToken(REFRESH_TOKEN);
//
//        assertNotNull(result);
//        assertEquals(REFRESH_TOKEN, result.getToken());
//    }
//
//    @Test
//    public void testGetRolesForUser() {
//        User user = new User(USERNAME, PASSWORD, ROLES);
//        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(user);
//
//        List<GrantedAuthority> roles = jwtService.getRolesForUser(USERNAME);
//
//        assertNotNull(roles);
//        assertEquals(ROLES, roles);
//    }
//}