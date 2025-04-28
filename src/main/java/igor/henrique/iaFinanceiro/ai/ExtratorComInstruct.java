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
        
            Regras para definir "acao":
            - Se a frase mencionar "filial" e "tipo de transação" (entrada, despesa, lucro), responda "consultar_somatorio_transacao_por_filial_tipo_e_intervalo".
            - Se a frase mencionar apenas "tipo de transação", sem filial, responda "consultar_somatorio_transacao_por_tipo_e_intervalo".
            - Se a frase perguntar qual filial teve maior movimentação, responda "consultar_filial_maior_transacao_somatorio_tipo_e_intervalo".
            - Se pedir um resumo de movimentações financeiras por filial, responda "consultar_resumo_financeiro_filial_tipos_e_intervalo".
            - Se não tiver informações suficientes, responda null.
        
            Regras para determinar o "tipo" de transação:
            - Se a frase mencionar "entrada", o tipo é "entrada".
            - Se a frase mencionar "despesa", o tipo é "despesa".
            - Se a frase mencionar "lucro", o tipo é "lucro".
            - Se a frase mencionar "faturamento", **o tipo deve ser considerado como "entrada"**.
        
            Responda apenas com um JSON, neste formato:
            {
              "acao": "consultar_somatorio_transacao_por_filial_tipo_e_intervalo",
              "tipo": "entrada",
              "mesInicio": 1,
              "mesFim": 5,
              "filial": "Filial 1"
            }
        
            Se algum valor não for mencionado, use null.
        
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

        System.out.println("TextoResposta: " + textoResposta);

        try {
            JSONObject jsonResposta = new JSONObject(textoResposta);

            InterpretacaoTransacao resultado = new InterpretacaoTransacao();
            resultado.setAcao(jsonResposta.optString("acao", null));
            resultado.setTipo(jsonResposta.optString("tipo", null));
            resultado.setMesInicio(jsonResposta.has("mesInicio") && !jsonResposta.isNull("mesInicio") ? jsonResposta.getInt("mesInicio") : null);
            resultado.setMesFim(jsonResposta.has("mesFim") && !jsonResposta.isNull("mesFim") ? jsonResposta.getInt("mesFim") : null);
            resultado.setFilial(jsonResposta.optString("filial", null)); // corrigido aqui

            return resultado;

        } catch (JSONException e) {
            System.err.println("Erro ao processar JSON: " + e.getMessage());
            return null;
        }
    }
}






