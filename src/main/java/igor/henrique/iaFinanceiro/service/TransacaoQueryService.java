package igor.henrique.iaFinanceiro.service;

import igor.henrique.iaFinanceiro.ai.ExtratorComInstruct;
import igor.henrique.iaFinanceiro.ai.ResultadoTransacao;
import igor.henrique.iaFinanceiro.enums.TipoTransacao;
import igor.henrique.iaFinanceiro.repository.TransacaoRepository;
import igor.henrique.iaFinanceiro.util.TextoFinanceiroParser;
import org.springframework.stereotype.Service;

import org.json.JSONObject;

@Service
public class TransacaoQueryService {

    private TransacaoRepository repository;

    public TransacaoQueryService(TransacaoRepository repository) {
        this.repository = repository;
    }

    public String interpretarConsulta(String texto) {
        final String textoLower = texto.toLowerCase();

        // Usando o ChatGPT para extrair os dados (retorna uma string JSON)
        String resultado = ExtratorComInstruct.dadosTransacao(textoLower);

        if (resultado != null && !resultado.isEmpty()) {
            // Convertendo a string JSON para um objeto JSONObject
            JSONObject resultadoJson = new JSONObject(resultado);

            // Extraindo o tipo e o mês do JSON
            String tipoString = resultadoJson.getString("tipo");
            Integer mes = resultadoJson.getInt("mes");

            TipoTransacao tipo = TipoTransacao.fromString(resultado);

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
        }

        return "Desculpe, não entendi a pergunta.";
    }
}

