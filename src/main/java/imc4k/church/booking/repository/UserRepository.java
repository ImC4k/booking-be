package imc4k.church.booking.repository;

import imc4k.church.booking.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    public User findOneByEmail(String email);
}
