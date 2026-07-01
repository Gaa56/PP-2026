package pt.ipp.estg.pp.pickingManagement;

import com.estg.pickingManagement.Strategy;
import com.estg.core.Institution;
import com.estg.pickingManagement.RouteValidator;
import com.estg.pickingManagement.Route;
import com.estg.pickingManagement.Vehicle;
import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.Measurement;
import com.estg.pickingManagement.exceptions.RouteException;

public class StrategyImpl implements Strategy {

    @Override
    public Route[] generate(Institution inst, RouteValidator validator) {
        Vehicle[] vehicles = inst.getVehicles();
        AidBox[] aidBoxes = inst.getAidBoxes();

        Route[] tempRoutes = new Route[vehicles.length];
        int numberOfRoutes = 0;

        for (int i = 0; i < vehicles.length; i++) {
            Route route = new RouteImpl(vehicles[i]);

            for (int j = 0; j < aidBoxes.length; j++) {
                if (hasCollectableContainer(vehicles[i], aidBoxes[j])) {
                    addAidBoxToRoute(route, aidBoxes[j], validator);
                }
            }

            if (route.getRoute().length > 0) {
                tempRoutes[numberOfRoutes] = route;
                numberOfRoutes++;
            }
        }

        Route[] result = new Route[numberOfRoutes];
        for (int i = 0; i < numberOfRoutes; i++) {
            result[i] = tempRoutes[i];
        }

        return result;
    }

    public boolean hasCollectableContainer(Vehicle vehicle, AidBox aidBox) {
        Container[] containers = aidBox.getContainers();

        for (int i = 0; i < containers.length; i++) {
            if (containers[i].getType() == vehicle.getSupplyType()) {
                Measurement[] measurements = containers[i].getMeasurements();
                if (measurements != null && measurements.length > 0) {
                    if (measurements[measurements.length - 1].getValue() > containers[i].getCapacity() * 0.8) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean addAidBoxToRoute(Route route, AidBox aidBox, RouteValidator validator) {
        if (validator.validate(route, aidBox)) {
            try {
                route.addAidBox(aidBox);
                return true;
            } catch (RouteException e) {
                return false;
            }
        }
        return false;
    }
}
