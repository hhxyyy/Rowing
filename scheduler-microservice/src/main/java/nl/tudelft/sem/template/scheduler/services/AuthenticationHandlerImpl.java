package nl.tudelft.sem.template.scheduler.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class AuthenticationHandlerImpl implements AuthenticationHandler {
    @Override
    public boolean isAdmin() {
        Collection<? extends GrantedAuthority> authorities =
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        return authorities.stream().anyMatch(authority -> authority.toString().equals("ROLE_Admin"));
    }
}
