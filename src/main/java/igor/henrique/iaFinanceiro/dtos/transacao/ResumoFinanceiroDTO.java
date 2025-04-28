package igor.henrique.iaFinanceiro.dtos.transacao;

import igor.henrique.iaFinanceiro.enums.TipoTransacao;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResumoFinanceiroDTO {
    private TipoTransacao tipo;
    private Double total;
}
