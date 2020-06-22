package propets;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import propets.dao.accounting.UserAccountRepository;
import propets.model.accounting.User;

@SpringBootApplication
public class ProPetsAccountingApplication implements CommandLineRunner {
	
	@Autowired
	UserAccountRepository userAccountRepository;

	public static void main(String[] args) {
		SpringApplication.run(ProPetsAccountingApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String hashPassword = BCrypt.hashpw("admin", BCrypt.gensalt());
		if (!userAccountRepository.existsById("admin")) {
			User admin = User.builder().email("admin").password(hashPassword).name("Super").role("User")
					.role("Administrator").build();
			userAccountRepository.save(admin);
		}

	}

}
