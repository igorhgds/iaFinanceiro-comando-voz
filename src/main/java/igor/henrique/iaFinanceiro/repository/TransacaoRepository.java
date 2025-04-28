package igor.henrique.iaFinanceiro.repository;

import igor.henrique.iaFinanceiro.entities.Transacao;
import igor.henrique.iaFinanceiro.enums.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.data BETWEEN :dataInicio AND :dataFim")
    Double somarValorPorPeriodo(LocalDate dataInicio, LocalDate dataFim);

    List<Transacao> findByFilialIgnoreCase(String filial);

    @Query("SELECT t.filial, COUNT(t) FROM Transacao t GROUP BY t.filial ORDER BY COUNT(t) DESC")
    List<Object[]> encontrarFilialMaisTransacoes();

    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.tipo = :tipo AND FUNCTION('MONTH', t.data) = :mes")
    Double somarPorTipoEMes(TipoTransacao tipo, int mes);

    @Query("""
    SELECT t.tipo, SUM(t.valor) 
    FROM Transacao t 
    WHERE LOWER(t.filial) = LOWER(:filial)
    GROUP BY t.tipo
""")
    List<Object[]> somarValoresPorTipoEFilial(String filial);

}
