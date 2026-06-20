package pt.ipp.estg.pp.simulator;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.Institution;
import com.estg.core.Measurement;
import com.estg.core.exceptions.ContainerException;
import com.estg.core.exceptions.MeasurementException;

import pt.ipp.estg.pp.core.MeasurementImpl;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Simulador de sensores para os contentores.
 * Permite gerar valores de peso aleatórios para simular o comportamento de sensores reais.
 */
public class SensorSimulator {

    /**
     * Simula a leitura de sensores para todos os contentores de todas as AidBoxes na instituição.
     * Gera um valor aleatório de peso (entre 0 e a capacidade do contentor)
     * e adiciona uma medição com a data e hora atual.
     *
     * @param institution a instituição contendo as AidBoxes e contentores
     */
    public static void generateRandomMeasurements(Institution institution) {
        if (institution == null) {
            return;
        }

        AidBox[] boxes = institution.getAidBoxes();
        if (boxes == null) {
            return;
        }

        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();

        System.out.println("\n[SensorSimulator] A gerar medições aleatórias...");

        for (AidBox box : boxes) {
            if (box == null) continue;

            Container[] containers = box.getContainers();
            if (containers == null) continue;

            for (Container c : containers) {
                if (c == null) continue;

                // Gerar valor aleatório entre 0.0 e a capacidade máxima do contentor
                double maxCapacity = c.getCapacity();
                double randomValue = random.nextDouble() * maxCapacity;
                
                // Arredondar a duas casas decimais
                randomValue = Math.round(randomValue * 100.0) / 100.0;

                Measurement measurement = new MeasurementImpl(now, randomValue);
                try {
                    boolean added = institution.addMeasurement(measurement, c);
                    if (added) {
                        System.out.println("  -> Sensor " + box.getCode() + "/" + c.getCode() + 
                                           " registou: " + randomValue + " Kg (Capacidade: " + maxCapacity + " Kg)");
                    }
                } catch (ContainerException | MeasurementException e) {
                    System.err.println("  -> Erro ao registar sensor " + box.getCode() + "/" + c.getCode() + ": " + e.getMessage());
                }
            }
        }
        System.out.println("[SensorSimulator] Simulação concluída.\n");
    }
}
