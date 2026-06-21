package pt.ipp.estg.pp.pickingManagement;

import com.estg.pickingManagement.RouteValidator;
import com.estg.pickingManagement.Route;
import com.estg.core.AidBox;

public class RouteValidatorImpl implements RouteValidator {

    @Override
    public boolean validate(Route route, AidBox aidBox) {
        if (route.containsAidBox(aidBox)) {
            return true;
        } else {
            return false;
        }
    }
}
