package igor.henrique.iaFinanceiro.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import igor.henrique.iaFinanceiro.dtos.transacao.InterpretacaoTransacao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

@Component
public class ExtratorComInstruct {

    @Value("${azure.openai.endpoint}")
    private String azureEndpoint;

    @Value("${azure.openai.api-key}")
    private String azureApiKey;

    @Value("${azure.openai.deployment-name}")
    private String azureDeploymentName;

    @Value("${azure.openai.api-version}")
    private String azureApiVersion;

    private final ObjectMapper mapper = new ObjectMapper();

    public InterpretacaoTransacao dadosTransacao(String frase) {
        try {
            Map<String, Object> message1 = Map.of(
                    "role", "system",
                    "content", "Você é um assistente que responde apenas com JSON válido, sem explicações."
            );

            Map<String, Object> message2 = Map.of(
                    "role", "user",
                    "content", String.format("""
                        A partir da frase abaixo, identifique:

                        - A ação que o sistema deve executar (chave "acao").
                        - O tipo da transação (chave "tipo") com os possíveis valores: "entrada", "despesa" ou "lucro".
                        - O mês de início (chave "mesInicio", número de 1 a 12).
                        - O mês de fim (chave "mesFim", número de 1 a 12).
                        - A filial envolvida, se mencionada (chave "filial") sempre utilizar essa estrutura 'Filial 1'.
                        - A ação nunca deve ser null.

                         Regras para determinar "acao":

                        - Se mencionar **tipo de transação** E **filial**, retorne: \s
                          `"consultar_somatorio_transacao_por_filial_tipo_e_intervalo"`

                        - Se mencionar apenas o **tipo de transação**, sem filial, retorne: \s
                          `"consultar_somatorio_transacao_por_tipo_e_intervalo"`

                        - Se perguntar qual filial teve maior movimentação, responda com: \s
                          `"consultar_filial_maior_transacao_somatorio_tipo_e_intervalo"`

                        - Se pedir um resumo financeiro de uma filial (com ou sem tipo), use: \s
                          `"consultar_resumo_financeiro_filial_tipos_e_intervalo"`

                        - Se quiser ver os **detalhes de cada transação** de uma filial por período: \s
                          `"consultar_transacoes_detalhadas_por_filial_e_periodo"`

                        - Se quiser **comparar valores entre filiais por tipo**, retorne: \s
                          `"consultar_comparativo_entre_filiais_por_tipo_e_periodo"`

                        - Se quiser o **total geral** de um tipo (sem filial), use: \s
                          `"consultar_total_geral_por_tipo_e_periodo"`

                         Regras para "tipo":
                        - "entrada" → entrada
                        - "despesa" → despesa
                        - "lucro" → lucro
                        - "faturamento" → entrada

                         Regras para "mesInicio" e "mesFim":
                        - Se só um mês for citado, use o mesmo em ambos
                        - Se for intervalo (ex: "janeiro a março"), converta corretamente
                        - Se nenhum for citado, use null

                         Regra para "filial":
                        - exemplo 'Filial 1'
                        - Se ausente, use null

                        Retorne apenas um JSON neste formato:

                        json
                        {
                          "acao": "consultar_somatorio_transacao_por_filial_tipo_e_intervalo",
                          "tipo": "entrada",
                          "mesInicio": 1,
                          "mesFim": 3,
                          "filial": "Filial 2"
                        }
                        Frase: "%s"
        """, frase)
            );

            Map<String, Object> requestMap = Map.of(
                    "messages", List.of(message1, message2),
                    "max_tokens", 500,
                    "temperature", 0.7
            );

            String requestBody = mapper.writeValueAsString(requestMap);

            System.out.println("frase " + frase);

            String url = String.format("%s/openai/deployments/%s/chat/completions?api-version=%s",
                    azureEndpoint, azureDeploymentName, azureApiVersion);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("api-key", azureApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status: " + response.statusCode());
            System.out.println("Body: " + response.body());

            JsonNode root = mapper.readTree(response.body());
            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                System.err.println("Resposta inesperada da API: " + response.body());
                return null;
            }

            // Extrai e limpa o conteúdo da IA
            String jsonContent = choices.get(0).path("message").path("content").asText().trim();

            System.out.println("json content: " + jsonContent);

            if (jsonContent.startsWith("```")) {
                jsonContent = jsonContent.replaceAll("(?s)```(?:json)?\\s*", "")
                        .replaceAll("```", "")
                        .trim();
            }

            if ("null".equalsIgnoreCase(jsonContent)) {
                return null;
            }

            JsonNode data = mapper.readTree(jsonContent);

            InterpretacaoTransacao dto = new InterpretacaoTransacao();
            dto.setAcao(data.path("acao").asText(null));
            dto.setTipo(data.path("tipo").asText(null));
            dto.setMesInicio(data.path("mesInicio").isInt() ? data.path("mesInicio").asInt() : null);
            dto.setMesFim(data.path("mesFim").isInt() ? data.path("mesFim").asInt() : null);
            dto.setFilial(data.path("filial").asText(null));

            int anoAtual = Year.now().getValue();
            dto.setAno(anoAtual);

            if (dto.getMesInicio() != null)
                dto.setDataInicio(LocalDate.of(anoAtual, dto.getMesInicio(), 1));
            if (dto.getMesFim() != null)
                dto.setDataFim(LocalDate.of(anoAtual, dto.getMesFim(), 1).withDayOfMonth(1).plusMonths(1).minusDays(1));

            return dto;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
