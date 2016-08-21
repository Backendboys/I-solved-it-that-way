# Teste schema

# --- !Ups

CREATE TABLE Teste (
    codigo bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    arquivo varchar(255) NOT NULL,
    estado varchar(2) NOT NULL,
    questao_codigo bigint(20) NOT NULL references Questao(codigo)
);

# --- !Downs

DROP TABLE Teste;
