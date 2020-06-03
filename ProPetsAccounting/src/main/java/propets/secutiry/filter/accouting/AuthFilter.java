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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import propets.configuration.accouting.AccountConfiguration;
import propets.dao.accouting.UserAccountRepository;
import propets.service.accouting.UserAccountService;

@Service
@Order(10)
public class AuthFilter implements Filter {

	@Autowired
	UserAccountService userAccountService;

	@Autowired
	UserAccountRepository repository;

	@Autowired
	AccountConfiguration configuration;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		String method = request.getMethod();
		if (!checkPointCut(path, method)) {
			String xToken = request.getHeader("X-token");
			if (xToken == null) {
				response.sendError(401);
				return;
			}
//			Jws<Claims> claims = null;
//			try {
//				claims = Jwts.parser().setSigningKey(configuration.getSecretKey().getBytes("UTF-8")).parseClaimsJws(xToken);
//			} catch (Exception e) {
//				response.sendError(401, "You need authorization");
//				return;
//			}
//			String userId = (String) claims.getBody().get("userId");
//			User user = repository.findById(userId).orElse(null);
//			if (user == null) {
//				response.sendError(401);
//				return;
//			}
//			String newXToken = Jwts.builder().addClaims(claims.getBody())
//					.setExpiration(Date.from(ZonedDateTime.now().plusDays(configuration.getExpPeriod()).toInstant()))
//					.signWith(SignatureAlgorithm.HS256, configuration.getSecretKey().getBytes("UTF-8")).compact();
//			response.addHeader("X-token", newXToken);
//			chain.doFilter(new WrapperRequest(request, user.getEmail()), response);
//			return;
//			========================

			ResponseEntity<String> responseCheckToken = userAccountService.checkXToken(xToken);
			if (responseCheckToken.getStatusCode().equals(HttpStatus.CONFLICT)) {
				response.sendError(409, "Something wrong");
				return;
			}
			String userId = findUserInPath(path);
			if (checkForUserId(path, method)
					&& !userId.equalsIgnoreCase(responseCheckToken.getHeaders().getFirst("X-userId"))) {
				response.sendError(409, "!!!");
				return;
			}
			response.addHeader("X-token", responseCheckToken.getHeaders().getFirst("X-token"));
			chain.doFilter(new WrapperRequest(request, responseCheckToken.getHeaders().getFirst("X-userId")), response);
			return;
//			response.addHeader("X-userId", responseCheckToken.getHeaders().getFirst("X-userId"));

//			========================
//			String url = configuration.getRequestToken();
//			RestTemplate restTemplate = new RestTemplate();
//			URI urlCheckTokenServ = null;
//			try {
//				urlCheckTokenServ = new URI(url);
//			} catch (URISyntaxException e) {
//				e.printStackTrace();
//			}
//			HttpHeaders headers = new HttpHeaders();
//			headers.add("X-token", xToken);
//			RequestEntity<String> requestAccServ = new RequestEntity<>(headers, HttpMethod.GET, urlCheckTokenServ);
//			ResponseEntity<String> responseAccServ;
//			try {
//				responseAccServ = restTemplate.exchange(requestAccServ, String.class);
//			} catch (RestClientException e) {
//				response.sendError(409);
//				return;
//			}
//			if (responseAccServ.getStatusCode().equals(HttpStatus.CONFLICT)) {
//				response.sendError(409);
//				return;
//			}
//			String userId = path.substring(path.indexOf("login/") + 6).substring(0,
//					path.substring(path.indexOf("login/") + 6).indexOf("/"));
//			if (userId != responseAccServ.getHeaders().getFirst("X-userId")) {
//				response.sendError(409);
//				return;
//			}
//			response.addHeader("X-token", responseAccServ.getHeaders().getFirst("X-token"));
//			response.addHeader("X-userId", responseAccServ.getHeaders().getFirst("X-userId"));

		}
		chain.doFilter(request, response);

	}

	private boolean checkForUserId(String path, String method) {
		boolean check = ((method.equalsIgnoreCase("Put") || method.equalsIgnoreCase("Delete"))
				&& !(path.matches(".+/v1/\\w+/role/.+") || path.matches(".+/v1/\\w+/block/.+")))
				&& !(method.equalsIgnoreCase("Put") && path.matches(".+/v1/\\w+/activity/.+"));
		return check;
	}

	private String findUserInPath(String path) {
		String userId = path.substring(path.indexOf("v1/") + 3);
		int end = userId.indexOf("/");
		if (end > 0) {
			userId = userId.substring(0, end);
		}
		return userId;
	}

	private boolean checkPointCut(String path, String method) {
		boolean check = ((path.matches(".+/v1/registration") || path.matches(".+/v1/login"))
				&& "Post".equalsIgnoreCase(method))
				|| ("Get".equalsIgnoreCase(method) && (path.endsWith("activities") || path.endsWith("favorites")))
				|| "Options".equalsIgnoreCase(method) || path.matches(".+/v1/check");
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
