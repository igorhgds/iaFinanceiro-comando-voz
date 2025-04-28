package igor.henrique.iaFinanceiro.repository;

import igor.henrique.iaFinanceiro.dtos.transacao.ResumoFinanceiroDTO;
import igor.henrique.iaFinanceiro.entities.Transacao;
import igor.henrique.iaFinanceiro.enums.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @Query("""
        SELECT SUM(t.valor)
        FROM Transacao t
        WHERE t.tipo = :tipo
          AND t.data BETWEEN :dataInicio AND :dataFim
    """)
    Double somarValorPorTipoEDataEntre(@Param("tipo") TipoTransacao tipo,
                                       @Param("dataInicio") LocalDate dataInicio,
                                       @Param("dataFim") LocalDate dataFim);

    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.tipo = :tipo AND t.data BETWEEN :dataInicio AND :dataFim AND t.filial = :filial")
    Double somarValorPorTipoDataEFilial(@Param("tipo") TipoTransacao tipo,
                                        @Param("dataInicio") LocalDate dataInicio,
                                        @Param("dataFim") LocalDate dataFim,
                                        @Param("filial") String filial);

    @Query("""
        SELECT t.filial, SUM(t.valor)
        FROM Transacao t
        WHERE t.tipo = :tipo
        GROUP BY t.filial
        ORDER BY SUM(t.valor) DESC
    """)
    List<Object[]> somarTotalPorFilialETipo(@Param("tipo") TipoTransacao tipo);

    @Query("""
        SELECT new igor.henrique.iaFinanceiro.dtos.transacao.ResumoFinanceiroDTO(t.tipo, SUM(t.valor))
        FROM Transacao t
        WHERE t.filial = :filial
          AND EXTRACT(MONTH FROM t.data) BETWEEN :mesInicio AND :mesFim
          AND EXTRACT(YEAR FROM t.data) = :ano
        GROUP BY t.tipo
        ORDER BY t.tipo
    """)
    List<ResumoFinanceiroDTO> resumoFinanceiroPorFilialEPeriodo(@Param("filial") String filial,
                                                                @Param("mesInicio") Integer mesInicio,
                                                                @Param("mesFim") Integer mesFim,
                                                                @Param("ano") Integer ano);
}

