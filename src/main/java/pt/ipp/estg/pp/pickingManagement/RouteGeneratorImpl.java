package pt.ipp.estg.pp.pickingManagement;

import com.estg.pickingManagement.RouteGenerator;
import com.estg.core.Institution;
import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.pickingManagement.Strategy;
import com.estg.pickingManagement.RouteValidator;
import com.estg.pickingManagement.Report;
import com.estg.pickingManagement.Route;
import com.estg.pickingManagement.Vehicle;

import java.time.LocalDateTime;

/**
 * Implementação da interface RouteGenerator.
 * Orquestra a geração de rotas usando uma estratégia, um validador de rotas
 * e produz um relatório com as estatísticas da operação.
 */
public class RouteGeneratorImpl implements RouteGenerator {

    /**
     * Gera as rotas de recolha para a instituição.
     * Delega a geração efetiva à estratégia e calcula as estatísticas para o relatório.
     *
     * @param institution    a instituição
     * @param strategy       a estratégia de recolha
     * @param routeValidator o validador de restrições das rotas
     * @param report         o relatório a preencher com as estatísticas
     * @return as rotas geradas
     */
    @Override
    public Route[] generateRoutes(Institution institution, Strategy strategy,
            RouteValidator routeValidator, Report report) {

        // Gerar as rotas usando a estratégia
        Route[] routes = strategy.generate(institution, routeValidator);

        // Preencher o relatório se for do nosso tipo concreto
        if (report instanceof ReportImpl) {
            ReportImpl reportImpl = (ReportImpl) report;
            fillReport(reportImpl, routes, institution);
        }

        return routes;
    }

    /**
     * Preenche o relatório com as estatísticas calculadas a partir das rotas geradas.
     *
     * @param report      o relatório a preencher
     * @param routes      as rotas geradas
     * @param institution a instituição
     */
    private void fillReport(ReportImpl report, Route[] routes, Institution institution) {
        report.setDate(LocalDateTime.now());

        // Contar veículos utilizados
        int usedVehicles = 0;
        if (routes != null) {
            usedVehicles = routes.length;
        }
        report.setUsedVehicles(usedVehicles);

        // Contar veículos não utilizados
        Vehicle[] allVehicles = institution.getVehicles();
        int totalVehicles = 0;
        if (allVehicles != null) {
            totalVehicles = allVehicles.length;
        }
        report.setNotUsedVehicles(totalVehicles - usedVehicles);

        // Contar contentores recolhidos e calcular distâncias/durações
        int pickedContainers = 0;
        double totalDistance = 0.0;
        double totalDuration = 0.0;

        if (routes != null) {
            for (Route route : routes) {
                if (route != null) {
                    AidBox[] routeBoxes = route.getRoute();
                    if (routeBoxes != null) {
                        pickedContainers += routeBoxes.length;
                    }
                    totalDistance += route.getTotalDistance();
                    totalDuration += route.getTotalDuration();
                }
            }
        }

        report.setPickedContainers(pickedContainers);
        report.setTotalDistance(totalDistance);
        report.setTotalDuration(totalDuration);

        // Contar total de contentores na instituição
        int totalContainers = 0;
        AidBox[] allBoxes = institution.getAidBoxes();
        if (allBoxes != null) {
            for (AidBox box : allBoxes) {
                if (box != null) {
                    Container[] containers = box.getContainers();
                    if (containers != null) {
                        totalContainers += containers.length;
                    }
                }
            }
        }
        report.setNonPickedContainers(totalContainers - pickedContainers);
    }
}
