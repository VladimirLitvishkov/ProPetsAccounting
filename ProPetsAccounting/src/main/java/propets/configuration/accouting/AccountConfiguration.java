package propets.configuration.accouting;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import propets.exceptions.accouting.UserAuthenticationException;

@Configuration
@ManagedResource
@RefreshScope
public class AccountConfiguration {
	
	@Value(value = "${secret}")
	String secretKey;
	
	@ManagedAttribute
	public String getSecretKey() {
		return secretKey;
	}
	
	@ManagedAttribute
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
	@Value("${exp.value}")
	long expPeriod;

	@ManagedAttribute
	public long getExpPeriod() {
		return expPeriod;
	}

	@ManagedAttribute
	public void setExpPeriod(long expPeriod) {
		this.expPeriod = expPeriod;
	}

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

//	public Date getExpDate() {
//		return Date.from(ZonedDateTime.now().plusDays(expPeriod).toInstant());
//	}
	
	

}
