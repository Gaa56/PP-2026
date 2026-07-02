package pt.ipp.estg.pp.pickingManagement;

import com.estg.pickingManagement.Report;
import java.time.LocalDateTime;

public class ReportImpl implements Report {

    private LocalDateTime date;
    private int nonPickedContainers;
    private int notUsedVehicles;
    private int pickedContainers;
    private double totalDistance;
    private double totalDuration;
    private int usedVehicles;

    //Método construtor
    public ReportImpl(LocalDateTime date, int nonPickedContainers, int notUsedVehicles, int pickedContainers,
            double totalDistance, double totalDuration, int usedVehicles) {
        this.date = date;
        this.nonPickedContainers = nonPickedContainers;
        this.notUsedVehicles = notUsedVehicles;
        this.pickedContainers = pickedContainers;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.usedVehicles = usedVehicles;
    }
    
    //Método construtor com a data definida para o exato momento (overloading)
     public ReportImpl(int nonPickedContainers, int notUsedVehicles, int pickedContainers, double totalDistance, double totalDuration, int usedVehicles) {
        this.date = LocalDateTime.now();
        this.nonPickedContainers = nonPickedContainers;
        this.notUsedVehicles = notUsedVehicles;
        this.pickedContainers = pickedContainers;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.usedVehicles = usedVehicles;
    }

    @Override
    public LocalDateTime getDate() {
        return this.date;
    }

    @Override
    public int getNonPickedContainers() {
        return this.nonPickedContainers;
    }

    @Override
    public int getNotUsedVehicles() {
        return this.notUsedVehicles;
    }

    @Override
    public int getPickedContainers() {
        return this.pickedContainers;
    }

    @Override
    public double getTotalDistance() {
        return this.totalDistance;
    }

    @Override
    public double getTotalDuration() {
        return this.totalDuration;
    }

    @Override
    public int getUsedVehicles() {
        return this.usedVehicles;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setNonPickedContainers(int nonPickedContainers) {
        this.nonPickedContainers = nonPickedContainers;
    }

    public void setNotUsedVehicles(int notUsedVehicles) {
        this.notUsedVehicles = notUsedVehicles;
    }

    public void setPickedContainers(int pickedContainers) {
        this.pickedContainers = pickedContainers;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public void setTotalDuration(double totalDuration) {
        this.totalDuration = totalDuration;
    }

    public void setUsedVehicles(int usedVehicles) {
        this.usedVehicles = usedVehicles;
    }


        @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RELATÓRIO DO SISTEMA ===")
          .append("\nData: ").append(this.date)
          .append("\nDistância Total: ").append(this.totalDistance).append(" m")
          .append("\nDuração Total: ").append(this.totalDuration).append(" s")
          .append("\nContentores Recolhidos: ").append(this.pickedContainers)
          .append("\nContentores não recolhidos: ").append(this.nonPickedContainers)
          .append("\nVeículos usados: ").append(this.usedVehicles)
          .append("\nVeículos não usados: ").append(this.notUsedVehicles)
          .append("\n============================");
          
        return sb.toString();
    }

}
