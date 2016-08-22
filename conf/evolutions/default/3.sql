# Resposta schema

# --- !Ups

CREATE TABLE Resposta (
    codigo bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    compilador varchar(50) NOT NULL,
    estado varchar(2) NOT NULL,
    nomeAluno varchar(255) NOT NULL,
    dataEnvio TIMESTAMP NOT NULL,
    dataCompilado TIMESTAMP,
    arquivo varchar(255) NOT NULL,
    questao_codigo bigint(20) NOT NULL REFERENCES Questao(codigo) ON DELETE CASCADE
);

# --- !Downs

DROP TABLE Resposta;
