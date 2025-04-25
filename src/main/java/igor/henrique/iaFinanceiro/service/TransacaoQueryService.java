package igor.henrique.iaFinanceiro.service;

import org.springframework.stereotype.Service;

@Service
public class TransacaoQueryService {

    private final TransacaoRepository repository;

    public TransacaoQueryService(TransacaoRepository repository) {
        this.repository = repository;
    }

    public String interpretarConsulta(String texto) {
        final String textoLower = texto.toLowerCase();

        TipoTransacao tipo = TextoFinanceiroParser.extrairTipo(textoLower);
        Integer mes = TextoFinanceiroParser.extrairMes(textoLower);

        if (tipo != null && mes != null) {
            Double valor = repository.somarPorTipoEMes(tipo, mes);

            String tipoTexto = TextoFinanceiroParser.chavePorValorTipo(tipo);
            String mesTexto = TextoFinanceiroParser.chavePorValorMes(mes);

            if (valor == null || valor == 0.0) {
                String artigoNegativo = tipoTexto.matches("(?i)(despesa|receita)") ? "uma" : "um";
                return String.format("Não houve %s %s registrado em %s.", artigoNegativo, tipoTexto, mesTexto);
            }

            String artigo = tipoTexto.matches("(?i)(despesa|receita)") ? "A" : "O";
            return String.format("%s %s de %s foi R$ %.2f", artigo, tipoTexto, mesTexto, valor);
        }
        return "Desculpe, não entendi a pergunta.";
    }
}