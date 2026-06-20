package pt.ipp.estg.pp.pickingManagement;

import com.estg.pickingManagement.RouteValidator;
import com.estg.pickingManagement.Route;
import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.Measurement;
import com.estg.pickingManagement.Vehicle;

/**
 * Implementação da interface RouteValidator.
 * Valida se uma AidBox pode ser inserida numa rota, verificando:
 * - Compatibilidade do tipo de contentor com o veículo
 * - Capacidade máxima do veículo (peso total recolhido)
 * - Autonomia máxima para veículos refrigerados (quilometragem)
 */
public class RouteValidatorImpl implements RouteValidator {

    /**
     * Valida se uma AidBox pode ser adicionada à rota fornecida.
     *
     * @param route  a rota atual
     * @param aidBox a AidBox candidata a ser adicionada
     * @return true se a rota permanece válida com a AidBox, false caso contrário
     */
    @Override
    public boolean validate(Route route, AidBox aidBox) {
        if (route == null || aidBox == null) {
            return false;
        }

        Vehicle vehicle = route.getVehicle();
        if (vehicle == null) {
            return false;
        }

        // 1. Verificar compatibilidade de tipo
        Container container = aidBox.getContainer(vehicle.getSupplyType());
        if (container == null) {
            return false;
        }

        // 2. Verificar capacidade do veículo
        double currentLoad = calculateCurrentLoad(route);
        double newLoad = getContainerCurrentWeight(container);
        if (currentLoad + newLoad > vehicle.getMaxCapacity()) {
            return false;
        }

        // 3. Verificar autonomia para veículos refrigerados
        if (vehicle instanceof RefrigeratedVehicleImpl) {
            RefrigeratedVehicleImpl refVehicle = (RefrigeratedVehicleImpl) vehicle;
            double currentDistanceKm = route.getTotalDistance() / 1000.0;
            // Estimar a distância adicional com a nova caixa
            // (usar a distância total atual como base conservadora)
            if (currentDistanceKm > refVehicle.getMaxKilometers()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Calcula o peso total atualmente carregado na rota.
     *
     * @param route a rota
     * @return o peso total em Kg
     */
    private double calculateCurrentLoad(Route route) {
        double total = 0.0;
        AidBox[] boxes = route.getRoute();
        Vehicle vehicle = route.getVehicle();

        if (boxes == null || vehicle == null) {
            return 0.0;
        }

        for (AidBox box : boxes) {
            if (box != null) {
                Container container = box.getContainer(vehicle.getSupplyType());
                if (container != null) {
                    total += getContainerCurrentWeight(container);
                }
            }
        }

        return total;
    }

    /**
     * Obtém o peso atual de um contentor com base na última medição registada.
     *
     * @param container o contentor
     * @return o peso atual em Kg, ou 0 se não houver medições
     */
    private double getContainerCurrentWeight(Container container) {
        Measurement[] measurements = container.getMeasurements();
        if (measurements == null || measurements.length == 0) {
            return 0.0;
        }

        // A última medição representa o peso atual
        return measurements[measurements.length - 1].getValue();
    }
}
