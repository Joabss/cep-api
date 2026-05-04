package br.com.joabe.cepapi.domain.service;

import br.com.joabe.cepapi.domain.exception.CepNotFoundException;
import br.com.joabe.cepapi.domain.model.CepData;
import br.com.joabe.cepapi.domain.model.CepLog;
import br.com.joabe.cepapi.domain.port.CepGateway;
import br.com.joabe.cepapi.domain.port.CepLogPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CepService {

    private static final Logger log = LoggerFactory.getLogger(CepService.class);

    private final CepGateway cepGateway;
    private final CepLogPort cepLogPort;

    public CepService(CepGateway cepGateway, CepLogPort cepLogPort) {
        this.cepGateway = cepGateway;
        this.cepLogPort = cepLogPort;
    }

    public CepData consultar(String cep) {
        String cleanCep = cep.replaceAll("[^0-9]", "");

        log.info("Starting lookup for CEP: {}", cleanCep);

        try {
            Optional<CepData> response = cepGateway.buscar(cleanCep);

            if (response.isPresent()) {
                CepData data = response.get();
                CepLog entrada = CepLog.sucesso(
                        cleanCep,
                        data.logradouro(), data.complemento(),
                        data.bairro(), data.localidade(),
                        data.uf(), data.ibge()
                );
                cepLogPort.salvar(entrada);
                return data;
            }

            cepLogPort.salvar(CepLog.naoEncontrado(cleanCep));
            throw new CepNotFoundException(cleanCep);

        } catch (CepNotFoundException e) {
            throw e;
        } catch (Exception e) {
            cepLogPort.salvar(CepLog.erro(cleanCep, e.getMessage()));
            throw e;
        }
    }

}
