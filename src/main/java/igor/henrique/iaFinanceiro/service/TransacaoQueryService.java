package igor.henrique.iaFinanceiro.service;

import igor.henrique.iaFinanceiro.dtos.transacao.ResumoFinanceiroDTO;
import igor.henrique.iaFinanceiro.entities.Transacao;
import igor.henrique.iaFinanceiro.enums.TipoTransacao;
import igor.henrique.iaFinanceiro.repository.TransacaoRepository;
import igor.henrique.iaFinanceiro.util.DataFormatter;
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

        String mesInicioNome = DataFormatter.nomeMes(dataInicio);
        String mesFimNome = DataFormatter.nomeMes(dataFim);


        if (dataInicio.getMonth() == dataFim.getMonth()) {
            return String.format(" %s em %s foi de R$ %.2f.",
                    tipo.name().toLowerCase(), mesInicioNome, valor);
        } else {
            return String.format(" %s de %s até %s foi de R$ %.2f.",
                    tipo.name().toLowerCase(), mesInicioNome, mesFimNome, valor);
        }
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

    public String buscarSomatorioPorTipoDataEFilial(TipoTransacao tipo, LocalDate dataInicio, LocalDate dataFim, String filial) {
        Double valor = repository.somarValorPorTipoDataEFilial(tipo, dataInicio, dataFim, filial);

        if (valor == null || valor == 0.0) {
            return String.format("Não houve movimentação do tipo %s para a %s no período informado.", tipo.name().toLowerCase(), filial);
        }

        String mesInicioNome = DataFormatter.nomeMes(dataInicio);
        String mesFimNome = DataFormatter.nomeMes(dataFim);

        if (dataInicio.getMonth() == dataFim.getMonth()) {
            return String.format("O valor de %s para a %s em %s foi de R$ %.2f.",
                    tipo.name().toLowerCase(), filial, mesInicioNome, valor);
        } else {
            return String.format("O valor de %s para a %s de %s até %s foi de R$ %.2f.",
                    tipo.name().toLowerCase(), filial, mesInicioNome, mesFimNome, valor);
        }
    }

    public String listarTransacoesPorFilialEPeriodo(String filial, LocalDate inicio, LocalDate fim) {
        List<Transacao> transacoes = repository.findByFilialAndDataBetween(filial, inicio, fim);
        if (transacoes.isEmpty()) return "Nenhuma transação encontrada para a " + filial + ".";

        StringBuilder sb = new StringBuilder("Transações da " + filial + ":\n");
        for (Transacao t : transacoes) {
            sb.append("- ").append(t.getTipo()).append(" de R$ ")
                    .append(String.format("%.2f", t.getValor())).append(" em ")
                    .append(DataFormatter.formatarData(t.getData())).append("\n");
        }
        return sb.toString();
    }

    public String compararFiliaisPorTipo(TipoTransacao tipo, LocalDate inicio, LocalDate fim) {
        List<Object[]> comparativo = repository.somarPorTipoAgrupadoPorFilial(tipo, inicio, fim);
        if (comparativo.isEmpty()) return "Nenhuma movimentação encontrada.";

        StringBuilder sb = new StringBuilder("Comparativo entre filiais:\n");
        for (Object[] obj : comparativo) {
            String filial = (String) obj[0];
            Double total = (Double) obj[1];
            sb.append("- ").append(filial).append(": R$ ").append(String.format("%.2f", total)).append("\n");
        }
        return sb.toString();
    }

    public String totalGeralPorTipo(TipoTransacao tipo, LocalDate inicio, LocalDate fim) {
        Double total = repository.somarTotalPorTipoEPeriodo(tipo, inicio, fim);
        if (total == null || total == 0.0) {
            return String.format("Não houve movimentação do tipo %s no período.", tipo.name().toLowerCase());
        }
        return String.format("Total de %s entre %s e %s: R$ %.2f",
                tipo.name().toLowerCase(), DataFormatter.formatarData(inicio),
                DataFormatter.formatarData(fim), total);
    }

    public List<ResumoFinanceiroDTO> buscarResumoFinanceiro(String filial, Integer mesInicio, Integer mesFim, Integer ano) {
        return repository.resumoFinanceiroPorFilialEPeriodo(filial, mesInicio, mesFim, ano);
    }
}

