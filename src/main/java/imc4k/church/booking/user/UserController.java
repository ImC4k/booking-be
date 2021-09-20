package imc4k.church.booking.user;

import imc4k.church.booking.service.UserTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.el.MethodNotFoundException;
import javax.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final HttpServletRequest request;
    private final String requesterEmail;

    @Autowired
    private UserService userService;
    @Autowired
    private UserTokenService userTokenService;

    public UserController(HttpServletRequest request) {
        this.request = request;
        this.requesterEmail = userTokenService.getRequesterEmail(request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody UserDto userDto) {
        String accept = request.getHeader("Accept");
        if (accept != null && (accept.contains("application/json") || accept.contains("*/*"))) {
            return userService.save(new User(userDto), userService.findByEmail(requesterEmail));
        }
        throw new MethodNotFoundException("Unsupported accept type");
    }
}
