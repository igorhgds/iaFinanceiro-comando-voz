package igor.henrique.iaFinanceiro.service;

import igor.henrique.iaFinanceiro.dtos.transacao.ResumoFinanceiroDTO;
import igor.henrique.iaFinanceiro.enums.TipoTransacao;
import igor.henrique.iaFinanceiro.repository.TransacaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransacaoQueryService {

    private final TransacaoRepository repository;

    public TransacaoQueryService(TransacaoRepository repository) {
        this.repository = repository;
    }

    public String faturamentoPorTipoEData(TipoTransacao tipo, LocalDate dataInicio, LocalDate dataFim) {
        Double valor = repository.somarValorPorTipoEDataEntre(tipo, dataInicio, dataFim);
        if (valor == null || valor == 0.0) {
            return String.format("Não houve movimentação do tipo %s no período informado.", tipo.name().toLowerCase());
        }
        return String.format("O faturamento de %s de %s até %s foi de R$ %.2f.",
                tipo.name().toLowerCase(), dataInicio, dataFim, valor);
    }

    public String buscarFilialComMaisTransacoes(TipoTransacao tipo) {
        List<Object[]> resultados = repository.somarTotalPorFilialETipo(tipo);
        if (resultados.isEmpty()) {
            return "Nenhuma filial encontrada.";
        }
        Object[] melhorResultado = resultados.get(0);
        String nomeFilial = (String) melhorResultado[0];
        Double total = (Double) melhorResultado[1];
        return String.format("A filial com maior %s foi %s com R$ %.2f.",
                tipo.name().toLowerCase(), nomeFilial, total);
    }

    public String buscarSomatorioPorTipoEMes(String tipoString, Integer mes) {
        try {
            TipoTransacao tipo = TipoTransacao.valueOf(tipoString.toUpperCase());
            Double valor = repository.somarValorPorTipoEMes(tipo, mes);
            if (valor == null || valor == 0.0) {
                return String.format("Não houve %s registrada no mês %d.", tipoString.toLowerCase(), mes);
            }
            return String.format("O valor de %s no mês %d foi de R$ %.2f.", tipoString.toLowerCase(), mes, valor);
        } catch (IllegalArgumentException e) {
            return "Tipo de transação inválido: " + tipoString;
        }
    }

    public String resumoFinanceiroPorFilialEIntervalo(String filial, Integer mesInicio, Integer mesFim) {
        Double total = repository.somarValorPorFilialEMesEntre(filial, mesInicio, mesFim);
        if (total == null || total == 0.0) {
            return String.format("Nenhum dado financeiro encontrado para a filial %s no intervalo entre os meses %d e %d.", filial, mesInicio, mesFim);
        }
        return String.format("Resumo financeiro da %s entre os meses %d e %d: R$ %.2f.",
                filial, mesInicio, mesFim, total);
    }

    public List<ResumoFinanceiroDTO> buscarResumoFinanceiro(String filial, Integer mesInicio, Integer mesFim, Integer ano) {
        return repository.resumoFinanceiroPorFilialEPeriodo(filial, mesInicio, mesFim, ano);
    }

}
