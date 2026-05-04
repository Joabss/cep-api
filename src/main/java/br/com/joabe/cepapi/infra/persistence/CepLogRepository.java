package br.com.joabe.cepapi.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface CepLogRepository extends JpaRepository<CepLogEntity, Long> {}
