package igor.henrique.iaFinanceiro.ai;


import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import igor.henrique.iaFinanceiro.enums.TipoTransacao;
import org.json.JSONException;
import org.json.JSONObject;

public class ExtratorComInstruct {
    public static String dadosTransacao(String frase) {
        String apiKey = System.getenv("OPENAI_APIKEY");
        OpenAiService service = new OpenAiService(apiKey);

        String prompt = """
                A partir da frase abaixo, identifique o tipo de transação financeira e o mês.
                Os tipos podem ser: entrada, despesa ou lucro.
                Retorne apenas um JSON no seguinte formato (sem explicações):
                { "tipo": "entrada", "mes": 7 }
                
                Frase: "%s"
                """.formatted(frase);

        CompletionRequest requisicao = CompletionRequest.builder()
                .model("gpt-3.5-turbo-instruct")
                .prompt(prompt)
                .maxTokens(100)
                .temperature(0.0)
                .build();

        var resposta = service.createCompletion(requisicao);
        String textoResposta = resposta.getChoices().get(0).getText().trim();

        try {
            // Assumindo que o textoResposta seja um JSON válido
            JSONObject jsonResposta = new JSONObject(textoResposta);

            // Extraindo o valor de "tipo" diretamente, sem incluir o JSON completo
            String tipoString = jsonResposta.getString("tipo").trim().toLowerCase();
            System.out.println("Valor extraído do JSON: " + tipoString);

            // Passando o valor de "tipo" para o enum, sem o JSON completo
            TipoTransacao tipo = TipoTransacao.fromString(tipoString);

            // Extraindo o valor de "mes"
            int mes = jsonResposta.getInt("mes");

            // Criando o resultado final como um JSON
            JSONObject resultadoFinal = new JSONObject();
            resultadoFinal.put("tipo", tipo.toString().toLowerCase());
            resultadoFinal.put("mes", mes);

            // Retornando o JSON como string
            return resultadoFinal.toString();
        } catch (JSONException e) {
            System.err.println("Erro ao processar JSON: " + e.getMessage());
            return "{}";
        }


    }
}





