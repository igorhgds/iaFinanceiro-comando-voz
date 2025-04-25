package igor.henrique.iaFinanceiro.enums;

public enum TipoTransacao {
    ENTRADA, DESPESA, LUCRO;

    public static TipoTransacao fromString(String tipo) {
        System.out.println("Valor recebido no fromString: " + tipo); // Verifique o valor recebido
        switch (tipo.toLowerCase()) {
            case "entrada":
                return ENTRADA;
            case "despesa":
                return DESPESA;
            default:
                throw new IllegalArgumentException("Tipo de transação inválido: " + tipo);
        }
    }

}

