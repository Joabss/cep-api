package br.com.joabe.cepapi.dto;

import java.time.LocalDateTime;

/**
 * DTO para resposta de erros.
 * Contém informações como status HTTP, mensagem de erro e timestamp.
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, LocalDateTime.now());
    }
}
