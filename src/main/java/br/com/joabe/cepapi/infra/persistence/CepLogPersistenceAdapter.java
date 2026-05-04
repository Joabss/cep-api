package br.com.joabe.cepapi.infra.persistence;

import br.com.joabe.cepapi.domain.model.CepLog;
import br.com.joabe.cepapi.domain.port.CepLogPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adapter de saída — implementa CepLogPort usando Spring Data JPA.
 */

@Component
public class CepLogPersistenceAdapter implements CepLogPort {

    private static final Logger log = LoggerFactory.getLogger(CepLogPersistenceAdapter.class);

    private final CepLogRepository repository;

    public CepLogPersistenceAdapter(CepLogRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void salvar(CepLog domainLog) {
        CepLogEntity entity = toEntity(domainLog);
        CepLogEntity saved  = repository.save(entity);

        log.info("Log persistido [id={}] CEP={} status={} consultedAt={}",
                saved.getId(), saved.getCep(), saved.getStatus(), saved.getConsultedAt());
    }

    private CepLogEntity toEntity(CepLog d) {
        return CepLogEntity.builder()
                .cep(d.getCep())
                .logradouro(d.getLogradouro())
                .complemento(d.getComplemento())
                .bairro(d.getBairro())
                .localidade(d.getLocalidade())
                .uf(d.getUf())
                .ibge(d.getIbge())
                .status(d.getStatus())
                .errorMsg(d.getErrorMsg())
                .consultedAt(d.getConsultedAt())
                .build();
    }
}
