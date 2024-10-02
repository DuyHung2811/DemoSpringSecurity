package com.example.DemoSpringSecurity.repository;

import com.example.DemoSpringSecurity.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name); // Tìm vai trò theo tên
}
