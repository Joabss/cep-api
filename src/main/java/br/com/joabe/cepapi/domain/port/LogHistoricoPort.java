package br.com.joabe.cepapi.domain.port;

import br.com.joabe.cepapi.domain.model.CepLog;
import br.com.joabe.cepapi.domain.model.ConsultaStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface para consulta de histórico de logs.
 * <p>
 * DIP: o Service depende desta abstração, não de implementação concreta.
 * ISP: interface enxuta com apenas o método necessário.
 * OCP: basta criar outra implementação (ex: LogHistoricoRepository) sem alterar o Service.
 */
public interface LogHistoricoPort {

    /**
     * Busca logs com filtros opcionais. Todos os parâmetros são nullable.
     *
     * @param cep    filtra por CEP exato (sem máscara)
     * @param status filtra por status (SUCCESS, NOT_FOUND, ERROR)
     * @param de     data inicial (inclusive)
     * @param ate    data final (inclusive)
     * @return lista ordenada por consulted_at DESC
     */
    List<CepLog> buscar(String cep, ConsultaStatus status, LocalDate de, LocalDate ate);
}