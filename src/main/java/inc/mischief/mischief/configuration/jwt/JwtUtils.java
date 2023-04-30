package inc.mischief.mischief.configuration.jwt;

import inc.mischief.mischief.configuration.exception.ExceptionMessage;
import inc.mischief.mischief.configuration.property.SecurityConfigurationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtils {

	@Value("${jwt.token_secret}")
	private String secret;

	private static final String EMPTY_TEXT = "";
	private static final String BEARER = "Bearer ";
	private static final String AUTHORIZATION = "Authorization";

	private final UserDetailsService userDetailsService;

	@PostConstruct
	protected void init() {
		secret = Base64.getEncoder().encodeToString(secret.getBytes());
	}

	public UsernamePasswordAuthenticationToken getAuthentication(String token) {
		var userDetails = userDetailsService.loadUserByUsername(getUserName(token));

		return new UsernamePasswordAuthenticationToken(userDetails, EMPTY_TEXT, userDetails.getAuthorities());
	}

	public String getUserName(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
	}

	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION);
		if (bearerToken != null && bearerToken.startsWith(BEARER)) {
			return bearerToken.substring(SecurityConfigurationProperties.SUBSTRING_VALUE);
		}
		return null;
	}

	public boolean validateToken(String token) {
		if (token == null) {
			return false;
		}
		try {
			Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
			return !claimsJws.getBody().getExpiration().before(new Date());
		} catch (JwtException | IllegalArgumentException e) {
			throw new JwtException(ExceptionMessage.JWT_EXPIRED_OR_INVALID);
		}
	}
}
