package igor.henrique.iaFinanceiro.service;

import igor.henrique.iaFinanceiro.dtos.transacao.ResumoFinanceiroDTO;
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

        // Extrair os nomes dos meses
        String mesInicioNome = DataFormatter.nomeMes(dataInicio);
        String mesFimNome = DataFormatter.nomeMes(dataFim);


        if (dataInicio.getMonth() == dataFim.getMonth()) {
            // Se o mês de início e fim forem o mesmo
            return String.format(" %s em %s foi de R$ %.2f.",
                    tipo.name().toLowerCase(), mesInicioNome, valor);
        } else {
            // Se forem meses diferentes
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

        // Extrair os nomes dos meses em português
        String mesInicioNome = DataFormatter.nomeMes(dataInicio);
        String mesFimNome = DataFormatter.nomeMes(dataFim);


        if (dataInicio.getMonth() == dataFim.getMonth()) {
            // Se o mês de início e fim forem o mesmo
            return String.format("O valor de %s para a %s em %s foi de R$ %.2f.",
                    tipo.name().toLowerCase(), filial, mesInicioNome, valor);
        } else {
            // Se forem meses diferentes
            return String.format("O valor de %s para a %s de %s até %s foi de R$ %.2f.",
                    tipo.name().toLowerCase(), filial, mesInicioNome, mesFimNome, valor);
        }
    }



    public List<ResumoFinanceiroDTO> buscarResumoFinanceiro(String filial, Integer mesInicio, Integer mesFim, Integer ano) {
        return repository.resumoFinanceiroPorFilialEPeriodo(filial, mesInicio, mesFim, ano);
    }
}

