package inc.mischief.mischief.configuration.jwt;

import inc.mischief.mischief.domain.enumeration.UserRole;
import inc.mischief.mischief.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	@Value("${jwt.token_secret}")
	private String secret;

	@Value("${jwt.token_expired}")
	private Long validityInMilliseconds;

	private final UserRepository userRepository;

	private static final String ROLES = "roles";

	@PostConstruct
	protected void init() {
		secret = Base64.getEncoder().encodeToString(secret.getBytes());
	}

	@Bean
	public static BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public String createToken(String userName, List<UserRole> roles) {
		var claims = Jwts.claims().setSubject(userName);
		claims.put(ROLES, getListOfRolesName(roles));

		var now = new Date();
		var validity = new Date(now.getTime() + validityInMilliseconds);

		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(validity)
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}

	private List<String> getListOfRolesName(List<UserRole> roles) {
		return roles.stream().map(Enum::name).collect(Collectors.toList());
	}

	@Bean
	public UserDetailsService userDetailsServiceBean() {
		return userName -> {
			var expectedUser = Optional.ofNullable(userRepository.findByFirstName(userName))
					.orElseThrow(() -> new UsernameNotFoundException("Педик не найден"));

			return JwtUser.builder()
					.user(expectedUser)
					.enable(true)
					.authorities(List.of(new SimpleGrantedAuthority(expectedUser.getUserRole().name())))
					.build();
		};
	}
}
