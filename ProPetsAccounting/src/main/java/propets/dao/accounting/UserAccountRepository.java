package propets.dao.accounting;

import org.springframework.data.mongodb.repository.MongoRepository;

import propets.model.accounting.User;

public interface UserAccountRepository extends MongoRepository<User, String> {

}
