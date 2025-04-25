package igor.henrique.iaFinanceiro.enums;

public enum TipoTransacao {
    ENTRADA, DESPESA, LUCRO;

    public static TipoTransacao fromString(String tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de transação não pode ser nulo");
        }

        tipo = tipo.trim().toLowerCase();
        for (TipoTransacao t : values()) {
            if (t.name().toLowerCase().equals(tipo)) {
                return t;
            }
        }

        throw new IllegalArgumentException("Tipo de transação inválido: " + tipo);
    }
}


