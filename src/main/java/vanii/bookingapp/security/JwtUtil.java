package vanii.bookingapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    @Value("${jwt.expiration:30000}")
    private Long expiration;
    private SecretKey secret;

    public JwtUtil(
            @Value("${jwt.secret.string:ilovedrinkingteateateateateateanotnotnotcoffeecoffee}")
                   String secretString) {
        secret = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secret)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Date expiration = getClaimsFromToken(token, Claims::getExpiration);
            return expiration.after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Expired or invalid JWT token");
        }
    }

    public String getUsername(String token) {
        return getClaimsFromToken(token, Claims::getSubject);
    }

    private <T> T getClaimsFromToken(
            String token,
            Function<Claims, T> claimsResolver
    ) {
        final Claims claims = Jwts.parser().verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }
}
