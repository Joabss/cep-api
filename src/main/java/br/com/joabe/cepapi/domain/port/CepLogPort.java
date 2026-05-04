package br.com.joabe.cepapi.domain.port;

import br.com.joabe.cepapi.domain.model.CepLog;

public interface CepLogPort {

    void salvar(CepLog log);
}
