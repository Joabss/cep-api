package br.com.joabe.cepapi.infra.persistence;

import br.com.joabe.cepapi.domain.model.ConsultaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório Spring Data JPA para consultas de histórico.
 * Visibilidade de pacote — só o adapter acessa diretamente.
 */
interface LogHistoricoRepository extends JpaRepository<CepLogEntity, Long> {

    /**
     * Consulta dinâmica com todos os filtros opcionais.
     * Quando um parâmetro é null, o filtro correspondente é ignorado.
     */
    @Query("""
            SELECT l FROM CepLogEntity l
            WHERE (CASE WHEN :cep IS NULL THEN true ELSE l.cep = :cep END)
            AND (CASE WHEN :status IS NULL THEN true ELSE l.status = :status END)
            AND (CASE WHEN CAST(:de AS timestamp) IS NULL THEN true ELSE l.consultedAt >= :de END)
            AND (CASE WHEN CAST(:ate AS timestamp) IS NULL THEN true ELSE l.consultedAt <= :ate END)
            ORDER BY l.consultedAt DESC
            """)
    List<CepLogEntity> buscarComFiltros(
            @Param("cep")    String cep,
            @Param("status") ConsultaStatus status,
            @Param("de")     LocalDateTime de,
            @Param("ate")    LocalDateTime ate
    );
}
