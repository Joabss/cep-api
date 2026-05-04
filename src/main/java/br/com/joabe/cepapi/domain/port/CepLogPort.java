package br.com.joabe.cepapi.domain.port;

import br.com.joabe.cepapi.domain.model.CepLog;

/**
 * Interface para persistência de logs de consulta de CEP.
 * <p>
 * DIP: o Service depende desta abstração, não de implementação concreta.
 * ISP: interface enxuta com apenas o método necessário.
 * OCP: basta criar outra implementação (ex: CepLogRepository) sem alterar o Service.
 */
public interface CepLogPort {

    void salvar(CepLog log);
}
