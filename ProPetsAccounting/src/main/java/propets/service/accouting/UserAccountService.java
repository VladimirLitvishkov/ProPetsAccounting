package propets.service.accouting;

import java.util.Set;

import org.springframework.http.ResponseEntity;

import propets.dto.accouting.UserBlockDto;
import propets.dto.accouting.UserEditDto;
import propets.dto.accouting.UserProfileDto;
import propets.dto.accouting.UserRegRespDto;
import propets.dto.accouting.UserRegisterDto;

public interface UserAccountService {
	
	ResponseEntity<UserRegRespDto> userRegistration(UserRegisterDto user);
	
	ResponseEntity<UserProfileDto> login(String login);
	
	UserProfileDto editUser(String login, UserEditDto userEditDto);
	
	UserProfileDto removeUser(String login);
	
	UserBlockDto blockAccount(String login, boolean status);
	
	Set<String> addRole(String login, String role);
	
	Set<String> removeRole(String login, String role);
	
	UserProfileDto findUserByID(String login);
	
	ResponseEntity<String> checkXToken(String xToken);
	
	Set<String> addFavorite(String postId, String userId);
	
	Set<String> removeFavorite(String postId, String userId);
	
	Set<String> getFavorites(String userId);

}
