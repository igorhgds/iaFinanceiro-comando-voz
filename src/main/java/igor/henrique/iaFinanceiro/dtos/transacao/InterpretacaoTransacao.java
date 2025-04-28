package igor.henrique.iaFinanceiro.dtos.transacao;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class InterpretacaoTransacao {
    private String acao;
    private String tipo;
    private Integer mesInicio;
    private Integer mesFim;
    private String filial;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer ano;

}
