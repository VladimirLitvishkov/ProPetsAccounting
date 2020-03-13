package propets.dao.accouting;

import org.springframework.data.jpa.repository.JpaRepository;

import propets.model.accouting.User;

public interface UserAccountRepository extends JpaRepository<User, String> {

}
