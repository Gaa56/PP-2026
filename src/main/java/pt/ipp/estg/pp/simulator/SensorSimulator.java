package pt.ipp.estg.pp.simulator;

import com.estg.core.Container;
import com.estg.core.Measurement;
import com.estg.core.exceptions.MeasurementException;
import pt.ipp.estg.pp.core.MeasurementImpl;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Classe responsável por simular a leitura de sensores de peso dos contentores.
 * Gera medições aleatórias considerando a capacidade máxima de cada contentor.
 */
public class SensorSimulator {

    private final Random random;

    /**
     * Construtor por omissão. Inicializa o gerador aleatório.
     */
    public SensorSimulator() {
        this.random = new Random();
    }

    /**
     * Construtor com semente (seed) para repetibilidade.
     *
     * @param seed A semente do gerador aleatório.
     */
    public SensorSimulator(long seed) {
        this.random = new Random(seed);
    }

    /**
     * Simula o peso atual de um contentor, gerando um valor aleatório
     * entre 0 e a capacidade máxima do contentor (inclusive).
     *
     * @param container O contentor para o qual simular o peso.
     * @return O peso simulado em Kg.
     */
    public double simulateWeight(Container container) {
        if (container == null) {
            throw new IllegalArgumentException("Container cannot be null.");
        }
        double capacity = container.getCapacity();
        if (capacity <= 0) {
            return 0.0;
        }
        // Gera um valor entre 0 e a capacidade do contentor
        double weight = random.nextDouble() * capacity;
        // Arredondar para 2 casas decimais para maior realismo nas medições
        return Math.round(weight * 100.0) / 100.0;
    }

    /**
     * Gera uma medição (Measurement) para um contentor num determinado instante.
     *
     * @param container O contentor a simular.
     * @param instant   O instante da medição.
     * @return Um objeto do tipo Measurement com a data e valor gerados.
     */
    public Measurement simulateMeasurement(Container container, LocalDateTime instant) {
        double simulatedWeight = simulateWeight(container);
        return new MeasurementImpl(instant, simulatedWeight);
    }

    /**
     * Simula e adiciona diretamente uma medição ao histórico de um contentor.
     *
     * @param container O contentor a atualizar.
     * @param instant   O instante da medição.
     * @return A medição que foi gerada e adicionada.
     * @throws MeasurementException Se ocorrer algum erro de validação ao adicionar a medição ao contentor.
     */
    public Measurement simulateAndAddMeasurement(Container container, LocalDateTime instant) throws MeasurementException {
        Measurement measurement = simulateMeasurement(container, instant);
        container.addMeasurement(measurement);
        return measurement;
    }
}

