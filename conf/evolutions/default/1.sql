# Questao schema

# --- !Ups

CREATE TABLE Questao (
    codigo bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    enunciado varchar(255) NOT NULL,
    descricao text NOT NULL,
);

# --- !Downs

DROP TABLE Questao;
