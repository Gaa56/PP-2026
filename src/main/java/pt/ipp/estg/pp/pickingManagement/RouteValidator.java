package pt.ipp.estg.pp.pickingManagement;

import pt.ipp.estg.pp.core.AidBox;

public interface RouteValidator {
    boolean validate(Route route, AidBox aidBox);
}
