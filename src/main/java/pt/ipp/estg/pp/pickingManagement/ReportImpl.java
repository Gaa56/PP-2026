package pt.ipp.estg.pp.pickingManagement;

import com.estg.pickingManagement.Report;
import java.time.LocalDateTime;

/**
 * Implementação da interface Report.
 * Acumula as estatísticas resultantes da geração de rotas de recolha.
 */
public class ReportImpl implements Report {

    private int usedVehicles;
    private int notUsedVehicles;
    private int pickedContainers;
    private int nonPickedContainers;
    private double totalDistance;
    private double totalDuration;
    private LocalDateTime date;

    /**
     * Construtor por omissão. Inicializa o relatório com a data/hora atual.
     */
    public ReportImpl() {
        this.date = LocalDateTime.now();
    }

    /**
     * Construtor completo para definir todas as estatísticas do relatório.
     *
     * @param usedVehicles        número de veículos utilizados
     * @param notUsedVehicles     número de veículos não utilizados
     * @param pickedContainers    número de contentores recolhidos
     * @param nonPickedContainers número de contentores não recolhidos
     * @param totalDistance       distância total percorrida
     * @param totalDuration       duração total das rotas
     * @param date                data do relatório
     */
    public ReportImpl(int usedVehicles, int notUsedVehicles,
            int pickedContainers, int nonPickedContainers,
            double totalDistance, double totalDuration,
            LocalDateTime date) {
        this.usedVehicles = usedVehicles;
        this.notUsedVehicles = notUsedVehicles;
        this.pickedContainers = pickedContainers;
        this.nonPickedContainers = nonPickedContainers;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.date = date;
    }

    @Override
    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public int getNonPickedContainers() {
        return nonPickedContainers;
    }

    @Override
    public int getNotUsedVehicles() {
        return notUsedVehicles;
    }

    @Override
    public int getPickedContainers() {
        return pickedContainers;
    }

    @Override
    public double getTotalDistance() {
        return totalDistance;
    }

    @Override
    public double getTotalDuration() {
        return totalDuration;
    }

    @Override
    public int getUsedVehicles() {
        return usedVehicles;
    }

    // Setters para permitir a configuração pelo RouteGenerator

    public void setUsedVehicles(int usedVehicles) {
        this.usedVehicles = usedVehicles;
    }

    public void setNotUsedVehicles(int notUsedVehicles) {
        this.notUsedVehicles = notUsedVehicles;
    }

    public void setPickedContainers(int pickedContainers) {
        this.pickedContainers = pickedContainers;
    }

    public void setNonPickedContainers(int nonPickedContainers) {
        this.nonPickedContainers = nonPickedContainers;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public void setTotalDuration(double totalDuration) {
        this.totalDuration = totalDuration;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * Devolve a representação textual do relatório.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ReportImpl{")
                .append("date=").append(date)
                .append(", usedVehicles=").append(usedVehicles)
                .append(", notUsedVehicles=").append(notUsedVehicles)
                .append(", pickedContainers=").append(pickedContainers)
                .append(", nonPickedContainers=").append(nonPickedContainers)
                .append(", totalDistance=").append(totalDistance).append("m")
                .append(", totalDuration=").append(totalDuration).append("s")
                .append("}");
        return sb.toString();
    }
}
