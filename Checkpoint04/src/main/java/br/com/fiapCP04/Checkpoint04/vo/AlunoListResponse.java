package br.com.fiapCP04.Checkpoint04.vo;

public record AlunoListResponse(
        Long id,
        String nome,
        String email,
        String cpf
) {
}

