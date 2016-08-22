# Teste schema

# --- !Ups

CREATE TABLE Teste (
    codigo bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    arquivo varchar(255) NOT NULL,
    questao_codigo bigint(20) NOT NULL REFERENCES Questao(codigo) ON DELETE CASCADE
);

# --- !Downs

DROP TABLE Teste;
