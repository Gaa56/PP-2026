package pt.ipp.estg.pp.pickingManagement;

import com.estg.pickingManagement.RouteGenerator;
import com.estg.core.Institution;
import com.estg.pickingManagement.Strategy;
import com.estg.pickingManagement.RouteValidator;
import com.estg.pickingManagement.Report;
import com.estg.pickingManagement.Route;
import com.estg.core.AidBox;
import com.estg.core.ItemType;
import com.estg.pickingManagement.Vehicle;
import pt.ipp.estg.pp.core.InstitutionImpl;
import com.estg.core.exceptions.AidBoxException;

import java.time.LocalDateTime;

/**
 * Implementação da interface {@link RouteGenerator}.
 * Responsável por gerar rotas, calcular a distância/duração total percorrida de forma precisa,
 * e preencher as estatísticas associadas num relatório (Report).
 */
public class RouteGeneratorImpl implements RouteGenerator {

    @Override
    public Route[] generateRoutes(Institution institution, Strategy strategy, RouteValidator validator, Report report) {
        if (institution == null || strategy == null || validator == null) {
            return new Route[0];
        }

        // 1. Gerar as rotas utilizando a estratégia fornecida
        Route[] routes = strategy.generate(institution, validator);

        double totalDistance = 0.0;
        double totalDuration = 0.0;
        int pickedContainersCount = 0;

        // Contabilizar o total de contentores no sistema
        AidBox[] allAidBoxes = institution.getAidBoxes();
        int totalContainers = 0;
        for (AidBox box : allAidBoxes) {
            if (box.getContainers() != null) {
                totalContainers += box.getContainers().length;
            }
        }

        // 2. Calcular distância/duração de cada rota e atualizar os seus campos internos
        if (routes != null) {
            for (Route r : routes) {
                if (r instanceof RouteImpl) {
                    RouteImpl routeImpl = (RouteImpl) r;
                    
                    double routeDist = calculateRouteDistance(institution, routeImpl);
                    double routeDur = calculateRouteDuration(institution, routeImpl);
                    
                    routeImpl.setTotalDistance(routeDist);
                    routeImpl.setTotalDuration(routeDur);
                    
                    totalDistance += routeDist;
                    totalDuration += routeDur;

                    // Contabilizar contentores recolhidos por esta rota
                    ItemType supplyType = r.getVehicle().getSupplyType();
                    for (AidBox box : r.getRoute()) {
                        if (box.getContainer(supplyType) != null) {
                            pickedContainersCount++;
                        }
                    }
                }
            }
        }

        // 3. Contabilizar estatísticas dos veículos
        Vehicle[] allVehicles = institution.getVehicles();
        int totalActiveVehicles = 0;
        for (Vehicle v : allVehicles) {
            if (v instanceof VehicleImpl) {
                if (((VehicleImpl) v).getState() == State.ACTIVE) {
                    totalActiveVehicles++;
                }
            }
        }

        int usedVehiclesCount = 0;
        if (routes != null) {
            for (Route r : routes) {
                if (r.getRoute() != null && r.getRoute().length > 0) {
                    usedVehiclesCount++;
                }
            }
        }
        int notUsedVehiclesCount = Math.max(0, totalActiveVehicles - usedVehiclesCount);
        int nonPickedContainersCount = Math.max(0, totalContainers - pickedContainersCount);

        // 4. Preencher o objeto Report com as métricas geradas, caso seja uma instância de ReportImpl
        if (report instanceof ReportImpl) {
            ReportImpl rImpl = (ReportImpl) report;
            rImpl.setDate(LocalDateTime.now());
            rImpl.setPickedContainers(pickedContainersCount);
            rImpl.setNonPickedContainers(nonPickedContainersCount);
            rImpl.setUsedVehicles(usedVehiclesCount);
            rImpl.setNotUsedVehicles(notUsedVehiclesCount);
            rImpl.setTotalDistance(totalDistance);
            rImpl.setTotalDuration(totalDuration);
        }

        return routes != null ? routes : new Route[0];
    }

    /**
     * Calcula a distância total de uma rota (Base -> Caixas da rota -> Base).
     */
    private double calculateRouteDistance(Institution institution, Route route) {
        AidBox[] path = route.getRoute();
        if (path == null || path.length == 0) {
            return 0.0;
        }
        double distance = 0.0;
        try {
            // Distância da Base até à primeira caixa
            distance += institution.getDistance(path[0]);
            
            // Distâncias entre caixas consecutivas
            for (int i = 0; i < path.length - 1; i++) {
                distance += path[i].getDistance(path[i + 1]);
            }
            
            // Distância da última caixa de regresso à Base
            distance += institution.getDistance(path[path.length - 1]);
        } catch (AidBoxException e) {
            // Se falhar alguma ligação, retorna o acumulado
        }
        return distance;
    }

    /**
     * Calcula a duração total estimada de uma rota (Base -> Caixas da rota -> Base).
     */
    private double calculateRouteDuration(Institution institution, Route route) {
        AidBox[] path = route.getRoute();
        if (path == null || path.length == 0) {
            return 0.0;
        }
        double duration = 0.0;
        try {
            // Duração da Base até à primeira caixa
            if (institution instanceof InstitutionImpl) {
                duration += ((InstitutionImpl) institution).getDuration(path[0]);
            }
            
            // Duração entre caixas consecutivas
            for (int i = 0; i < path.length - 1; i++) {
                duration += path[i].getDuration(path[i + 1]);
            }
            
            // Duração da última caixa de regresso à Base
            if (institution instanceof InstitutionImpl) {
                duration += ((InstitutionImpl) institution).getDuration(path[path.length - 1]);
            }
        } catch (AidBoxException e) {
            // Se falhar alguma ligação, retorna o acumulado
        }
        return duration;
    }
}
