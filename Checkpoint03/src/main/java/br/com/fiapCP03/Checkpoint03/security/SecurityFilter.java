package br.com.fiapCP03.Checkpoint03.security;

import br.com.fiapCP03.Checkpoint03.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Autentica a requisicao a partir do token JWT enviado no header Authorization.
 * Nao e registrado como bean para nao ser adicionado automaticamente na cadeia de filtros do servlet.
 */
public class SecurityFilter extends OncePerRequestFilter {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public SecurityFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(PREFIXO_BEARER)) {
            tokenService.extrairLogin(header.substring(PREFIXO_BEARER.length()))
                    .flatMap(usuarioRepository::findByLogin)
                    .ifPresent(usuario -> SecurityContextHolder.getContext().setAuthentication(
                            UsernamePasswordAuthenticationToken.authenticated(usuario, null, usuario.getAuthorities())
                    ));
        }

        filterChain.doFilter(request, response);
    }
}
