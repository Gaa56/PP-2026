package pt.ipp.estg.pp.pickingManagement;

import pt.ipp.estg.pp.core.Institution;

public interface RouteGenerator {
    Route[] generateRoutes(Institution institution, Strategy strategy, RouteValidator routeValidator, Report report);
}
