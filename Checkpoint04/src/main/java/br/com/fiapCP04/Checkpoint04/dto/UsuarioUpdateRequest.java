package br.com.fiapCP04.Checkpoint04.dto;

import br.com.fiapCP04.Checkpoint04.domain.Perfil;
import jakarta.validation.constraints.NotNull;

public record UsuarioUpdateRequest(
        @NotNull Perfil perfil
) {
}
