package igor.henrique.iaFinanceiro.service;

import igor.henrique.iaFinanceiro.ai.ExtratorComInstruct;
import igor.henrique.iaFinanceiro.ai.ResultadoTransacao;
import igor.henrique.iaFinanceiro.enums.TipoTransacao;
import igor.henrique.iaFinanceiro.repository.TransacaoRepository;
import igor.henrique.iaFinanceiro.util.TextoFinanceiroParser;
import org.json.JSONException;
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

        // Usando o ChatGPT para extrair os dados (retorna uma string formatada, como "Tipo: entrada, Mes: 4")
        String resultado = ExtratorComInstruct.dadosTransacao(textoLower);
        System.out.println("resultado: " + resultado);

        try {
            // Vamos criar um JSON válido a partir da string formatada "Tipo: entrada, Mes: 4"
            if (resultado != null && !resultado.isEmpty()) {
                JSONObject jsonResultado = new JSONObject();

                // Processando a string para extrair os dados e preencher o JSON
                String[] partes = resultado.split(",");
                for (String parte : partes) {
                    String[] chaveValor = parte.split(":");
                    if (chaveValor[0].trim().equalsIgnoreCase("Tipo")) {
                        jsonResultado.put("tipo", chaveValor[1].trim().toLowerCase());  // Ex: "entrada"
                    }
                    if (chaveValor[0].trim().equalsIgnoreCase("Mes")) {
                        jsonResultado.put("mes", Integer.parseInt(chaveValor[1].trim()));  // Ex: 4
                    }
                }

                // Exibindo o JSON gerado para depuração
                System.out.println("JSON gerado: " + jsonResultado.toString());

                // Agora podemos continuar o processamento como um JSON válido
                String tipoString = jsonResultado.getString("tipo");
                Integer mes = jsonResultado.getInt("mes");

                // Passando o valor de "tipo" para o enum
                TipoTransacao tipo = TipoTransacao.fromString(tipoString);

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
        } catch (JSONException e) {
            System.err.println("Erro ao processar a string ou converter para JSON: " + e.getMessage());
            return "Erro ao processar os dados.";
        }

        return "Desculpe, não entendi a pergunta.";
    }
}