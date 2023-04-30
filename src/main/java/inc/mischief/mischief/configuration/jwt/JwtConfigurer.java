package inc.mischief.mischief.configuration.jwt;

import lombok.AllArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private JwtUtils jwtUtils;

	@Override
	public void configure(HttpSecurity httpSecurity) {
		httpSecurity.addFilterBefore(
				new JwtTokenFilter(jwtUtils),
				UsernamePasswordAuthenticationFilter.class);
	}
}
