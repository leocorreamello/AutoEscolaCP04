CREATE TABLE instrucoes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    aluno_id BIGINT NOT NULL,
    instrutor_id BIGINT NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    motivo_cancelamento VARCHAR(30),
    CONSTRAINT fk_instrucoes_aluno FOREIGN KEY (aluno_id) REFERENCES alunos (id),
    CONSTRAINT fk_instrucoes_instrutor FOREIGN KEY (instrutor_id) REFERENCES instrutores (id)
);
