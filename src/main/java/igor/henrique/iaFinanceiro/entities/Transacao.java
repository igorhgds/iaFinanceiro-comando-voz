package igor.henrique.iaFinanceiro.entities;

import igor.henrique.iaFinanceiro.enums.TipoTransacao;
import igor.henrique.iaFinanceiro.util.TipoTransacaoConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "transacoes")
@Getter
@Setter
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = TipoTransacaoConverter.class)
    private TipoTransacao tipo;

    private String cliente;

    private String categoria;

    private Double valor;

    private String filial;

    private LocalDate data;

}
