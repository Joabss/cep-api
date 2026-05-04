package br.com.joabe.cepapi.controller;

import br.com.joabe.cepapi.domain.exception.CepGatewayException;
import br.com.joabe.cepapi.domain.exception.CepNotFoundException;
import br.com.joabe.cepapi.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handler global de exceções para a API de CEP.
 * <p>
 * Captura exceções específicas como CepNotFoundException e ConstraintViolationException,
 * além de uma captura genérica para outras exceções não tratadas.
 * Retorna respostas JSON padronizadas com status HTTP apropriados.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CepNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(CepNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(404, "Not Found", ex.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .map(v -> v.getMessage())
                .findFirst()
                .orElse("Parâmetro inválido");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, "Bad Request", msg));
    }

    @ExceptionHandler(CepGatewayException.class)
    public ResponseEntity<ErrorResponse> handleGateway(CepGatewayException ex) {
        log.error("Falha no gateway de CEP", ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.of(503, "Service Unavailable", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Erro inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(500, "Internal Server Error", "Erro interno."));
    }
}
