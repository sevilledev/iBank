package dev.seville.ibank.auth.repository;

import dev.seville.ibank.auth.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Boolean existsByTokenAndExpiredFalseAndRevokedFalse(String token);

    @Modifying
    @Transactional
    @Query("""
        UPDATE RefreshToken rt
        SET rt.expired = true, rt.revoked = true
        WHERE rt.user.id = :userId AND rt.expired = false AND rt.revoked = false
    """)
    void revokeAllToken(Long userId);
}
