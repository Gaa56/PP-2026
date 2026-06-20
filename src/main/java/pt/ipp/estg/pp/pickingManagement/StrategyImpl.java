package pt.ipp.estg.pp.pickingManagement;

import com.estg.pickingManagement.Strategy;
import com.estg.core.Institution;
import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.ItemType;
import com.estg.core.Measurement;
import com.estg.pickingManagement.RouteValidator;
import com.estg.pickingManagement.Route;
import com.estg.pickingManagement.Vehicle;
import com.estg.pickingManagement.exceptions.RouteException;

/**
 * Implementação da interface Strategy.
 * Define a estratégia de recolha dos contentores das AidBoxes:
 * - Alimentos perecíveis (PERISHABLE_FOOD): recolhidos sempre que haverem medições com peso > 0
 * - Restantes tipos: recolhidos quando a lotação está acima de 50% da capacidade
 *
 * Gera uma rota por cada veículo ativo, agrupando por tipo de item.
 */
public class StrategyImpl implements Strategy {

    /** Percentagem mínima de ocupação para despoletar a recolha de bens não perecíveis */
    private static final double THRESHOLD = 0.50;

    /**
     * Gera as rotas de recolha para a instituição fornecida.
     *
     * @param institution    a instituição para a qual gerar as rotas
     * @param routeValidator o validador de restrições das rotas
     * @return as rotas geradas
     */
    @Override
    public Route[] generate(Institution institution, RouteValidator routeValidator) {
        if (institution == null || routeValidator == null) {
            return new Route[0];
        }

        AidBox[] allBoxes = institution.getAidBoxes();
        Vehicle[] allVehicles = institution.getVehicles();

        if (allBoxes == null || allBoxes.length == 0 ||
                allVehicles == null || allVehicles.length == 0) {
            return new Route[0];
        }

        // Array temporário para guardar as rotas geradas
        Route[] tempRoutes = new Route[allVehicles.length];
        int routeCount = 0;
        
        // Registo de contentores já atribuídos para evitar duplicação (assumindo máximo de contentores = allBoxes.length * tipos)
        // Uma forma simples é criar um array paralelo de booleanos, mas como não temos ID único para contentor que seja numérico,
        // podemos criar uma lista simples baseada nos códigos.
        String[] assignedContainers = new String[allBoxes.length * 10];
        int assignedCount = 0;

        // Para cada veículo ativo, tentar criar uma rota
        for (Vehicle vehicle : allVehicles) {
            if (vehicle == null) {
                continue;
            }

            // Verificar se o veículo está ativo
            if (vehicle instanceof VehicleImpl) {
                if (((VehicleImpl) vehicle).getState() != State.ACTIVE) {
                    continue;
                }
            }

            ItemType vehicleType = vehicle.getSupplyType();
            RouteImpl route = new RouteImpl(vehicle);
            boolean hasBoxes = false;

            // Iterar pelas AidBoxes e verificar quais necessitam de recolha
            for (AidBox box : allBoxes) {
                if (box == null) {
                    continue;
                }

                Container container = box.getContainer(vehicleType);
                if (container == null) {
                    continue;
                }
                
                // Verificar se já foi atribuído a outra rota
                boolean alreadyAssigned = false;
                for (int i = 0; i < assignedCount; i++) {
                    if (assignedContainers[i].equals(container.getCode())) {
                        alreadyAssigned = true;
                        break;
                    }
                }
                
                if (alreadyAssigned) {
                    continue;
                }

                // Verificar se o contentor precisa de ser recolhido
                if (needsPicking(container, vehicleType)) {
                    // Usar o validador para confirmar que a inserção é válida
                    if (routeValidator.validate(route, box)) {
                        try {
                            route.addAidBox(box);
                            hasBoxes = true;
                            // Marcar como atribuído
                            assignedContainers[assignedCount++] = container.getCode();
                        } catch (RouteException e) {
                            // Se não for possível adicionar, continuar
                        }
                    }
                }
            }

            // Só adicionar a rota se tiver pelo menos uma AidBox
            if (hasBoxes) {
                if (routeCount == tempRoutes.length) {
                    Route[] newRoutes = new Route[tempRoutes.length * 2];
                    System.arraycopy(tempRoutes, 0, newRoutes, 0, routeCount);
                    tempRoutes = newRoutes;
                }
                tempRoutes[routeCount] = route;
                routeCount++;
            }
        }

        // Criar array do tamanho correto
        Route[] result = new Route[routeCount];
        System.arraycopy(tempRoutes, 0, result, 0, routeCount);
        return result;
    }

    /**
     * Determina se um contentor necessita de ser recolhido com base no critério:
     * - Perecíveis: sempre que o peso atual > 0
     * - Restantes tipos: quando a lotação >= 50% da capacidade
     *
     * @param container   o contentor a avaliar
     * @param vehicleType o tipo de item do veículo
     * @return true se o contentor necessita de recolha
     */
    private boolean needsPicking(Container container, ItemType vehicleType) {
        Measurement[] measurements = container.getMeasurements();
        if (measurements == null || measurements.length == 0) {
            return false;
        }

        double currentWeight = measurements[measurements.length - 1].getValue();

        if (currentWeight <= 0) {
            return false;
        }

        // Alimentos perecíveis são sempre recolhidos se tiverem peso
        if (vehicleType == ItemType.PERISHABLE_FOOD) {
            return true;
        }

        // Para outros tipos, só recolher se a lotação >= 50%
        double occupancy = currentWeight / container.getCapacity();
        return occupancy >= THRESHOLD;
    }
}
