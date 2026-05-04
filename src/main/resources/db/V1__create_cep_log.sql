-- V1__create_cep_log.sql
CREATE TABLE IF NOT EXISTS cep_log (
    id           BIGSERIAL PRIMARY KEY,
    cep          VARCHAR(9)   NOT NULL,
    logradouro   VARCHAR(255),
    complemento  VARCHAR(255),
    bairro       VARCHAR(255),
    localidade   VARCHAR(255),
    uf           VARCHAR(2),
    ibge         VARCHAR(20),
    status       VARCHAR(20)  NOT NULL DEFAULT 'SUCCESS',
    error_msg    VARCHAR(500),
    consulted_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_cep_log_cep          ON cep_log(cep);
CREATE INDEX idx_cep_log_consulted_at ON cep_log(consulted_at DESC);

COMMENT ON TABLE  cep_log              IS 'Log de todas as consultas de CEP realizadas';
COMMENT ON COLUMN cep_log.status       IS 'SUCCESS | NOT_FOUND | ERROR';
COMMENT ON COLUMN cep_log.consulted_at IS 'Horário exato da consulta';
