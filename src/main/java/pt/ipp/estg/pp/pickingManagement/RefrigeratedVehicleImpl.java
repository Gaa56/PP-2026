package pt.ipp.estg.pp.pickingManagement;

import com.estg.core.ItemType;

/**
 * Representa um veículo de transporte refrigerado específico, que estende as capacidades
 * de um veículo padrão para suportar o transporte de alimentos perecíveis.
 * Possui uma câmara de refrigeração e restrição de quilometragem máxima de circulação com carga.
 */
public class RefrigeratedVehicleImpl extends VehicleImpl {

     private double maxKilometers;
     private double temperature;

     /**
      * Construtor para criar um veículo refrigerado.
      *
      * @param maxCapacity capacidade máxima do veículo em kg
      * @param supplyType  tipo de item suportado pelo veículo (normalmente perecíveis)
      * @param state       estado inicial do veículo
      * @param maxKilometers limite máximo de quilómetros a circular com carga
      * @param temperature temperatura ideal da câmara de refrigeração em ºC
      */
     public RefrigeratedVehicleImpl(double maxCapacity, ItemType supplyType, State state, double maxKilometers,
                double temperature) {
          super(maxCapacity, supplyType, state);
          this.maxKilometers = maxKilometers;
          this.temperature = temperature;
     }

     /**
      * Retorna o limite máximo de quilómetros que o veículo pode circular com carga.
      *
      * @return quilometragem máxima
      */
     public double getMaxKilometers() {
          return this.maxKilometers;
     }

     /**
      * Retorna a temperatura atual da câmara de refrigeração.
      *
      * @return temperatura em ºC
      */
     public double getTemperature() {
          return this.temperature;
     }

     /**
      * Define uma nova temperatura para a câmara de refrigeração.
      *
      * @param temperature nova temperatura em ºC
      */
     public void setTemperature(double temperature) {
          this.temperature = temperature;
     }

     /**
      * Retorna a representação textual do veículo refrigerado, incluindo os atributos herdados da classe pai.
      *
      * @return representação textual
      */
     @Override
     public String toString() {
          StringBuilder sb = new StringBuilder();

          sb.append("RefrigeratedVehicle{")
                    .append("maxCapacity: ").append(getMaxCapacity()).append(" kg")
                    .append(", supplyType: '").append(getSupplyType()).append('\'')
                    .append(", state: ").append(getState())
                    .append(", maxKilometers: ").append(maxKilometers).append(" km")
                    .append(", temperature: ").append(temperature).append(" ºC")
                    .append("}");
          return sb.toString();
     }
}
