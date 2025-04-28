package igor.henrique.iaFinanceiro.service;

import igor.henrique.iaFinanceiro.ai.ExtratorComInstruct;
import igor.henrique.iaFinanceiro.dtos.transacao.InterpretacaoTransacao;

public class AnalisadorTextoFinanceiro {

    public static InterpretacaoTransacao analisar(String frase) {
        try {
            // Extrair dados usando IA
            InterpretacaoTransacao transacao = ExtratorComInstruct.dadosTransacao(frase);

            if (transacao == null) {
                throw new IllegalStateException("Não foi possível interpretar a frase.");
            }

            // Agora você pode usar transacao.getTipo(), transacao.getMesInicio(), transacao.getMesFim(), transacao.getEmpresa()
            // Exemplo (depois você conecta com seu banco real):
            String resultadoBanco = consultarNoBanco(transacao.getTipo(), transacao.getMesInicio());
            System.out.println("Resultado da consulta no banco: " + resultadoBanco);

            return transacao;
        } catch (Exception e) {
            System.out.println("Erro na IA: " + e.getMessage());
            throw new RuntimeException("Erro ao analisar a frase. Tente novamente.", e);
        }
    }

    private static String consultarNoBanco(String tipo, Integer mes) {
        // Aqui você pode implementar a lógica real para consultar o banco de dados
        // Atualmente é um mock:
        return "Resultado da query do banco simulada";
    }
}
