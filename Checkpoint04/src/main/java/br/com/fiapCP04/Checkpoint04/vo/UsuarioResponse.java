package br.com.fiapCP04.Checkpoint04.vo;

import br.com.fiapCP04.Checkpoint04.domain.Perfil;

public record UsuarioResponse(
        Long id,
        String login,
        Perfil perfil
) {
}
