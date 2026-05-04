package br.com.joabe.cepapi.dto;

import br.com.joabe.cepapi.domain.model.CepLog;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * DTO resumido de resposta do histórico.
 * Expõe apenas: cep, status e horário da consulta.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LogResumoResponse(
        Long id,
        String cep,
        String status,
        LocalDateTime consultedAt
) {
    public static LogResumoResponse from(CepLog log) {
        return new LogResumoResponse(
                log.getId(),
                log.getCep(),
                log.getStatus().name(),
                log.getConsultedAt()
        );
    }
}
