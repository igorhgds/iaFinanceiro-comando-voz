package igor.henrique.iaFinanceiro.service;

import igor.henrique.iaFinanceiro.ai.ExtratorComInstruct;
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
    private final ExtratorComInstruct extratorComInstruct;

    public DispatcherService(TransacaoQueryService transacaoQueryService, ExtratorComInstruct extratorComInstruct) {
        this.transacaoQueryService = transacaoQueryService;
        this.extratorComInstruct = extratorComInstruct;
    }

    public String processarConsulta(String texto) {
        InterpretacaoTransacao interpretacao = extratorComInstruct.dadosTransacao(texto);

        if (interpretacao == null || interpretacao.getAcao() == null) {
            return "Não entendi sua solicitação.";
        }

        switch (interpretacao.getAcao()) {
            case "consultar_somatorio_transacao_por_tipo_e_intervalo":
                return processarConsultaTransacoesPorTipoEIntervalo(interpretacao);

            case "consultar_somatorio_transacao_por_filial_tipo_e_intervalo":
                return processarConsultaTransacoesPorTipoDataEFilial(interpretacao);

            case "consultar_filial_maior_transacao_somatorio_tipo_e_intervalo":
                return processarConsultaFilialComMaisTransacoes(interpretacao);

            case "consultar_resumo_financeiro_filial_tipos_e_intervalo":
                return processarResumoFinanceiro(interpretacao);

            case "consultar_transacoes_detalhadas_por_filial_e_periodo":
                return processarTransacoesDetalhadasPorFilial(interpretacao);

            case "consultar_comparativo_entre_filiais_por_tipo_e_periodo":
                return processarComparativoFiliais(interpretacao);

            case "consultar_total_geral_por_tipo_e_periodo":
                return processarTotalGeralPorTipo(interpretacao);

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

    private String processarConsultaTransacoesPorTipoDataEFilial(InterpretacaoTransacao interpretacao) {
        if (interpretacao.getTipo() == null || interpretacao.getFilial() == null) {
            return "Informe o tipo de transação e a filial para a consulta.";
        }

        LocalDate dataInicio = obterDataInicio(interpretacao);
        LocalDate dataFim = obterDataFim(interpretacao);

        if (dataInicio == null || dataFim == null) {
            return "Informe a data de início e a data de fim ou os meses de início e fim.";
        }

        TipoTransacao tipoTransacao;
        try {
            tipoTransacao = TipoTransacao.valueOf(interpretacao.getTipo().toUpperCase());
        } catch (IllegalArgumentException e) {
            return "Tipo de transação inválido: " + interpretacao.getTipo();
        }

        return transacaoQueryService.buscarSomatorioPorTipoDataEFilial(
                tipoTransacao,
                dataInicio,
                dataFim,
                interpretacao.getFilial()
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

    private String processarResumoFinanceiro(InterpretacaoTransacao interpretacao) {
        String filial = interpretacao.getFilial();
        Integer mesInicio = interpretacao.getMesInicio();
        Integer mesFim = interpretacao.getMesFim();
        Integer ano = interpretacao.getAno();

        if (filial == null) {
            return "Informe a filial.";
        }
        if (ano == null) {
            ano = Year.now().getValue();
        }

        if (mesInicio == null || mesFim == null) {
            mesInicio = 1;
            mesFim = 12;
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

    private String processarTransacoesDetalhadasPorFilial(InterpretacaoTransacao interpretacao) {
        if (interpretacao.getFilial() == null) return "Informe a filial.";
        LocalDate dataInicio = obterDataInicio(interpretacao);
        LocalDate dataFim = obterDataFim(interpretacao);
        if (dataInicio == null || dataFim == null) return "Informe o período.";

        return transacaoQueryService.listarTransacoesPorFilialEPeriodo(interpretacao.getFilial(), dataInicio, dataFim);
    }

    private String processarComparativoFiliais(InterpretacaoTransacao interpretacao) {
        if (interpretacao.getTipo() == null) return "Informe o tipo de transação.";
        LocalDate dataInicio = obterDataInicio(interpretacao);
        LocalDate dataFim = obterDataFim(interpretacao);
        if (dataInicio == null || dataFim == null) return "Informe o período.";

        return transacaoQueryService.compararFiliaisPorTipo(
                TipoTransacao.valueOf(interpretacao.getTipo().toUpperCase()), dataInicio, dataFim);
    }

    private String processarTotalGeralPorTipo(InterpretacaoTransacao interpretacao) {
        if (interpretacao.getTipo() == null) return "Informe o tipo de transação.";
        LocalDate dataInicio = obterDataInicio(interpretacao);
        LocalDate dataFim = obterDataFim(interpretacao);
        if (dataInicio == null || dataFim == null) return "Informe o período.";

        return transacaoQueryService.totalGeralPorTipo(
                TipoTransacao.valueOf(interpretacao.getTipo().toUpperCase()), dataInicio, dataFim);
    }


    private LocalDate obterDataInicio(InterpretacaoTransacao interpretacao) {
        if (interpretacao.getDataInicio() != null) {
            return interpretacao.getDataInicio();
        } else if (interpretacao.getMesInicio() != null) {
            int anoAtual = Year.now().getValue();
            return LocalDate.of(anoAtual, interpretacao.getMesInicio(), 1);
        }
        return null;
    }

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
}
