package br.com.joabe.cepapi.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório Spring Data JPA para logs de CEP.
 * Visibilidade de pacote — só o adapter acessa diretamente.
 */
interface CepLogRepository extends JpaRepository<CepLogEntity, Long> {}
