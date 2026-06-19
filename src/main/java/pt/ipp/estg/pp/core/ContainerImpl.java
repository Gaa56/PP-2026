package pt.ipp.estg.pp.core;

import com.estg.core.Container;
import com.estg.core.ItemType;
import com.estg.core.Measurement;
import com.estg.core.exceptions.MeasurementException;

import java.time.LocalDate;

public class ContainerImpl implements Container {

    private double capacity;
    private String code;
    private ItemType type;
    private Measurement[] measurements;
    private int measurementCount;

    //Método construtor
    public ContainerImpl(double capacity, String code, ItemType type) {
        this.capacity = capacity;
        this.code = code;
        this.type = type;
        this.measurements = new Measurement[100];
        this.measurementCount = 0;

    }

    @Override
    public boolean addMeasurement(Measurement measurement) throws MeasurementException {
        if (measurement == null) {
            throw new MeasurementException("Measurement is null");
        }
        if (this.measurementCount >= this.measurements.length) {
            return false;
        }

        if (measurement.getValue() < 0) {
            throw new MeasurementException("Measurement value is negative");
        }

        if (this.measurementCount > 0) {
            Measurement ultimaMedicao = this.measurements[this.measurementCount - 1];

            if (measurement.getDate().isBefore(ultimaMedicao.getDate())) {
                throw new MeasurementException(
                        "Measurement date is before the last measurement date");
            }

            for (int i = 0; i < this.measurementCount; i++) {
                if (this.measurements[i].getDate().equals(measurement.getDate())) {
                    if (this.measurements[i].getValue() != measurement.getValue()) {
                        throw new MeasurementException(
                                "A measurement for this date already exists with a different value");
                    } else {
                        return false;
                    }
                }
            }
        }
        this.measurements[this.measurementCount] = measurement;

        this.measurementCount++;

        return true;
    }

    @Override
    public double getCapacity() {
        return capacity;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Measurement[] getMeasurements() {
        Measurement[] copy = new Measurement[this.measurementCount];

        for (int i = 0; i < this.measurementCount; i++) {
            copy[i] = this.measurements[i];
        }

        return copy;
    }

    @Override
    public Measurement[] getMeasurements(LocalDate date) {
        int count = 0;
        for (int i = 0; i < this.measurementCount; i++) {
            if (this.measurements[i].getDate().toLocalDate().equals(date)) {
                count++;
            }            
        }
        
        Measurement[] measurementByDate = new Measurement[count];
        int tempVar = 0;

        for (int i = 0; i < this.measurementCount; i++) {
            // Repetimos o MESMO IF para copiar só os que passam no filtro
            if (this.measurements[i].getDate().toLocalDate().equals(date)) {
                measurementByDate[tempVar] = this.measurements[i];
                tempVar++;
            }
        }

        return measurementByDate;
    }

    @Override
    public ItemType getType() {
        return type;
    }
}
