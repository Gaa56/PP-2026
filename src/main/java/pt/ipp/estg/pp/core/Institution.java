package pt.ipp.estg.pp.core;

import java.time.LocalDateTime;

import pt.ipp.estg.pp.pickingManagement.PickingMap;
import pt.ipp.estg.pp.pickingManagement.Vehicle;

public interface Institution {

    boolean addAidBox(AidBox aidBox);

    boolean addMeasurement(Measurement measurement, Container container);

    boolean addPickingMap(PickingMap pickingMap);

    boolean addVehicle(Vehicle vehicle);

    void disableVehicle(Vehicle vehicle);

    void enableVehicle(Vehicle vehicle);

    AidBox[] getAidBoxes();

    Container getContainer(AidBox aidBox, ItemType itemType);

    PickingMap getCurrentPickingMap();

    double getDistance(AidBox aidBox);

    String getName();

    PickingMap[] getPickingMaps();

    PickingMap[] getPickingMaps(LocalDateTime from, LocalDateTime to);

    Vehicle[] getVehicles();

}
