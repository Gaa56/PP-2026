package pt.ipp.estg.pp.core;

import java.time.LocalDate;



public interface Container extends Cloneable {

    boolean addMeasurement(Measurement measurement);

    double getCapacity();

    String getCode();

    Measurement[] getMeasurements();

    Measurement[] getMeasurements(LocalDate date);

    ItemType getType();
    


}
