package br.com.joabe.cepapi.domain.service;

import br.com.joabe.cepapi.domain.model.CepLog;
import br.com.joabe.cepapi.domain.model.ConsultaStatus;
import br.com.joabe.cepapi.domain.port.LogHistoricoPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Caso de uso: consultar histórico de logs com filtros.
 * <p>
 * SRP: responsabilidade única de orquestrar a consulta de histórico.
 * DIP: depende de LogHistoricoPort, nunca de JPA ou SQL.
 */
@Service
public class LogHistoricoService {

    private static final Logger log = LoggerFactory.getLogger(LogHistoricoService.class);

    private final LogHistoricoPort logHistoricoPort;

    public LogHistoricoService(LogHistoricoPort logHistoricoPort) {
        this.logHistoricoPort = logHistoricoPort;
    }

    /**
     * Retorna histórico de consultas aplicando os filtros informados.
     * Parâmetros nulos são ignorados (sem filtro para aquele campo).
     */
    public List<CepLog> consultar(String cep, ConsultaStatus status,
                                   LocalDate de, LocalDate ate) {
        String cepLimpo = cep != null ? cep.replaceAll("[^0-9]", "") : null;

        log.info("Consultando histórico cep={} status={} de={} ate={}",
                cepLimpo, status, de, ate);

        return logHistoricoPort.buscar(cepLimpo, status, de, ate);
    }
}
