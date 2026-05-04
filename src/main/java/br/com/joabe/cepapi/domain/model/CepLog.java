package br.com.joabe.cepapi.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Objeto de domínio que representa o log de uma consulta de CEP.
 * <p>
 * POJO puro — sem @Entity, sem anotações do Spring ou JPA.
 * O domínio não sabe como o log é persistido.
 */
public class CepLog {

    @Setter
    @Getter
    private Long id;
    @Getter
    private String cep;
    @Getter
    private String logradouro;
    @Getter
    private String complemento;
    @Getter
    private String bairro;
    @Getter
    private String localidade;
    @Getter
    private String uf;
    @Getter
    private String ibge;
    private String ddd;
    @Getter
    private ConsultaStatus status;
    @Getter
    private String errorMsg;
    @Getter
    private LocalDateTime consultedAt;

    private CepLog() {}

    // ── Factory methods ──────────────────────────────────────────────────────

    public static CepLog sucesso(String cep, String logradouro, String complemento,
                                  String bairro, String localidade, String uf,
                                  String ibge) {
        CepLog log = new CepLog();
        log.cep         = cep;
        log.logradouro  = logradouro;
        log.complemento = complemento;
        log.bairro      = bairro;
        log.localidade  = localidade;
        log.uf          = uf;
        log.ibge        = ibge;
        log.status      = ConsultaStatus.SUCCESS;
        log.consultedAt = LocalDateTime.now();
        return log;
    }

    public static CepLog naoEncontrado(String cep) {
        CepLog log = new CepLog();
        log.cep         = cep;
        log.status      = ConsultaStatus.NOT_FOUND;
        log.consultedAt = LocalDateTime.now();
        return log;
    }

    public static CepLog erro(String cep, String errorMsg) {
        CepLog log = new CepLog();
        log.cep         = cep;
        log.status      = ConsultaStatus.ERROR;
        log.errorMsg    = errorMsg;
        log.consultedAt = LocalDateTime.now();
        return log;
    }
}
