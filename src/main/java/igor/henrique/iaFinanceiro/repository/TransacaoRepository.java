package igor.henrique.iaFinanceiro.repository;

import igor.henrique.iaFinanceiro.entities.Transacao;
import igor.henrique.iaFinanceiro.enums.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.tipo = :tipo AND FUNCTION('MONTH', t.data) = :mes")
    Double somarPorTipoEMes(@Param("tipo") TipoTransacao tipo, @Param("mes") int mes);

}
