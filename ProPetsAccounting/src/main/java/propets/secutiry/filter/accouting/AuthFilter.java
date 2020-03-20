package propets.secutiry.filter.accouting;

import java.io.IOException;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import propets.configuration.accouting.AccountConfiguration;
import propets.dao.accouting.UserAccountRepository;
import propets.model.accouting.User;

@Service
@Order(15)
public class AuthFilter implements Filter {
	
	@Autowired
	UserAccountRepository repository;
	
	@Autowired
	AccountConfiguration configuration;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("Account: "+configuration.getSecretKey());
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		String method = request.getMethod();
		String xToken = request.getHeader("X-token");
		if (!checkPointCut(path, method)) {
			if (xToken == null) {
				response.sendError(401);
				return;
			}
			Jws<Claims> claims = null;
			try {
				claims = Jwts.parser().setSigningKey(configuration.getSecretKey().getBytes("UTF-8")).parseClaimsJws(xToken);
				if (claims == null) {
					response.sendError(401);
					return;
				}
			} catch (Exception e) {
				response.sendError(401);
			}
			if (claims.getBody().getExpiration().before(new Date(System.currentTimeMillis()))) {
				response.sendError(401);
				return;
			}
			String userId = (String) claims.getBody().get("userId");
			User user = repository.findById(userId).orElse(null);
			if (user == null) {
				response.sendError(401);
				return;
			}
			String newXToken = Jwts.builder().addClaims(claims.getBody())
					.setExpiration(Date.from(ZonedDateTime.now().plusDays(configuration.getExpPeriod()).toInstant()))
					.signWith(SignatureAlgorithm.HS256, configuration.getSecretKey().getBytes("UTF-8")).compact();
			response.addHeader("X-token", newXToken);
			chain.doFilter(new WrapperRequest(request, user.getEmail()), response);
			return;
			
		}
		chain.doFilter(request, response);

	}

	private boolean checkPointCut(String path, String method) {
		boolean check = ((path.matches("/\\w*/v1") || path.matches("/\\w*/v1/login")) 
				&& "Post".equalsIgnoreCase(method)) 
				|| path.startsWith("/h2") || path.matches("/\\w*/v1/check");
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

}
