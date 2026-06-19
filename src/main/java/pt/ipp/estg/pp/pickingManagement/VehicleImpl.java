package pt.ipp.estg.pp.pickingManagement;

import com.estg.pickingManagement.Vehicle;
import com.estg.core.ItemType;

/**
 * Representação concreta de um veículo que pode ser utilizado para transportar contentores.
 * Cada veículo possui uma capacidade máxima de transporte e é limitado a carregar apenas um tipo de item.
 */
public class VehicleImpl implements Vehicle {

    private double maxCapacity;
    private ItemType supplyType;
    private State state;

    /**
     * Construtor para criar uma instância de um veículo.
     *
     * @param maxCapacity capacidade máxima do veículo em kg
     * @param supplyType  tipo de item suportado pelo veículo
     * @param state       estado inicial do veículo
     */
    public VehicleImpl(double maxCapacity, ItemType supplyType, State state) {
        this.maxCapacity = maxCapacity;
        this.supplyType = supplyType;
        this.state = State.ACTIVE;
    }

    /**
     * Retorna a capacidade máxima (em kg) suportada pelo veículo.
     *
     * @return a capacidade máxima em kg
     */
    @Override
    public double getMaxCapacity() {
        return this.maxCapacity;
    }

    /**
     * Retorna o tipo de item que o veículo pode transportar.
     *
     * @return o tipo de item
     */
    @Override
    public ItemType getSupplyType() {
        return this.supplyType;
    }

    /**
     * Retorna o estado atual do veículo.
     *
     * @return o estado do veículo
     */
    public State getState() {
        return this.state;
    }

    /**
     * Altera o estado do veículo para desativado (DISABLE).
     */
    public void setStateDisable() {
        this.state = State.DISABLE;
    }

    /**
     * Altera o estado do veículo para ativo (ACTIVE).
     */
    public void setStateActive() {
        this.state = State.ACTIVE;
    }

    /**
     * Compara se o veículo atual é igual a outro objeto baseado nas suas propriedades.
     *
     * @param obj objeto a comparar
     * @return true se forem equivalentes, false caso contrário
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !(obj instanceof Vehicle))
            return false;
        Vehicle vehicle = (Vehicle) obj;
        return Double.compare(vehicle.getMaxCapacity(), getMaxCapacity()) == 0 &&
                getSupplyType() == vehicle.getSupplyType();
    }

    /**
     * Retorna o veículo representado em formato de texto usando StringBuilder.
     *
     * @return representação textual do veículo
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("VehicleImpl{")
                .append("supplyType: '").append(supplyType).append('\'')
                .append(", maxCapacity: ").append(maxCapacity).append(" kg")
                .append(", state: ").append(state)
                .append("}");

        return sb.toString();
    }
}
