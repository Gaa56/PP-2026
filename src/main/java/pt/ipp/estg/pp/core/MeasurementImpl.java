package pt.ipp.estg.pp.core;

import com.estg.core.Measurement;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Implementação da interface {@link Measurement}.
 * Representa uma medição de peso (em Kg) efetuada num contentor numa determinada data/hora.
 */
public class MeasurementImpl implements Measurement {

    private final LocalDateTime date;
    private final double value;

    /**
     * Construtor para criar uma medição de peso.
     *
     * @param date  A data e hora em que a medição foi efetuada (não pode ser nula).
     * @param value O valor da medição em Kg (não pode ser negativo).
     */
    public MeasurementImpl(LocalDateTime date, double value) {

        this.date = date;
        this.value = value;
    }

    /**
     * Devolve a data e hora em que a medição foi realizada.
     *
     * @return A data e hora da medição.
     */
    @Override
    public LocalDateTime getDate() {
        return this.date;
    }

    /**
     * Devolve o valor da medição (peso em Kg).
     *
     * @return O valor em Kg.
     */
    @Override
    public double getValue() {
        return this.value;
    }

    /**
     * Compara se esta medição é igual a outro objeto.
     * Duas medições são consideradas iguais se tiverem a mesma data e valor.
     *
     * @param obj O objeto a comparar.
     * @return true se forem iguais, false caso contrário.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Measurement)) {
            return false;
        }
        Measurement other = (Measurement) obj;
        return Double.compare(other.getValue(), this.getValue()) == 0 &&
               Objects.equals(this.getDate(), other.getDate());
    }

    /**
     * Gera o código hash para a medição com base na data e no valor.
     *
     * @return O código hash correspondente.
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.date, this.value);
    }

    /**
     * Devolve uma representação textual da medição.
     *
     * @return String com os detalhes da medição.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MeasurementImpl{")
          .append("date=").append(date)
          .append(", value=").append(value)
          .append("}");
        return sb.toString();
    }
}
