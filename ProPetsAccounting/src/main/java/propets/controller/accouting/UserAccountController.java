package propets.controller.accouting;

import java.security.Principal;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import propets.dto.accouting.UserBlockDto;
import propets.dto.accouting.UserEditDto;
import propets.dto.accouting.UserProfileDto;
import propets.dto.accouting.UserRegRespDto;
import propets.dto.accouting.UserRegisterDto;
import propets.service.accouting.UserAccountService;


@RestController
@RequestMapping("/{lang}/v1")
@CrossOrigin(origins = "*")
public class UserAccountController {

	@Autowired
	UserAccountService userAccountService;

	@PostMapping
	public ResponseEntity<UserRegRespDto> userRegistration(@RequestBody UserRegisterDto user) {
		return userAccountService.userRegistration(user);
	}

	@PostMapping("/login")
	public ResponseEntity<UserProfileDto> login(Principal principal) {
		return userAccountService.login(principal.getName());
	}

	@PutMapping("/{login}")
	public UserProfileDto editUser(Principal principal, @RequestBody UserEditDto userEditDto) {
		return userAccountService.editUser(principal.getName(), userEditDto);
	}

	@DeleteMapping("/{login}")
	public UserProfileDto removeUser(Principal principal) {
		return userAccountService.removeUser(principal.getName());
	}

	@PostMapping("/{login:.*}/role/{role}")
	public Set<String> addRole(@PathVariable String login, @PathVariable String role) {
		return userAccountService.addRole(login, role);
	}

	@DeleteMapping("/{login:.*}/role/{role}")
	public Set<String> removeRole(@PathVariable String login, @PathVariable String role) {
		return userAccountService.removeRole(login, role);
	}

	@PutMapping("/{login.*}/block/{status}")
	public UserBlockDto blockAccount(@PathVariable String login, @PathVariable boolean status) {
		return userAccountService.blockAccount(login, status);
	}
	
	@GetMapping("/{login:.*}/info")
	public UserProfileDto findUserByID(@PathVariable String login) {
		return userAccountService.findUserByID(login);
	}
	
	@GetMapping("/check")
	public ResponseEntity<String> checkXToken(@RequestHeader("X-token") String xToken){
		return userAccountService.checkXToken(xToken);
	}
	@PostMapping("/favorite/{id}")
	public Set<String> addFavorite(String postId, Principal principal){
		return userAccountService.addFavorite(postId, principal.getName());
	}
	@DeleteMapping("/favorite/{id}")
	public Set<String> removeFavorite(String postId, Principal principal){
		return userAccountService.removeFavorite(postId, principal.getName());
	}
	@GetMapping("/favorites")
	public Set<String> getFavorites(Principal principal){
		return userAccountService.getFavorites(principal.getName());
	}

}
