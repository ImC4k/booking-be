package imc4k.church.booking.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import imc4k.church.booking.exception.InvalidTokenException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserTokenService {
    public String getRequesterEmail(HttpServletRequest request) {
        Object userProfile = request.getAttribute("USER_PROFILE");
        if (!(userProfile instanceof GoogleIdToken.Payload)) {
            throw new InvalidTokenException();
        }
        return ((GoogleIdToken.Payload) userProfile).getEmail();
    }
}
