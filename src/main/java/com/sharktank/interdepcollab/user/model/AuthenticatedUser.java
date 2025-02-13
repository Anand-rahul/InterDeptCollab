package com.sharktank.interdepcollab.user.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class AuthenticatedUser extends User {

    private static final long serialVersionUID = 1L;
    private AppUser user;

    public AuthenticatedUser(AppUser user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getEmail(), user.getPassword(), authorities);
        this.user = user;
    }

    public AuthenticatedUser(AppUser user, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
            boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(user.getEmail(), user.getPassword(), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
                authorities);
        this.user = user;
    }

    public AppUser getUser() {
        return user;
    }
}
