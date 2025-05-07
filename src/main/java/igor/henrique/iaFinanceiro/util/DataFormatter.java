package igor.henrique.iaFinanceiro.util;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class DataFormatter {

    private static final String[] NOMES_MESES = {
            "janeiro", "fevereiro", "março", "abril", "maio", "junho",
            "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
    };

    public static String nomeMes(LocalDate data) {
        return NOMES_MESES[data.getMonthValue() - 1];
    }

    public static String formatarData(LocalDate data) {
        return data.getDayOfMonth() + " de " + nomeMes(data) + " de " + data.getYear();
    }
}
