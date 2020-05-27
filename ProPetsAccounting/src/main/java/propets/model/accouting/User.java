package propets.model.accouting;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = { "email" })
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	String email;
	String password;
	@Default
	Boolean block = false;
	String name;
	String phone;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Default
	LocalDateTime regDate = LocalDateTime.now();
	@Singular
	Set<String> roles;
	@Default
	String avatar = "https://www.gravatar.com/avatar/0?d=mp";
	@Default
	HashMap<String, Set<String>> favorites = new HashMap<>();
	@Default
	HashMap<String, Set<String>> activities = new HashMap<>();

	public boolean addRole(String role) {
		return roles.add(role);
	}

	public boolean removeRole(String role) {
		return roles.remove(role);
	}
	
	public boolean addFavorite(String key, String postId) {
		if (favorites.containsKey(key)) {
			return favorites.get(key).add(postId);
		} else {
			Set<String> set = new HashSet<>();
			set.add(postId);
			return favorites.put(key, set) == null;
		}
	}
	
	public boolean removeFavorite(String key, String postId) {
		return favorites.get(key).remove(postId);
	}
	
	public boolean addActivities(String key, String postId) {
		if (activities.containsKey(key)) {
			return activities.get(key).add(postId);
		} else {
			Set<String> set = new HashSet<>();
			set.add(postId);
			return activities.put(key, set) == null;
		}
	}
	
	public boolean removeActivities(String key, String postId) {
		return activities.get(key).remove(postId);
	}


}
