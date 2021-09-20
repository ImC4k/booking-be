package imc4k.church.booking.user;

import imc4k.church.booking.exception.InsufficientRightException;
import imc4k.church.booking.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class UserService {
    private UserRepository userRepository;

    public User save(User user, User requester) {
        if (Boolean.FALSE.equals(requester.getIsAdmin())) {
            throw new InsufficientRightException();
        }
        setMetaData(user, requester);
        log.info("saving user: {}", user);
        return userRepository.save(user);
    }

    private void setMetaData(User user, User requester) {
        user.setAddedDate(new Date());
        user.setUpdatedDate(new Date());
        user.setApprovedBy(requester.getId());
    }

    public User findByEmail(String email) {
        return userRepository.findOneByEmail(email);
    }
}
