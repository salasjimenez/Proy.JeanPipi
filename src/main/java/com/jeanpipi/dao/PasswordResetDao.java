package com.jeanpipi.dao;

// Acceso a tokens de recuperacion.
import com.jeanpipi.config.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;

public class PasswordResetDao {
    public void create(long userId, String tokenHash, OffsetDateTime expiresAt) throws SQLException {
        try (Connection c = Database.getConnection()) {
            try (PreparedStatement clean = c.prepareStatement("DELETE FROM password_reset_tokens WHERE user_id=? OR expires_at<NOW() OR used_at IS NOT NULL")) {
                clean.setLong(1, userId);
                clean.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO password_reset_tokens(user_id,token_hash,expires_at) VALUES(?,?,?)")) {
                ps.setLong(1, userId);
                ps.setString(2, tokenHash);
                ps.setObject(3, expiresAt);
                ps.executeUpdate();
            }
        }
    }

    public Optional<Long> validUserId(String tokenHash) throws SQLException {
        String sql = "SELECT user_id FROM password_reset_tokens WHERE token_hash=? AND used_at IS NULL AND expires_at>NOW()";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tokenHash);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(rs.getLong(1)) : Optional.empty();
            }
        }
    }

    public void markUsed(String tokenHash) throws SQLException {
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE password_reset_tokens SET used_at=NOW() WHERE token_hash=?")) {
            ps.setString(1, tokenHash);
            ps.executeUpdate();
        }
    }
}
