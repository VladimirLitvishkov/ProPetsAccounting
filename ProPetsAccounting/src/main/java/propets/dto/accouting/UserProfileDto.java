package propets.dto.accouting;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder

public class UserProfileDto {
	String name;
	String email;
	String phone;
	String imageURL;
	Boolean block;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime regDate;
	@Singular
	Set<String> roles;

}
