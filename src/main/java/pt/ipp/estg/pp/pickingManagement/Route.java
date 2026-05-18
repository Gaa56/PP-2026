package pt.ipp.estg.pp.pickingManagement;

import pt.ipp.estg.pp.core.AidBox;

public interface Route {
    void addAidBox(AidBox aidBox);

    boolean containsAidBox(AidBox aidBox);

    AidBox[] getRoute();

    double getTotalDistance();

    double getTotalDuration();

    Vehicle getVehicle();

    void insertAfter(AidBox after, AidBox toInsert);

    AidBox removeAidBox(AidBox aidBox);

    void replaceAidBox(AidBox from, AidBox to);
}
