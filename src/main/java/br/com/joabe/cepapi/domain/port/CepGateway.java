package br.com.joabe.cepapi.domain.port;

import br.com.joabe.cepapi.domain.model.CepData;

import java.util.Optional;

/**
 * Interface do cliente HTTP de CEP.
 * <p>
 * SOLID - DIP: o Service depende desta abstração, não de implementação concreta.
 * SOLID - ISP: interface enxuta com apenas o método necessário.
 * SOLID - OCP: basta criar outra implementação (ex: ViaCepClient) sem alterar o Service.
 */
public interface CepGateway {

    Optional<CepData> buscar(String cep);
}
