package br.com.fiapCP03.Checkpoint03.vo;

import br.com.fiapCP03.Checkpoint03.domain.Perfil;

public record UsuarioResponse(
        Long id,
        String login,
        Perfil perfil
) {
}
