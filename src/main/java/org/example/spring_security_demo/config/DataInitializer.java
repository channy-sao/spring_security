package org.example.spring_security_demo.config;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.spring_security_demo.constant.enums.PermissionEnum;
import org.example.spring_security_demo.entity.Permission;
import org.example.spring_security_demo.entity.Role;
import org.example.spring_security_demo.entity.User;
import org.example.spring_security_demo.repository.PermissionRepository;
import org.example.spring_security_demo.repository.RoleRepository;
import org.example.spring_security_demo.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
@Order(1)
public class DataInitializer implements ApplicationRunner {
  private final PermissionRepository permissionRepository;
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  private static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";
  private static final String ADMIN_ROLE = "ADMIN";
  private static final String USER_ROLE = "USER";

  private void seedPermissions() {

    try {
      for (var permissionEnum : PermissionEnum.values()) {
        permissionRepository
            .findByName(permissionEnum)
            .orElseGet(
                () ->
                    permissionRepository.save(
                        Permission.builder()
                            .name(permissionEnum)
                            .category(permissionEnum.getCategory())
                            .description(permissionEnum.getDescription())
                            .build()));
        log.info("Permission {} has been created", permissionEnum.name());
      }
    } catch (Exception _) {
      log.info("Permissions not created");
    }
  }

  private void seedSuperAdminRole() {
    try {
      // Create a super admin role
      roleRepository
          .findByName(SUPER_ADMIN_ROLE)
          .ifPresentOrElse(
              d -> log.info("Role {} has been created", SUPER_ADMIN_ROLE),
              () ->
                  roleRepository.save(
                      Role.builder()
                          .name(SUPER_ADMIN_ROLE)
                          .permissions(new HashSet<>(permissionRepository.findAll()))
                          .isActive(true)
                          .uid(UUID.randomUUID().toString())
                          .build()));

      // Create an admin role
      roleRepository
          .findByName(ADMIN_ROLE)
          .ifPresentOrElse(
              _ -> log.info("Role admin has been created"),
              () ->
                  roleRepository.save(
                      Role.builder()
                          .name(ADMIN_ROLE)
                          .permissions(new HashSet<>(permissionRepository.findAll()))
                          .isActive(true)
                          .uid(UUID.randomUUID().toString())
                          .build()));

      roleRepository
          .findByName(USER_ROLE)
          .ifPresentOrElse(
              _ -> log.info("Role user has been created"),
              () ->
                  roleRepository.save(
                      Role.builder()
                          .name(USER_ROLE)
                          .permissions(Set.of())
                          .isActive(true)
                          .uid(UUID.randomUUID().toString())
                          .build()));
    } catch (Exception _) {
      log.info("Roles not created");
    }
  }

  private void initSuperAdmin() {

    // Check if super admin already exists
    userRepository
        .findByEmail("admin@gmail.com")
        .ifPresentOrElse(
            _ -> log.info("Admin already exists"),
            () -> {
              Role role =
                  roleRepository
                      .findByName(DataInitializer.SUPER_ADMIN_ROLE)
                      .orElseThrow(() -> new RuntimeException("Super admin role is not found"));
              // Create a super admin user
              User admin =
                  User.builder()
                      .email("admin@gmail.com")
                      .password(passwordEncoder.encode("admin@123"))
                      .avatar("admin-avatar.png")
                      .firstName("Admin")
                      .lastName("Admin")
                      .phone("+855 12356789")
                      .uuid(UUID.randomUUID())
                      .isActive(true)
                      .rememberMe(true)
                      .emailVerifiedAt(LocalDateTime.now())
                      .isEmailVerified(false)
                      .roles(Set.of(role))
                      .build();

              userRepository.save(admin);

              log.info("Super Admin initialized successfully!");
            });

    userRepository
        .findByEmail("normaladmin@gmail.com")
        .ifPresentOrElse(
            _ -> log.info("Normal Admin already exists"),
            () -> {
              Role role = roleRepository.findByName(DataInitializer.ADMIN_ROLE).orElseThrow();
              // Create a normal admin user
              User normalAdmin =
                  User.builder()
                      .email("normaladmin@gmail.com")
                      .password(passwordEncoder.encode("admin@123"))
                      .avatar("normal-admin-avatar.png")
                      .firstName("Normal")
                      .lastName("Admin")
                      .phone("+855 12567892")
                      .uuid(UUID.randomUUID())
                      .isActive(true)
                      .rememberMe(true)
                      .emailVerifiedAt(LocalDateTime.now())
                      .isEmailVerified(false)
                      .roles(Set.of(role))
                      .build();

              userRepository.save(normalAdmin);

              log.info("Normal Admin initialized successfully!");
            });
  }

  @Transactional
  @Override
  public void run(ApplicationArguments args) throws Exception {
    seedPermissions();
    seedSuperAdminRole();
    initSuperAdmin();
  }
}
