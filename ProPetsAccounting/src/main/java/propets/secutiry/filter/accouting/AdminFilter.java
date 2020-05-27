package propets.secutiry.filter.accouting;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import propets.dao.accouting.UserAccountRepository;
import propets.model.accouting.User;

@Service
@Order(20)
public class AdminFilter implements Filter {
	
	@Autowired
	UserAccountRepository repository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		String method = request.getMethod();
		if (checkPointCut(path, method)) {
			String userId = response.getHeader("X-userId");
			User user = repository.findById(userId).get();
			if (user == null) {
				response.sendError(409, "You have problem");
				return;
			}
			if (!user.getRoles().contains("Administrator")) {
				response.sendError(403, "You not admin");
				return;
			}
		}
		chain.doFilter(request, response);

	}

	private boolean checkPointCut(String path, String method) {
		boolean check = (path.matches(".+/v1/\\w+/role/.+") || path.matches(".+/v1/\\w+/block/.+"))
				&& (method.equalsIgnoreCase("Put") || method.equalsIgnoreCase("Delete"));
		return check;
	}

}
