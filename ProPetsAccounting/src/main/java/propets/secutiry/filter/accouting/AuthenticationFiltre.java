package propets.secutiry.filter.accouting;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import propets.configuration.accouting.AccountConfiguration;
import propets.configuration.accouting.UserAccountCredentials;
import propets.dao.accouting.UserAccountRepository;
import propets.model.accouting.User;
@Service
@Order(10)
public class AuthenticationFiltre implements Filter {
	
	@Autowired
	AccountConfiguration configuration;
	@Autowired
	UserAccountRepository repository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		String method = request.getMethod();
		String auth = request.getHeader("Authorization");
		if (checkPointCut(path, method)) {
			UserAccountCredentials credentials = null;
			try {
				credentials = configuration.tokenDecode(auth);
			} catch (Exception e) {
				response.sendError(401, "Header Authorization is not valid");
				return;
			}
			User user = repository.findById(credentials.getLogin()).orElse(null);
			if (user == null) {
				response.sendError(401, "User not found");
				return;
			}
			if (!BCrypt.checkpw(credentials.getPassword(), user.getPassword())) {
				response.sendError(403, "Password incorrect");
				return;
			}
			chain.doFilter(new WrapperRequest(request, credentials.getLogin()), response);
			return;
		}
		chain.doFilter(request, response);

	}
	
	private boolean checkPointCut(String path, String method) {
		boolean check = path.matches("/\\w*/v1/login") && "Post".equalsIgnoreCase(method);
		return check;
	}
	
	private class WrapperRequest extends HttpServletRequestWrapper {
		String user;

		public WrapperRequest(HttpServletRequest request, String user) {
			super(request);
			this.user = user;
		}
		@Override
		public Principal getUserPrincipal() {
			return new Principal() { // () -> user;
				
				@Override
				public String getName() {
					return user;
				}
			};
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	@Override
//	public void init(FilterConfig config) {
//		
//	}
//	@Override
//	public void destroy() {
//		
//	}

}
