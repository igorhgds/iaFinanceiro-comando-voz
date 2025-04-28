package igor.henrique.iaFinanceiro.dtos.transacao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterpretacaoTransacao {
    private String acao;
    private String tipo;
    private Integer mesInicio;
    private Integer mesFim;
    private String empresa;
}
