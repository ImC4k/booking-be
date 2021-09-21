package imc4k.church.booking.user;

import imc4k.church.booking.service.UserTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.el.MethodNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    private final HttpServletRequest request;

    @Autowired
    private UserService userService;
    @Autowired
    private UserTokenService userTokenService;

    public UserController(HttpServletRequest request) {
        this.request = request;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        log.info("requester is {}", userTokenService.getRequesterEmail(request));
        return userService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody UserDto userDto) {
        String accept = request.getHeader("Accept");
        if (accept != null && (accept.contains("application/json") || accept.contains("*/*"))) {
            String requesterEmail = userTokenService.getRequesterEmail(request);
            return userService.save(new User(userDto), userService.findByEmail(requesterEmail));
        }
        throw new MethodNotFoundException("Unsupported accept type");
    }
}
