package igor.henrique.iaFinanceiro.service;

import igor.henrique.iaFinanceiro.dtos.transacao.InterpretacaoTransacao;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

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
            case "consultar_transacoes_intervalo":
                return processarConsultaIntervalo(interpretacao);

            case "consultar_transacoes_filial":
                return processarConsultaFilial(interpretacao);

            case "consultar_filial_mais_transacoes":
                return transacaoQueryService.filialComMaisTransacoes();

            case "consultar_somatorio_tipo_mes":
                return processarConsultaTipoMes(interpretacao);

            case "consultar_resumo_financeiro_filial":
                return processarResumoFinanceiroFilial(interpretacao);

            default:
                return "Não entendi a ação solicitada.";
        }
    }

    private String processarConsultaIntervalo(InterpretacaoTransacao interpretacao) {
        if (interpretacao.getMesInicio() == null || interpretacao.getMesFim() == null) {
            return "Informe o mês de início e o mês de fim para a consulta.";
        }
        LocalDate dataInicio = LocalDate.of(Year.now().getValue(), interpretacao.getMesInicio(), 1);
        LocalDate dataFim = LocalDate.of(Year.now().getValue(), interpretacao.getMesFim(), Month.of(interpretacao.getMesFim()).length(Year.now().isLeap()));
        return transacaoQueryService.faturamentoPorPeriodo(dataInicio, dataFim);
    }

    private String processarConsultaFilial(InterpretacaoTransacao interpretacao) {
        if (interpretacao.getEmpresa() == null || interpretacao.getEmpresa().isBlank()) {
            return "Informe o nome da filial para a consulta.";
        }
        return transacaoQueryService.consultarPorEmpresa(interpretacao.getEmpresa());
    }

    private String processarConsultaTipoMes(InterpretacaoTransacao interpretacao) {
        if (interpretacao.getTipo() == null || interpretacao.getMesInicio() == null) {
            return "Informe o tipo de transação e o mês para a consulta.";
        }
        return transacaoQueryService.consultarTipoMes(interpretacao.getTipo(), interpretacao.getMesInicio());
    }

    private String processarResumoFinanceiroFilial(InterpretacaoTransacao interpretacao) {
        if (interpretacao.getEmpresa() == null || interpretacao.getEmpresa().isBlank()) {
            return "Informe o nome da filial para gerar o resumo financeiro.";
        }
        return transacaoQueryService.resumoFinanceiroPorFilial(interpretacao.getEmpresa());
    }
}
