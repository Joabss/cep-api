package br.com.joabe.cepapi.domain.exception;

public class CepGatewayException extends RuntimeException {
    public CepGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
