package br.com.fiapCP03.Checkpoint03.vo;

public record AlunoListResponse(
        Long id,
        String nome,
        String email,
        String cpf
) {
}

