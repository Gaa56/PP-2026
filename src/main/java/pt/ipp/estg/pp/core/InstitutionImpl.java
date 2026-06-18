package pt.ipp.estg.pp.core;

import java.time.LocalDateTime;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.Institution;
import com.estg.core.ItemType;
import com.estg.core.Measurement;
import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;
import com.estg.core.exceptions.MeasurementException;
import com.estg.core.exceptions.PickingMapException;
import com.estg.core.exceptions.VehicleException;
import com.estg.pickingManagement.PickingMap;
import com.estg.pickingManagement.Vehicle;

public class InstitutionImpl implements Institution {

    @Override
    public boolean addAidBox(AidBox arg0) throws AidBoxException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addAidBox'");
    }

    @Override
    public boolean addMeasurement(Measurement arg0, Container arg1) throws ContainerException, MeasurementException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addMeasurement'");
    }

    @Override
    public boolean addPickingMap(PickingMap arg0) throws PickingMapException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addPickingMap'");
    }

    @Override
    public boolean addVehicle(Vehicle arg0) throws VehicleException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addVehicle'");
    }

    @Override
    public void disableVehicle(Vehicle arg0) throws VehicleException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'disableVehicle'");
    }

    @Override
    public void enableVehicle(Vehicle arg0) throws VehicleException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'enableVehicle'");
    }

    @Override
    public AidBox[] getAidBoxes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAidBoxes'");
    }

    @Override
    public Container getContainer(AidBox arg0, ItemType arg1) throws ContainerException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getContainer'");
    }

    @Override
    public PickingMap getCurrentPickingMap() throws PickingMapException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCurrentPickingMap'");
    }

    @Override
    public double getDistance(AidBox arg0) throws AidBoxException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDistance'");
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }

    @Override
    public PickingMap[] getPickingMaps() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPickingMaps'");
    }

    @Override
    public PickingMap[] getPickingMaps(LocalDateTime arg0, LocalDateTime arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPickingMaps'");
    }

    @Override
    public Vehicle[] getVehicles() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getVehicles'");
    }
    // TODO: Implementar métodos da interface
}
