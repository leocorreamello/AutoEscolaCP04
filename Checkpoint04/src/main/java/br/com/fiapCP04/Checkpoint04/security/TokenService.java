package br.com.fiapCP04.Checkpoint04.security;

import br.com.fiapCP04.Checkpoint04.domain.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class TokenService {

    private final Algorithm algoritmo;
    private final String emissor;
    private final Duration expiracao;

    public TokenService(
            @Value("${api.security.token.secret}") String segredo,
            @Value("${api.security.token.issuer}") String emissor,
            @Value("${api.security.token.expiracao-horas}") long expiracaoHoras
    ) {
        this.algoritmo = Algorithm.HMAC256(segredo);
        this.emissor = emissor;
        this.expiracao = Duration.ofHours(expiracaoHoras);
    }

    public String gerarToken(Usuario usuario) {
        Instant agora = Instant.now();
        return JWT.create()
                .withIssuer(emissor)
                .withSubject(usuario.getLogin())
                .withClaim("perfil", usuario.getPerfil().name())
                .withIssuedAt(agora)
                .withExpiresAt(agora.plus(expiracao))
                .sign(algoritmo);
    }

    public Optional<String> extrairLogin(String token) {
        try {
            return Optional.of(JWT.require(algoritmo)
                    .withIssuer(emissor)
                    .build()
                    .verify(token)
                    .getSubject());
        } catch (JWTVerificationException ex) {
            return Optional.empty();
        }
    }
}
