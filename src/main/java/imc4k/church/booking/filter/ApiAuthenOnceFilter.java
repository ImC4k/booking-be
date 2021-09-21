package imc4k.church.booking.filter;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import imc4k.church.booking.exception.InvalidTokenException;
import imc4k.church.booking.exception.UserNotFoundException;
import imc4k.church.booking.service.UserTokenService;
import imc4k.church.booking.user.UserService;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
public class ApiAuthenOnceFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(ApiAuthenOnceFilter.class);
    public static final String USER_PROFILE = "USER_PROFILE";
    @Autowired
    private UserService userService;
    @Autowired
    private UserTokenService userTokenService;
//    @Autowired
//    private AuditLogService auditLogService;
    private GoogleIdTokenVerifier verifier;

    @Autowired
    public ApiAuthenOnceFilter(@Value("${google.oauth.apiKey}") String CLIENT_ID) {
        verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (httpServletRequest.getRequestURI().startsWith("/")) {
            String token = "";
            try {
                token = httpServletRequest.getHeader("Authorization").substring(OAuth2AccessToken.TokenType.BEARER.getValue().length() + 1);
                if (!token.isEmpty()) {
                    log.debug("with token {}", token);
                }
                else {
                    log.warn("no token");
                }
                GoogleIdToken.Payload payload = verifyToken(token);
                verifyIsValidUser(payload);
                httpServletRequest.setAttribute(USER_PROFILE, payload);
                log.info("approved request {} {} by {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), getClientIp(httpServletRequest));
                logRequest(httpServletRequest, token, "approved");
            }
            catch (InvalidTokenException | NullPointerException | UserNotFoundException | IllegalArgumentException e) {
                log.error("oops", e);
                httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "User not approved");
                log.warn("rejected request {} {} by {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), getClientIp(httpServletRequest));
                logRequest(httpServletRequest, token, "rejected");
                return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void logRequest(HttpServletRequest request, String token, String status) {
//        AuditLog newRequest = AuditLog.builder()
//                .hostname(getClientIp(request))
//                .method(request.getMethod())
//                .url(request.getRequestURI())
//                .token(token)
//                .status(status)
//                .build();
//        AuditLog savedLog = auditLogService.createLog(newRequest);
//        log.debug("logged request {}", savedLog);
    }

    private void verifyIsValidUser(GoogleIdToken.Payload payload) {
        String email = payload.getEmail();
        userService.findByEmail(email);
    }

    private GoogleIdToken.Payload verifyToken(String token) throws GeneralSecurityException, IOException {
        try {
            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                return idToken.getPayload();
            }
        } catch (Exception e) {
            log.error("huh?", e);
        }
        throw new InvalidTokenException();
    }

    private static String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }
}
