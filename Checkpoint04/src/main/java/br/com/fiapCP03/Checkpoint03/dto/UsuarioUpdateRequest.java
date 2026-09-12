package br.com.fiapCP03.Checkpoint03.dto;

import br.com.fiapCP03.Checkpoint03.domain.Perfil;
import jakarta.validation.constraints.NotNull;

public record UsuarioUpdateRequest(
        @NotNull Perfil perfil
) {
}
