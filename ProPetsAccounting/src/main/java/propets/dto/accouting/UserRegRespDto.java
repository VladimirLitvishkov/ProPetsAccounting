package propets.dto.accouting;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserRegRespDto {
	String email;
	String name;
	String imageURL;
	Set<String> roles;

}
