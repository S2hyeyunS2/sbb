package com.mysite.spring.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SiteUser, Long> {
    //uwername으로 조회하는 기존 메서드
    Optional<SiteUser> findByusername(String username);
    //email로 사용자를 조회하는 메서드
    Optional<SiteUser> findByEmail(String email);
}
