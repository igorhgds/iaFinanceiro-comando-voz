package igor.henrique.iaFinanceiro.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import igor.henrique.iaFinanceiro.ai.ExtratorComInstruct;
import igor.henrique.iaFinanceiro.ai.ResultadoTransacao;
import igor.henrique.iaFinanceiro.util.TextoFinanceiroParser;

public class AnalisadorTextoFinanceiro {

    public static ResultadoTransacao analisar(String frase) {
        try {
            // Extrair dados usando IA
            String json = ExtratorComInstruct.dadosTransacao(frase);
            ResultadoTransacao transacao = new ObjectMapper().readValue(json, ResultadoTransacao.class);

            // Agora você pode usar transacao.getTipo() e transacao.getMes() para consultar no banco
            // Aqui, você pode realizar a consulta no banco com base nesses dados
            String resultadoBanco = consultarNoBanco(transacao.getTipo(), transacao.getMes());
            System.out.println("Resultado da consulta no banco: " + resultadoBanco);

            return transacao;
        } catch (Exception e) {
            System.out.println("Erro na IA: " + e.getMessage());
            return TextoFinanceiroParser.analisarComMap(frase); // Fallback para análise local
        }
    }

    private static String consultarNoBanco(String tipo, Integer mes) {
        // Aqui você pode implementar a lógica para consultar o banco de dados
        // Usando o tipo de transação e o mês
        // Exemplo (pode variar conforme a estrutura do seu banco):
        String sql = "SELECT * FROM transacoes WHERE tipo = ? AND mes = ?";

        // Use uma função de execução de query no banco para buscar os dados
        return "Resultado da query do banco";
    }
}
