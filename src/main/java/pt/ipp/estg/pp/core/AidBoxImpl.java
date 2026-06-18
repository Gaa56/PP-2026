package pt.ipp.estg.pp.core;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.GeographicCoordinates;
import com.estg.core.ItemType;
import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;

/**
 * Representa uma caixa de suprimentos (AidBox) com contentores, coordenadas
 * geográficas e ligações a outras caixas com distâncias e durações associadas.
 */
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
     * Cria uma AidBox com todos os parâmetros definidos, incluindo a capacidade
     * máxima de contentores.
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
     * Cria uma AidBox com capacidade máxima de contentores por omissão (10).
     *
     * @param code        código único da caixa de suprimentos
     * @param coordinates coordenadas geográficas da caixa
     * @param refLocal    referência local da caixa
     * @param zone        zona onde a caixa está localizada
     */
    public AidBoxImpl(String code, GeographicCoordinates coordinates,
            String refLocal, String zone) {
        this(code, coordinates, refLocal, zone, 10);
    }

    /**
     * Expande para o dobro os arrays internos de ligações quando a capacidade
     * atual é atingida.
     */
    private void expandConnections() {
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

    /**
     * Regista uma ligação para outra AidBox com a distância e duração respetivas.
     * Deve ser usado durante a leitura do Distances.json.
     *
     * @param destination AidBox de destino
     * @param distance    distância em metros
     * @param duration    duração em segundos
     */
    public void addDistance(AidBox destination, double distance, double duration) {
        if (connectionCount == connectedBoxes.length) {
            expandConnections();
        }

        connectedBoxes[connectionCount] = destination;
        distances[connectionCount] = distance;
        durations[connectionCount] = duration;
        connectionCount++;
    }

    /**
     * Adiciona um contentor a esta AidBox. Não são permitidos contentores
     * duplicados nem dois contentores do mesmo tipo na mesma AidBox.
     *
     * @param container contentor a adicionar
     * @return {@code true} se adicionado com sucesso; {@code false} se já existia
     *         ou a capacidade máxima foi atingida
     * @throws ContainerException se {@code container} for {@code null} ou já
     *                            existir um contentor do mesmo tipo nesta AidBox
     */
    @Override
    public boolean addContainer(Container container) throws ContainerException {
        if (container == null) {
            throw new ContainerException("Container is null");
        }

        for (int i = 0; i < containerCount; i++) {
            if (containers[i].equals(container)) {
                return false;
            }
            if (containers[i].getType().equals(container.getType())) {
                throw new ContainerException("Já existe um contentor deste tipo nesta AidBox");
            }
        }

        if (containerCount < containers.length) {
            containers[containerCount] = container;
            containerCount++;
            return true;
        }

        return false;
    }

    /**
     * Devolve o código desta AidBox.
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * Devolve o contentor do tipo indicado, ou {@code null} se não existir.
     */
    @Override
    public Container getContainer(ItemType itemType) {
        for (int i = 0; i < containerCount; i++) {
            if (containers[i].getType().equals(itemType)) {
                return containers[i];
            }
        }
        return null;
    }

    /**
     * Devolve uma cópia do array de contentores desta AidBox.
     */
    @Override
    public Container[] getContainers() {
        Container[] result = new Container[containerCount];
        System.arraycopy(containers, 0, result, 0, containerCount);
        return result;
    }

    /**
     * Devolve as coordenadas geográficas desta AidBox.
     */
    @Override
    public GeographicCoordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Devolve a distância em metros até à AidBox indicada.
     *
     * @param aidBox AidBox de destino
     * @return distância em metros
     * @throws AidBoxException se {@code aidBox} for {@code null} ou não existir
     *                         ligação registada para essa AidBox
     */
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

    /**
     * Devolve a duração estimada em segundos do trajeto até à AidBox indicada.
     *
     * @param aidBox AidBox de destino
     * @return duração em segundos
     * @throws AidBoxException se {@code aidBox} for {@code null} ou não existir
     *                         ligação registada para essa AidBox
     */
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

    /**
     * Devolve a referência local desta AidBox.
     */
    @Override
    public String getRefLocal() {
        return refLocal;
    }

    /**
     * Devolve a zona onde esta AidBox está localizada.
     */
    @Override
    public String getZone() {
        return zone;
    }

    /**
     * Devolve a AidBox representada em texto, usando StringBuilder.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AidBoxImpl{")
                .append("code='" + code + '\'')
                .append(", zone='" + zone + '\'')
                .append(", refLocal='" + refLocal + '\'')
                .append(", containerCount=" + containerCount)
                .append(", coordinates=" + coordinates)
                .append("}");
        return sb.toString();
    }

    /**
     * Cria e devolve uma cópia desta AidBox. Os arrays internos são copiados para
     * novos arrays (deep copy estrutural), garantindo independência entre o
     * original e o clone. Os objetos referenciados dentro dos arrays são copiados
     * por referência (shallow copy), dado que as interfaces base não garantem
     * acesso a um método {@code clone()} próprio.
     *
     * @return uma nova instância de {@code AidBoxImpl} com os mesmos dados
     * @throws CloneNotSupportedException se a clonagem não for suportada pela
     *                                    superclasse
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        AidBoxImpl clone = (AidBoxImpl) super.clone();

        clone.containers = new Container[containers.length];
        clone.containerCount = containerCount;

        for (int i = 0; i < containerCount; i++) {
            if (containers[i] != null) {
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