package igor.henrique.iaFinanceiro.util;


import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;

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
        return resposta.getChoices().get(0).getText().trim();
    }
}




