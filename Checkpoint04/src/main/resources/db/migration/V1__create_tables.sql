CREATE TABLE instrutores (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefone VARCHAR(30) NOT NULL,
    cnh VARCHAR(30) NOT NULL UNIQUE,
    especialidade VARCHAR(20) NOT NULL,
    endereco_logradouro VARCHAR(255) NOT NULL,
    endereco_numero VARCHAR(20),
    endereco_complemento VARCHAR(255),
    endereco_bairro VARCHAR(255) NOT NULL,
    endereco_cidade VARCHAR(255) NOT NULL,
    endereco_uf CHAR(2) NOT NULL,
    endereco_cep VARCHAR(9) NOT NULL,
    ativo BOOLEAN NOT NULL
);

CREATE TABLE alunos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefone VARCHAR(30) NOT NULL,
    cpf VARCHAR(30) NOT NULL UNIQUE,
    endereco_logradouro VARCHAR(255) NOT NULL,
    endereco_numero VARCHAR(20),
    endereco_complemento VARCHAR(255),
    endereco_bairro VARCHAR(255) NOT NULL,
    endereco_cidade VARCHAR(255) NOT NULL,
    endereco_uf CHAR(2) NOT NULL,
    endereco_cep VARCHAR(9) NOT NULL
);

