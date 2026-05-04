package br.com.joabe.cepapi.domain.model;

/**
 * Status possíveis de uma consulta de CEP.
 * Pertence ao domínio — sem anotações de framework.
 */
public enum ConsultaStatus {
    SUCCESS,
    NOT_FOUND,
    ERROR
}
