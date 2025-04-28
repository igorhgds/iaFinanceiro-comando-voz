package igor.henrique.iaFinanceiro.ai;

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import igor.henrique.iaFinanceiro.dtos.transacao.InterpretacaoTransacao;
import org.json.JSONException;
import org.json.JSONObject;

public class ExtratorComInstruct {

    public static InterpretacaoTransacao dadosTransacao(String frase) {
        String apiKey = System.getenv("OPENAI_APIKEY");
        OpenAiService service = new OpenAiService(apiKey);

        String prompt = """
            A partir da frase abaixo, identifique:
            - A ação que o sistema deve executar (chame de "acao").
            - O tipo da transação ("entrada", "despesa", "lucro").
            - O mês de início ("mesInicio", número 1-12).
            - O mês de fim ("mesFim", número 1-12).
            - A empresa envolvida ("filial").
        
            Possíveis valores para "acao":
            - "consultar_transacoes_intervalo"
            - "consultar_transacoes_filial"
            - "consultar_filial_mais_transacoes"
            - "consultar_somatorio_tipo_mes"
            - "consultar_resumo_financeiro_filial"
        
            Responda apenas um JSON, exatamente neste formato:
            {
              "acao": "consultar_transacoes_intervalo",
              "tipo": "entrada",
              "mesInicio": 1,
              "mesFim": 5,
              "filial": "Filial 1"
            }
        
            Se algum valor não for mencionado na frase, use null.
        
            Frase: "%s"
        """.formatted(frase);

        System.out.println("Frase: " + frase);

        CompletionRequest requisicao = CompletionRequest.builder()
                .model("gpt-3.5-turbo-instruct")
                .prompt(prompt)
                .maxTokens(200)
                .temperature(0.0)
                .build();

        var resposta = service.createCompletion(requisicao);
        String textoResposta = resposta.getChoices().get(0).getText().trim();

        System.out.println("Resposta: " + resposta);
        System.out.println("TextoResposta: " + textoResposta);

        try {
            JSONObject jsonResposta = new JSONObject(textoResposta);

            InterpretacaoTransacao resultado = new InterpretacaoTransacao();
            resultado.setAcao(jsonResposta.optString("acao", null));
            resultado.setTipo(jsonResposta.optString("tipo", null));
            resultado.setMesInicio(jsonResposta.has("mesInicio") && !jsonResposta.isNull("mesInicio") ? jsonResposta.getInt("mesInicio") : null);
            resultado.setMesFim(jsonResposta.has("mesFim") && !jsonResposta.isNull("mesFim") ? jsonResposta.getInt("mesFim") : null);
            resultado.setEmpresa(jsonResposta.optString("filial", null)); // corrigido aqui

            return resultado;

        } catch (JSONException e) {
            System.err.println("Erro ao processar JSON: " + e.getMessage());
            return null;
        }
    }
}






