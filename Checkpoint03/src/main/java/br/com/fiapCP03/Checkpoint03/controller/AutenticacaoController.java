package br.com.fiapCP03.Checkpoint03.controller;

import br.com.fiapCP03.Checkpoint03.domain.Usuario;
import br.com.fiapCP03.Checkpoint03.dto.LoginRequest;
import br.com.fiapCP03.Checkpoint03.security.TokenService;
import br.com.fiapCP03.Checkpoint03.vo.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AutenticacaoController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication autenticacao = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.login(), request.senha())
        );

        String token = tokenService.gerarToken((Usuario) autenticacao.getPrincipal());
        return new TokenResponse(token, "Bearer");
    }
}
