package propets.configuration.accounting;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import propets.exceptions.accounting.UserAuthenticationException;

@Configuration
@RefreshScope
@Getter
@Setter
public class AccountConfiguration {

	@Value("${secret}")
	String secretKey;

	@Value("${exp.value}")
	long expPeriod;
	
	@Value("${requestToken}")
	String requestToken;
	
	@Value("${urlToLostFoundEdit}")
	String urlToLostFoundEdit;
	
	@Value("${messageService}")
	String messageService;
	
	@Value("${lostFoundService}")
	String lostFoundService;
	
	@Value("${urlToMessageEdit}")
	String urlToMessageEdit;

	public UserAccountCredentials tokenDecode(String token) {
		try {
			int pos = token.indexOf(" ");
			token = token.substring(pos + 1);
			String credential = new String(Base64.getDecoder().decode(token));
			String[] credentials = credential.split(":");
			return new UserAccountCredentials(credentials[0], credentials[1]);
		} catch (Exception e) {
			throw new UserAuthenticationException();
		}
	}

}
