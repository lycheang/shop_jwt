package com.dreamshops.data;

import com.dreamshops.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUserId(Long userId);
    Optional<RefreshToken> findByUserId(Long userId);
}
