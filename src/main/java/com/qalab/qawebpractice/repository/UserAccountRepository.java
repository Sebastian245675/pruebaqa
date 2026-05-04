package com.qalab.qawebpractice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qalab.qawebpractice.model.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

	Optional<UserAccount> findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCase(String email);
}
