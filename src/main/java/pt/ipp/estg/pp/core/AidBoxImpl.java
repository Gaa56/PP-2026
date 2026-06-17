package pt.ipp.estg.pp.core;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.GeographicCoordinates;
import com.estg.core.ItemType;
import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;

public class AidBoxImpl implements AidBox {

    private String code;
    private Container[] containers;
    private GeographicCoordinates coordinates;
    private String refLocal;
    private String zone;
    private int containerCount;

    private AidBox[] connectedBoxes;
    private double[] distances;
    private double[] durations;
    private int connectionCount;

    /**
     * Construtor da classe AidBoxImpl.
     * 
     * @param code          código único da caixa de suprimentos
     * @param coordinates   coordenadas geográficas da caixa
     * @param refLocal      referência local da caixa
     * @param zone          zona onde a caixa está localizada
     * @param maxContainers número máximo de contentores que a caixa pode conter
     */
    public AidBoxImpl(String code, GeographicCoordinates coordinates,
            String refLocal, String zone, int maxContainers) {
        this.code = code;
        this.coordinates = coordinates;
        this.refLocal = refLocal;
        this.zone = zone;
        this.containers = new Container[maxContainers];
        this.containerCount = 0;

        this.connectedBoxes = new AidBox[10];
        this.distances = new double[10];
        this.durations = new double[10];
        this.connectionCount = 0;
    }

    /**
     * Adiciona uma ligação para outra AidBox com a distância e duração respetivas.
     * Deve ser usado durante a leitura do Distances.json.
     *
     * @param destination AidBox de destino
     * @param distance    Distância em metros
     * @param duration    Duração em segundos
     */
    public void addDistance(AidBox destination, double distance, double duration) {
        if (connectionCount == connectedBoxes.length) {
            AidBox[] newBoxes = new AidBox[connectedBoxes.length * 2];
            double[] newDist = new double[distances.length * 2];
            double[] newDur = new double[durations.length * 2];

            System.arraycopy(connectedBoxes, 0, newBoxes, 0, connectionCount);
            System.arraycopy(distances, 0, newDist, 0, connectionCount);
            System.arraycopy(durations, 0, newDur, 0, connectionCount);

            connectedBoxes = newBoxes;
            distances = newDist;
            durations = newDur;
        }

        connectedBoxes[connectionCount] = destination;
        distances[connectionCount] = distance;
        durations[connectionCount] = duration;
        connectionCount++;
    }

    @Override
    public boolean addContainer(Container container) throws ContainerException {
        if (container == null) {
            throw new ContainerException("Container is null");
        }

        for (int i = 0; i < containerCount; i++) {
            if (containers[i].getType().equals(container.getType())) {
                throw new ContainerException("Já existe um contentor deste tipo");
            }
        }

        if (containerCount < containers.length) {
            containers[containerCount] = container;
            containerCount++;
            return true;
        }

        return false;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Container getContainer(ItemType itemType) {
        for (int i = 0; i < containerCount; i++) {
            if (containers[i].getType().equals(itemType)) {
                return containers[i];
            }
        }
        return null;
    }

    @Override
    public Container[] getContainers() {
        Container[] result = new Container[containerCount];
        System.arraycopy(containers, 0, result, 0, containerCount);
        return result;
    }

    @Override
    public GeographicCoordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public double getDistance(AidBox aidBox) throws AidBoxException {
        if (aidBox == null) {
            throw new AidBoxException("AidBox is null");
        }

        for (int i = 0; i < connectionCount; i++) {
            if (connectedBoxes[i].equals(aidBox)) {
                return distances[i];
            }
        }
        throw new AidBoxException("Distância não encontrada para a AidBox fornecida.");
    }

    @Override
    public double getDuration(AidBox aidBox) throws AidBoxException {
        if (aidBox == null) {
            throw new AidBoxException("AidBox is null");
        }

        for (int i = 0; i < connectionCount; i++) {
            if (connectedBoxes[i].equals(aidBox)) {
                return durations[i];
            }
        }
        throw new AidBoxException("Duração não encontrada para a AidBox fornecida.");
    }

    @Override
    public String getRefLocal() {
        return refLocal;
    }

    @Override
    public String getZone() {
        return zone;
    }

    @Override
    public String toString() {
        return "AidBoxImpl{" +
                "code='" + code + '\'' +
                ", zone='" + zone + '\'' +
                ", refLocal='" + refLocal + '\'' +
                ", containerCount=" + containerCount +
                ", coordinates=" + coordinates +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AidBoxImpl clone = (AidBoxImpl) super.clone();
        
        // Deep copy do array de containers
        clone.containers = new Container[containers.length];
        clone.containerCount = containerCount;

        for (int i = 0; i < containerCount; i++) {
            if (containers[i] != null) {
                // Nem todas as interfaces base podem expor public clone(), então ficamos com shallow copy do objecto
                // se não nos for garantido acesso a método clone() do contentor.
                clone.containers[i] = containers[i];
            }
        }

        clone.coordinates = coordinates;

        clone.connectedBoxes = new AidBox[connectedBoxes.length];
        System.arraycopy(connectedBoxes, 0, clone.connectedBoxes, 0, connectionCount);

        clone.distances = new double[distances.length];
        System.arraycopy(distances, 0, clone.distances, 0, connectionCount);

        clone.durations = new double[durations.length];
        System.arraycopy(durations, 0, clone.durations, 0, connectionCount);

        clone.connectionCount = connectionCount;

        return clone;
    }
}
