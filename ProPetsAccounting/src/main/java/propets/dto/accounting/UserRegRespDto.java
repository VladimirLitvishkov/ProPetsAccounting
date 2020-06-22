package propets.dto.accounting;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserRegRespDto {
	String email;
	String name;
	String phone;
	String avatar;
	@Singular
	Set<String> roles;

}
