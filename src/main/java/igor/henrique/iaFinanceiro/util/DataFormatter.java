package igor.henrique.iaFinanceiro.util;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class DataFormatter {

    public static String nomeMes(LocalDate data) {
        if (data == null) {
            return "";
        }
        return data.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
    }

    public static boolean mesmoMes(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            return false;
        }
        return dataInicio.getMonth() == dataFim.getMonth();
    }
}
