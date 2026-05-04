package br.com.joabe.cepapi.domain.model;

/**
 * Dados de um CEP retornados pelo gateway externo.
 * Objeto de domínio — sem dependência de serialização JSON.
 */
public record CepData(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String ibge
) {}
