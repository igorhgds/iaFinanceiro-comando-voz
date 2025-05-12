package igor.henrique.iaFinanceiro.util;

import igor.henrique.iaFinanceiro.enums.TipoTransacao;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoTransacaoConverter implements AttributeConverter<TipoTransacao, String> {

    @Override
    public String convertToDatabaseColumn(TipoTransacao attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name().toLowerCase();
    }

    @Override
    public TipoTransacao convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return TipoTransacao.fromString(dbData);
    }
}
