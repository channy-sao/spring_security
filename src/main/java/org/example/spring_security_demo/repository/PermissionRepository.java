package org.example.spring_security_demo.repository;

import org.example.spring_security_demo.constant.enums.PermissionEnum;
import org.example.spring_security_demo.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(PermissionEnum permissionEnum);
}
