package pt.ipp.estg.pp.pickingManagement;

import java.time.LocalDateTime;

public interface Report {
    LocalDateTime getDate();

    int getNonPickedContainers();

    int getNotUsedVehicles();

    int getPickedContainers();

    double getTotalDistance();

    double getTotalDuration();

    int getUsedVehicles();
}
