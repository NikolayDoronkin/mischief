package inc.mischief.mischief.configuration.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

	private final JwtUtils jwtUtils;

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		var token = jwtUtils.resolveToken((HttpServletRequest) servletRequest);
		log.info("TOKEN: {}", token);

		if (jwtUtils.validateToken(token)) {
			Optional.ofNullable(jwtUtils.getAuthentication(token))
					.ifPresent(authentication -> {
						SecurityContextHolder.getContext().setAuthentication(authentication);
						log.info("AUTHENTICATED! {}", authentication);
					});
		}

		chain.doFilter(servletRequest, servletResponse);
	}
}
