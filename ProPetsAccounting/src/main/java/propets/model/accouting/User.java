package propets.model.accouting;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
@Entity
@Table(name = "users")
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
	@ElementCollection
	Set<String> roles;
	@Default
	String imageURL = "https://www.gravatar.com/avatar/0?d=mp";
	@ElementCollection
	@Default
	Set<String> favorites = new HashSet<String>();

	public boolean addRole(String role) {
		return roles.add(role);
	}

	public boolean removeRole(String role) {
		return roles.remove(role);
	}
	
	public boolean addFavorite(String postId) {
		return favorites.add(postId);
	}
	
	public boolean removeFavorite(String postId) {
		return favorites.remove(postId);
	}

}
