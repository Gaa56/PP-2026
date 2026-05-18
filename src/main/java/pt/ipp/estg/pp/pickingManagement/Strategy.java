package pt.ipp.estg.pp.pickingManagement;

import pt.ipp.estg.pp.core.Institution;

public interface Strategy {
    Route[] generate(Institution institution, RouteValidator routeValidator);
}
