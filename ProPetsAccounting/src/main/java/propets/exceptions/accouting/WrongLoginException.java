package propets.exceptions.accouting;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "User exist")
public class WrongLoginException extends RuntimeException {

	private static final long serialVersionUID = 1L;

}
