package propets.service.accounting;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import propets.configuration.accounting.AccountConfiguration;
import propets.dao.accounting.UserAccountRepository;
import propets.dto.accounting.UserBlockDto;
import propets.dto.accounting.UserDataToPostDto;
import propets.dto.accounting.UserEditDto;
import propets.dto.accounting.UserProfileDto;
import propets.dto.accounting.UserRegRespDto;
import propets.dto.accounting.UserRegisterDto;
import propets.exceptions.accounting.UserNotFoundException;
import propets.exceptions.accounting.WrongLoginException;
import propets.model.accounting.User;

@Service
public class UserAccountServiceImpl implements UserAccountService {

	@Autowired
	UserAccountRepository userAccountRepository;

	@Autowired
	AccountConfiguration configuration;

	@Override
	public ResponseEntity<UserRegRespDto> userRegistration(UserRegisterDto userReg) {
		if (userAccountRepository.existsById(userReg.getEmail())) {
			throw new WrongLoginException();
		}
		String hashPassword = BCrypt.hashpw(userReg.getPassword(), BCrypt.gensalt());
		User user = User.builder().email(userReg.getEmail()).password(hashPassword).name(userReg.getName()).role("User")
				.build();
		userAccountRepository.save(user);

		String newXToken = buildXToken(user);
		if (newXToken == null) {
			return null;
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-token", newXToken);
		ResponseEntity<UserRegRespDto> response = new ResponseEntity<UserRegRespDto>(buildUserRegRespDto(user), headers,
				HttpStatus.OK);
		return response;
	}

	private String buildXToken(User user) {
		String token = null;
		try {
			return token = Jwts.builder().setSubject("User")
					.setExpiration(Date.from(ZonedDateTime.now().plusDays(configuration.getExpPeriod()).toInstant()))
					.claim("userId", user.getEmail()).claim("userName", user.getName())
					.claim("password", user.getPassword()).claim("avatar", user.getAvatar())
					.signWith(SignatureAlgorithm.HS256, configuration.getSecretKey().getBytes("UTF-8")).compact();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return token;
		}
	}

	private UserRegRespDto buildUserRegRespDto(User user) {
		return UserRegRespDto.builder().email(user.getEmail()).name(user.getName()).roles(user.getRoles())
				.phone(user.getPhone()).avatar(user.getAvatar()).build();
	}

	private UserProfileDto buildUserProfileDto(User user) {
		return UserProfileDto.builder().email(user.getEmail()).name(user.getName()).phone(user.getPhone())
				.block(user.getBlock()).avatar(user.getAvatar()).regDate(user.getRegDate()).roles(user.getRoles())
				.build();
	}

	@Override
	public ResponseEntity<UserProfileDto> login(String login) {
		User user = userAccountRepository.findById(login).get();
		String newXToken = buildXToken(user);
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-token", newXToken);
		return new ResponseEntity<UserProfileDto>(buildUserProfileDto(user), headers, HttpStatus.OK);
	}

	@Override
	public UserProfileDto editUser(String login, UserEditDto userEditDto) {
		User user = userAccountRepository.findById(login).get();
		if (userEditDto.getName() != null) {
			user.setName(userEditDto.getName());
		}
		if (userEditDto.getPhone() != null) {
			user.setPhone(userEditDto.getPhone());
		}
		if (userEditDto.getImageURL() != null) {
			user.setAvatar(userEditDto.getImageURL());
		}
		userAccountRepository.save(user);
//		need Kafka
		RestTemplate restTemplate = new RestTemplate();
		URI urlToEdit = null;
		UserDataToPostDto authorEditDto = UserDataToPostDto.builder().userName(user.getName()).avatar(user.getAvatar()).build();
		for (String serviceName : user.getActivities().keySet()) {
			String urlToService = null;
			if (serviceName.equalsIgnoreCase(configuration.getLostFoundService())) {
				urlToService = configuration.getUrlToLostFoundEdit();
			} else if (serviceName.equalsIgnoreCase(configuration.getMessageService())) {
				urlToService = configuration.getUrlToMessageEdit();
			}
			for (String idPost : user.getActivities().get(serviceName)) {
				try {
					urlToEdit = new URI(urlToService+idPost);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				RequestEntity<UserDataToPostDto> request = new RequestEntity<>(authorEditDto, HttpMethod.DELETE, urlToEdit);
				restTemplate.exchange(request, UserDataToPostDto.class);
			}
		}
		
		return buildUserProfileDto(user);
	}

	@Override
	public UserProfileDto removeUser(String login) {
		User user = userAccountRepository.findById(login).get();
		UserProfileDto userRes = buildUserProfileDto(user);
		userAccountRepository.deleteById(login);
		return userRes;
	}

	@Override
	public UserBlockDto blockAccount(String login, boolean status) {
		User user = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
		if (status) {
			user.setBlock(true);
		} else {
			user.setBlock(false);
		}
		userAccountRepository.save(user);
		return UserBlockDto.builder().login(user.getEmail()).block(user.getBlock()).build();

	}

	@Override
	public Set<String> addRole(String login, String role) {
		User user = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
		user.addRole(role);
		userAccountRepository.save(user);
		return user.getRoles();
	}

	@Override
	public Set<String> removeRole(String login, String role) {
		User user = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
		user.removeRole(role);
		userAccountRepository.save(user);
		return user.getRoles();
	}

	@Override
	public UserProfileDto findUserByID(String login) {
		User user = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
		return buildUserProfileDto(user);
	}

	@Override
	public ResponseEntity<String> checkXToken(String xToken) {
		String newXToken = null;
		User user = null;
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(configuration.getSecretKey().getBytes("UTF-8"))
					.parseClaimsJws(xToken);
			String userId = (String) claims.getBody().get("userId");
			user = userAccountRepository.findById(userId).orElseThrow(UserNotFoundException::new);
			newXToken = Jwts.builder().addClaims(claims.getBody())
					.setExpiration(Date.from(ZonedDateTime.now().plusDays(configuration.getExpPeriod()).toInstant()))
					.signWith(SignatureAlgorithm.HS256, configuration.getSecretKey().getBytes("UTF-8")).compact();
		} catch (Exception e) {
			return new ResponseEntity<String>(HttpStatus.CONFLICT);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-token", newXToken);
		headers.add("X-userId", user.getEmail());
		return new ResponseEntity<String>(headers, HttpStatus.OK);
	}

	@Override
	public boolean addFavorite(String nameService, String postId, String userId) {
		User user = userAccountRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		boolean result = user.addFavorite(nameService, postId);
		userAccountRepository.save(user);
		return result;
	}

	@Override
	public boolean removeFavorite(String nameService, String postId, String userId) {
		User user = userAccountRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		boolean result = user.removeFavorite(nameService, postId);
		userAccountRepository.save(user);
		return result;
	}

	@Override
	public HashMap<String, Set<String>> getFavorites(String userId) {
		User user = userAccountRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		return user.getFavorites();
	}

	@Override
	public boolean addActivities(String nameService, String postId, String userId) {
		User user = userAccountRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		boolean result = user.addActivities(nameService, postId);
		userAccountRepository.save(user);
		return result;
	}

	@Override
	public boolean removeActivities(String nameService, String postId, String userId) {
		User user = userAccountRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		boolean result = user.removeActivities(nameService, postId);
		userAccountRepository.save(user);
		return result;
	}

	@Override
	public HashMap<String, Set<String>> getActivities(String userId) {
		User user = userAccountRepository.findById(userId).orElseThrow(UserNotFoundException::new);
		return user.getActivities();
	}

}
