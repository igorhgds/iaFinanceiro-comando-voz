package igor.henrique.iaFinanceiro.service;

import igor.henrique.iaFinanceiro.dtos.transacao.InterpretacaoTransacao;
import igor.henrique.iaFinanceiro.dtos.transacao.ResumoFinanceiroDTO;
import igor.henrique.iaFinanceiro.enums.TipoTransacao;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;

@Service
public class DispatcherService {

    private final TransacaoQueryService transacaoQueryService;

    public DispatcherService(TransacaoQueryService transacaoQueryService) {
        this.transacaoQueryService = transacaoQueryService;
    }

    public String processarConsulta(InterpretacaoTransacao interpretacao) {
        if (interpretacao == null || interpretacao.getAcao() == null) {
            return "Não entendi sua solicitação.";
        }

        switch (interpretacao.getAcao()) {
            case "consulta_transacoes_por_tipo_intervalo":
                return processarConsultaTransacoesPorTipoEIntervalo(interpretacao);

            case "consulta_transacoes_filial_tipo_mes":
                return processarConsultaTransacoesPorTipoMesEFilial(interpretacao);

            case "consulta_filial_mais_transacoes":
                return processarConsultaFilialComMaisTransacoes(interpretacao);

            case "consulta_somatorio_tipo_mes":
                return processarConsultaSomatorioPorTipoEMes(interpretacao);

            case "consulta_resumo_financeiro_filial_intervalo":
                return processarResumoFinanceiroPorFilialEIntervalo(interpretacao);

            case "resumo_financeiro_filial":
                return processarResumoFinanceiro(interpretacao);

            default:
                return "Ação não reconhecida: " + interpretacao.getAcao();
        }
    }

    private String processarConsultaTransacoesPorTipoEIntervalo(InterpretacaoTransacao interpretacao) {
        if (interpretacao.getTipo() == null) {
            return "Informe o tipo de transação para a consulta.";
        }

        LocalDate dataInicio = obterDataInicio(interpretacao);
        LocalDate dataFim = obterDataFim(interpretacao);

        if (dataInicio == null || dataFim == null) {
            return "Informe as datas de início e fim ou os meses de início e fim.";
        }

        return transacaoQueryService.faturamentoPorTipoEData(
                TipoTransacao.valueOf(interpretacao.getTipo().toUpperCase()),
                dataInicio,
                dataFim
        );
    }

    private String processarConsultaTransacoesPorTipoMesEFilial(InterpretacaoTransacao interpretacao) {
        if (interpretacao.getTipo() == null || interpretacao.getMesInicio() == null || interpretacao.getFilial() == null) {
            return "Informe o tipo de transação, o mês e o nome da filial para a consulta.";
        }
        return transacaoQueryService.buscarSomatorioPorTipoEMes(
                interpretacao.getTipo(),
                interpretacao.getMesInicio()
        );
    }

    private String processarConsultaFilialComMaisTransacoes(InterpretacaoTransacao interpretacao) {
        if (interpretacao.getTipo() == null) {
            return "Informe o tipo de transação para consultar a filial com mais transações.";
        }
        return transacaoQueryService.buscarFilialComMaisTransacoes(
                TipoTransacao.valueOf(interpretacao.getTipo().toUpperCase())
        );
    }

    private String processarConsultaSomatorioPorTipoEMes(InterpretacaoTransacao interpretacao) {
        if (interpretacao.getTipo() == null || interpretacao.getMesInicio() == null) {
            return "Informe o tipo de transação e o mês para consultar o somatório.";
        }
        return transacaoQueryService.buscarSomatorioPorTipoEMes(
                interpretacao.getTipo(),
                interpretacao.getMesInicio()
        );
    }

    private String processarResumoFinanceiroPorFilialEIntervalo(InterpretacaoTransacao interpretacao) {
        if (interpretacao.getFilial() == null) {
            return "Informe o nome da filial para gerar o resumo financeiro.";
        }

        LocalDate dataInicio = obterDataInicio(interpretacao);
        LocalDate dataFim = obterDataFim(interpretacao);

        if (dataInicio == null || dataFim == null) {
            return "Informe as datas de início e fim ou os meses de início e fim.";
        }

        return transacaoQueryService.resumoFinanceiroPorFilialEIntervalo(
                interpretacao.getFilial(),
                dataInicio.getMonthValue(),
                dataFim.getMonthValue()
        );
    }

    /**
     * Utilitário para obter a data de início a partir do InterpretacaoTransacao
     */
    private LocalDate obterDataInicio(InterpretacaoTransacao interpretacao) {
        if (interpretacao.getDataInicio() != null) {
            return interpretacao.getDataInicio();
        } else if (interpretacao.getMesInicio() != null) {
            int anoAtual = Year.now().getValue();
            return LocalDate.of(anoAtual, interpretacao.getMesInicio(), 1);
        }
        return null;
    }

    /**
     * Utilitário para obter a data de fim a partir do InterpretacaoTransacao
     */
    private LocalDate obterDataFim(InterpretacaoTransacao interpretacao) {
        if (interpretacao.getDataFim() != null) {
            return interpretacao.getDataFim();
        } else if (interpretacao.getMesFim() != null) {
            int anoAtual = Year.now().getValue();
            Month mes = Month.of(interpretacao.getMesFim());
            int diaFim = mes.length(Year.isLeap(anoAtual));
            return LocalDate.of(anoAtual, mes, diaFim);
        }
        return null;
    }

    private String processarResumoFinanceiro(InterpretacaoTransacao interpretacao) {
        String filial = interpretacao.getFilial();
        Integer mesInicio = interpretacao.getMesInicio();
        Integer mesFim = interpretacao.getMesFim();
        Integer ano = interpretacao.getAno();

        if (ano == null) {
            ano = Year.now().getValue();
        }

        List<ResumoFinanceiroDTO> resumo = transacaoQueryService.buscarResumoFinanceiro(filial, mesInicio, mesFim, ano);

        if (resumo.isEmpty()) {
            return "Nenhuma transação encontrada para a " + filial + " no período solicitado.";
        }

        StringBuilder resposta = new StringBuilder("Resumo financeiro da " + filial + ":\n");
        for (ResumoFinanceiroDTO dto : resumo) {
            resposta.append("- ").append(dto.getTipo())
                    .append(": R$ ").append(String.format("%.2f", dto.getTotal()))
                    .append("\n");
        }

        return resposta.toString();
    }

}
