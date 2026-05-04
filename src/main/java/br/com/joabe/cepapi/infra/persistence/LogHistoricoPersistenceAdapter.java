package br.com.joabe.cepapi.infra.persistence;

import br.com.joabe.cepapi.domain.model.CepLog;
import br.com.joabe.cepapi.domain.model.ConsultaStatus;
import br.com.joabe.cepapi.domain.port.LogHistoricoPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Adapter de saída — implementa LogHistoricoPort usando Spring Data JPA.
 * Converte filtros de data (LocalDate) para LocalDateTime antes de consultar.
 */
@Component
public class LogHistoricoPersistenceAdapter implements LogHistoricoPort {

    private final LogHistoricoRepository repository;

    public LogHistoricoPersistenceAdapter(LogHistoricoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CepLog> buscar(String cep, ConsultaStatus status,
                                LocalDate de, LocalDate ate) {
        // Converte LocalDate → LocalDateTime para comparar com consulted_at (TIMESTAMP)
        LocalDateTime inicio = de  != null ? de.atStartOfDay()          : null;
        LocalDateTime fim    = ate != null ? ate.atTime(LocalTime.MAX)   : null;

        return repository
                .buscarComFiltros(cep, status, inicio, fim)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private CepLog toDomain(CepLogEntity e) {
        CepLog log = switch (e.getStatus()) {
            case SUCCESS   -> CepLog.sucesso(
                    e.getCep(), e.getLogradouro(), e.getComplemento(),
                    e.getBairro(), e.getLocalidade(), e.getUf(),
                    e.getIbge());
            case NOT_FOUND -> CepLog.naoEncontrado(e.getCep());
            case ERROR     -> CepLog.erro(e.getCep(), e.getErrorMsg());
        };
        log.setId(e.getId());
        return log;
    }
}
