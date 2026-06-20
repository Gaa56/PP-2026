package pt.ipp.estg.pp.core;

import com.estg.core.Container;
import com.estg.core.ItemType;
import com.estg.core.Measurement;
import com.estg.core.exceptions.MeasurementException;

import java.time.LocalDate;

/**
 * Implementação da interface Container.
 * Representa um contentor de um determinado tipo, com uma capacidade máxima
 * e a capacidade de registar medições ao longo do tempo.
 */
public class ContainerImpl implements Container {

    private double capacity;
    private String code;
    private ItemType type;
    private Measurement[] measurements;
    private int measurementCount;

    /**
     * Construtor principal para inicializar um contentor com todos os parâmetros.
     *
     * @param capacity A capacidade do contentor em Kg.
     * @param code     O código identificador do contentor.
     * @param type     O tipo de itens que o contentor suporta.
     */
    public ContainerImpl(double capacity, String code, ItemType type) {
        this.capacity = capacity;
        this.code = code;
        this.type = type;
        this.measurements = new Measurement[100];
        this.measurementCount = 0;
    }

    /**
     * Construtor sobrecarregado (Overloading).
     * Cria um contentor assumindo uma capacidade padrão de 100.0 Kg.
     *
     * @param code O código identificador do contentor.
     * @param type O tipo de itens que o contentor suporta.
     */
    public ContainerImpl(String code, ItemType type) {
        this(100.0, code, type);
    }

    /**
     * Adiciona uma nova medição ao contentor.
     * Expande o array automaticamente caso o limite inicial seja atingido.
     *
     * @param measurement A medição a ser adicionada.
     * @return true se inserido com sucesso, false se já existir uma medição com igual valor na mesma data.
     * @throws MeasurementException se a medição for inválida de acordo com as regras.
     */
    @Override
    public boolean addMeasurement(Measurement measurement) throws MeasurementException {
        if (measurement == null) {
            throw new MeasurementException("Measurement is null");
        }
        if (this.measurementCount >= this.measurements.length) {
            expandArray();
        }

        if (measurement.getValue() < 0) {
            throw new MeasurementException("Measurement value is negative");
        }

        if (this.measurementCount > 0) {
            Measurement ultimaMedicao = this.measurements[this.measurementCount - 1];

            if (measurement.getDate().isBefore(ultimaMedicao.getDate())) {
                throw new MeasurementException(
                        "Measurement date is before the last measurement date");
            }

            for (int i = 0; i < this.measurementCount; i++) {
                if (this.measurements[i].getDate().equals(measurement.getDate())) {
                    if (this.measurements[i].getValue() != measurement.getValue()) {
                        throw new MeasurementException(
                                "A measurement for this date already exists with a different value");
                    } else {
                        return false;
                    }
                }
            }
        }
        this.measurements[this.measurementCount] = measurement;

        this.measurementCount++;

        return true;
    }

    /**
     * Método auxiliar privado para expandir o tamanho do array de medições
     * quando este atinge o seu limite, duplicando o seu tamanho.
     */
    private void expandArray() {
        Measurement[] newMeasurements = new Measurement[this.measurements.length * 2];
        for (int i = 0; i < this.measurementCount; i++) {
            newMeasurements[i] = this.measurements[i];
        }
        this.measurements = newMeasurements;
    }

    /**
     * Devolve a capacidade máxima do contentor.
     *
     * @return A capacidade em Kg.
     */
    @Override
    public double getCapacity() {
        return capacity;
    }

    /**
     * Devolve o código do contentor.
     *
     * @return O código em formato String.
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * Devolve uma cópia profunda (deep copy) das medições registadas.
     *
     * @return Um array contendo as medições.
     */
    @Override
    public Measurement[] getMeasurements() {
        Measurement[] copy = new Measurement[this.measurementCount];

        for (int i = 0; i < this.measurementCount; i++) {
            copy[i] = this.measurements[i];
        }

        return copy;
    }

    /**
     * Devolve uma cópia das medições para uma data específica.
     *
     * @param date A data para filtrar as medições.
     * @return Um array contendo as medições dessa data.
     */
    @Override
    public Measurement[] getMeasurements(LocalDate date) {
        int count = 0;
        for (int i = 0; i < this.measurementCount; i++) {
            if (this.measurements[i].getDate().toLocalDate().equals(date)) {
                count++;
            }            
        }
        
        Measurement[] measurementByDate = new Measurement[count];
        int tempVar = 0;

        for (int i = 0; i < this.measurementCount; i++) {
            // Repetimos o MESMO IF para copiar só os que passam no filtro
            if (this.measurements[i].getDate().toLocalDate().equals(date)) {
                measurementByDate[tempVar] = this.measurements[i];
                tempVar++;
            }
        }

        return measurementByDate;
    }

    /**
     * Devolve o tipo de itens que este contentor suporta.
     *
     * @return O ItemType correspondente.
     */
    @Override
    public ItemType getType() {
        return type;
    }

    /**
     * Devolve uma representação textual do contentor, utilizando um StringBuilder
     * para melhorar o desempenho na concatenação de strings.
     *
     * @return String com os detalhes do contentor.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Container [Código: ").append(this.code)
          .append(", Tipo: ").append(this.type)
          .append(", Capacidade: ").append(this.capacity).append(" Kg")
          .append(", Total de Medições: ").append(this.measurementCount)
          .append("]");
        return sb.toString();
    }

    /**
     * Compara se este contentor é igual a outro objeto baseado no seu código.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Container)) {
            return false;
        }
        Container other = (Container) obj;
        return this.code != null && this.code.equalsIgnoreCase(other.getCode());
    }

    /**
     * Gera o código hash baseado no código do contentor.
     */
    @Override
    public int hashCode() {
        return this.code != null ? this.code.toLowerCase().hashCode() : 0;
    }
}
