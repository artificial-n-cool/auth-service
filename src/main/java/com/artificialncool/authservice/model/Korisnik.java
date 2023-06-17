package com.artificialncool.authservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Id;
import java.util.Collection;
import java.util.List;

@Document("USERS")
public class Korisnik implements UserDetails {
    @Id
    @Getter @Setter
    private String id;

    @Indexed(unique = true)
    @Setter
    private String username;

    @JsonIgnore
    @Setter
    private String password;

    @Getter @Setter
    private String ime;

    @Getter @Setter
    private String prezime;
    
    @Indexed(unique = true)
    @Getter @Setter
    private String email;

    @Getter @Setter
    private String prebivaliste;

    @Getter @Setter
    private List<Role> roles;

    @Getter @Setter
    private Long lastPasswordResetDate;


    @Setter
    private boolean enabled;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
