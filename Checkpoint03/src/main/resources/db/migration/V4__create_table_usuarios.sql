CREATE TABLE usuarios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    login VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(20) NOT NULL
);

-- Usuario administrador inicial. Senha: admin123 (armazenada com BCrypt)
INSERT INTO usuarios (login, senha, perfil)
VALUES ('admin', '$2a$10$ilv1jx6OSoihVO4h8dJ14Oe3S/LfMHp2nx.MF.RIniQzflzSXH6/i', 'ADMIN');
