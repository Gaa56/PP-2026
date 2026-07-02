package pt.ipp.estg.pp.pickingManagement;

import com.estg.pickingManagement.RouteValidator;
import com.estg.pickingManagement.Route;
import com.estg.core.AidBox;

public class RouteValidatorImpl implements RouteValidator {

    @Override
    public boolean validate(Route route, AidBox aidBox) {
        return route != null && aidBox != null && !route.containsAidBox(aidBox);
    }
}
