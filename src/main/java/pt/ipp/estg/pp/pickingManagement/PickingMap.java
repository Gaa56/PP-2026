package pt.ipp.estg.pp.pickingManagement;

import java.time.LocalDateTime;

public interface PickingMap {
    LocalDateTime getDate();

    Route[] getRoutes();
}
