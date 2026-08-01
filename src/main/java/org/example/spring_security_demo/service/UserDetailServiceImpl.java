package org.example.spring_security_demo.service;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.spring_security_demo.dto.AuthUser;
import org.example.spring_security_demo.dto.CustomUserDetails;
import org.example.spring_security_demo.entity.Role;
import org.example.spring_security_demo.entity.User;
import org.example.spring_security_demo.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByEmailAndIsActive(username, true)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return buildUserDetails(user);
  }

  private UserDetails buildUserDetails(User user) {
    Set<GrantedAuthority> authorities = buildAuthorities(user);

    AuthUser authUser =
        AuthUser.builder()
            .id(user.getId())
            .email(user.getEmail())
            .password(user.getPassword())
            .enabled(user.getIsActive())
            .authorities(authorities)
            .build();

    return new CustomUserDetails(authUser);
  }

  private Set<GrantedAuthority> buildAuthorities(User user) {
    Set<GrantedAuthority> authorities = new HashSet<>();

    if (!CollectionUtils.isEmpty(user.getRoles())) {
      for (Role role : user.getRoles()) {
        // Add role
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

        // Add permissions
        if (role.getPermissions() != null) {
          role.getPermissions()
              .forEach(
                  permission ->
                      authorities.add(
                          new SimpleGrantedAuthority(String.valueOf(permission.getName()))));
        }
      }
    }

    return authorities;
  }
}
