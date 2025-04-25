package igor.henrique.iaFinanceiro.util;

import igor.henrique.iaFinanceiro.ai.ResultadoTransacao;
import igor.henrique.iaFinanceiro.enums.TipoTransacao;

import java.util.Map;

public class TextoFinanceiroParser {

    private static final Map<String, TipoTransacao> tipoMap = Map.ofEntries(
            //gatilhos entrada
            Map.entry("entrada", TipoTransacao.entrada),
            Map.entry("faturamento", TipoTransacao.entrada),
            Map.entry("receita", TipoTransacao.entrada),
            Map.entry("ganhos", TipoTransacao.entrada),
            Map.entry("recebimento", TipoTransacao.entrada),
            Map.entry("recebidos", TipoTransacao.entrada),
            Map.entry("valores recebidos", TipoTransacao.entrada),

            //gatilhos despesa
            Map.entry("despesa", TipoTransacao.despesa),
            Map.entry("despesas", TipoTransacao.despesa),
            Map.entry("gasto", TipoTransacao.despesa),
            Map.entry("gastos", TipoTransacao.despesa),
            Map.entry("custo", TipoTransacao.despesa),
            Map.entry("custos", TipoTransacao.despesa),
            Map.entry("pagamentos", TipoTransacao.despesa),
            Map.entry("valores pagos", TipoTransacao.despesa),

            //gatilhos lucro
            Map.entry("lucro", TipoTransacao.lucro),
            Map.entry("lucro líquido", TipoTransacao.lucro),
            Map.entry("resultado final", TipoTransacao.lucro),
            Map.entry("saldo positivo", TipoTransacao.lucro),
            Map.entry("valor restante", TipoTransacao.lucro)
    );

    private static final Map<String, Integer> mesMap = Map.ofEntries(
            Map.entry("janeiro", 1),
            Map.entry("fevereiro", 2),
            Map.entry("março", 3),
            Map.entry("abril", 4),
            Map.entry("maio", 5),
            Map.entry("junho", 6),
            Map.entry("julho", 7),
            Map.entry("agosto", 8),
            Map.entry("setembro", 9),
            Map.entry("outubro", 10),
            Map.entry("novembro", 11),
            Map.entry("dezembro", 12)
    );

    public static TipoTransacao extrairTipo(String texto) {
        return tipoMap.entrySet().stream()
                .filter(e -> texto.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    public static Integer extrairMes(String texto) {
        return mesMap.entrySet().stream()
                .filter(e -> texto.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    public static String chavePorValorTipo(TipoTransacao valor) {
        return tipoMap.entrySet().stream()
                .filter(e -> e.getValue().equals(valor))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("valor");
    }

    public static String chavePorValorMes(Integer valor) {
        return mesMap.entrySet().stream()
                .filter(e -> e.getValue().equals(valor))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("mês");
    }

    public static ResultadoTransacao analisarComMap(String texto) {
        TipoTransacao tipo = extrairTipo(texto);
        Integer mes = extrairMes(texto);

        ResultadoTransacao r = new ResultadoTransacao();
        r.setTipo(tipo != null ? tipo.name() : null);
        r.setMes(mes);
        return r;
    }
}
