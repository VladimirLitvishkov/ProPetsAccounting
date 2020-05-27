package propets.dao.accouting;

import org.springframework.data.mongodb.repository.MongoRepository;

import propets.model.accouting.User;

public interface UserAccountRepository extends MongoRepository<User, String> {

}
