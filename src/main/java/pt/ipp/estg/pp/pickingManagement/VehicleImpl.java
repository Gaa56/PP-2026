package pt.ipp.estg.pp.pickingManagement;

import com.estg.pickingManagement.Vehicle;
import com.estg.core.ItemType;

public class VehicleImpl implements Vehicle {

    private double maxCapacity;
    private ItemType supplyType;

    public VehicleImpl(double maxCapacity, ItemType supplyType) {
        this.maxCapacity = maxCapacity;
        this.supplyType = supplyType;
    }

    @Override
    public double getMaxCapacity() {
        return this.maxCapacity;       
    }

    @Override
    public ItemType getSupplyType() {
        return this.supplyType;
    }
  
   @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Vehicle)) return false;
        // Se a outra classe for VehicleImpl, comparamos por ID. Caso contrário, comparamos propriedades
        if (obj instanceof VehicleImpl) {
            VehicleImpl vehicle = (VehicleImpl) o;
            return Objects.equals(id, vehicle.id);
        }
        Vehicle vehicle = (Vehicle) o;
        return Double.compare(vehicle.getMaxCapacity(), getMaxCapacity()) == 0 &&
               getSupplyType() == vehicle.getSupplyType();
    }

    @Override
    public String toString() {
        return "Veículo [Tipo: " + supplyType + ", Cap. Máx: " + maxCapacity + " kg]";
    }
}
