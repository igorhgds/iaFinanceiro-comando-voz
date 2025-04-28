package igor.henrique.iaFinanceiro.service;

import igor.henrique.iaFinanceiro.repository.TransacaoRepository;
import igor.henrique.iaFinanceiro.enums.TipoTransacao;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class TransacaoQueryService {

    private final TransacaoRepository repository;

    public TransacaoQueryService(TransacaoRepository repository) {
        this.repository = repository;
    }

    public String faturamentoPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        Double valor = repository.somarValorPorPeriodo(dataInicio, dataFim);
        if (valor == null || valor == 0.0) {
            return "Não houve faturamento no período informado.";
        }
        return String.format("O faturamento de %s até %s foi de R$ %.2f.", dataInicio, dataFim, valor);
    }

    public String consultarPorEmpresa(String empresa) {
        List<?> transacoes = repository.findByFilialIgnoreCase(empresa);
        if (transacoes.isEmpty()) {
            return "Nenhuma transação encontrada para a empresa " + empresa + ".";
        }
        return "Encontradas " + transacoes.size() + " transações para a empresa " + empresa + ".";
    }

    public String filialComMaisTransacoes() {
        List<Object[]> resultados = repository.encontrarFilialMaisTransacoes();
        if (resultados.isEmpty()) {
            return "Nenhuma filial encontrada.";
        }
        Object[] maior = resultados.get(0);
        String nomeFilial = (String) maior[0];
        Long quantidade = (Long) maior[1];
        return String.format("A filial com mais transações foi %s com %d transações.", nomeFilial, quantidade);
    }

    public String consultarTipoMes(String tipoString, Integer mes) {
        try {
            TipoTransacao tipo = TipoTransacao.valueOf(tipoString.toLowerCase());
            Double valor = repository.somarPorTipoEMes(tipo, mes);
            if (valor == null || valor == 0.0) {
                return String.format("Não houve %s registrada no mês %d.", tipoString, mes);
            }
            return String.format("O valor de %s no mês %d foi de R$ %.2f.", tipoString, mes, valor);
        } catch (IllegalArgumentException e) {
            return "Tipo de transação inválido.";
        }
    }

    public String resumoFinanceiroPorFilial(String filial) {
        List<Object[]> resultados = repository.somarValoresPorTipoEFilial(filial);

        if (resultados.isEmpty()) {
            return "Nenhum dado financeiro encontrado para a filial " + filial + ".";
        }

        double totalEntrada = 0.0;
        double totalDespesa = 0.0;
        double totalLucro = 0.0;

        for (Object[] linha : resultados) {
            String tipoString = linha[0].toString().toLowerCase();
            Double valor = (Double) linha[1];

            switch (tipoString) {
                case "entrada":
                    totalEntrada = valor;
                    break;
                case "despesa":
                    totalDespesa = valor;
                    break;
                case "lucro":
                    totalLucro = valor;
                    break;
                default:
                    break;
            }
        }

        return String.format(
                "Resumo financeiro da filial %s:\nEntrada: R$ %.2f\nDespesa: R$ %.2f\nLucro: R$ %.2f",
                filial, totalEntrada, totalDespesa, totalLucro
        );
    }

}
