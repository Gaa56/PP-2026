package pt.ipp.estg.pp.core;

public class AidBoxClass implements AidBox {

    private String code;
    private Container[] containers;
    private GeographicCoordinates coordinates;
    private String refLocal;
    private String zone;
    private int containerCount;

    /**
     * Construtor da classe AidBoxClass.
     * 
     * @param code          código único da caixa de suprimentos
     * @param coordinates   coordenadas geográficas da caixa
     * @param refLocal      referência local da caixa
     * @param zone          zona onde a caixa está localizada
     * @param maxContainers número máximo de contentores que a caixa pode conter
     */
    public AidBoxClass(String code, GeographicCoordinates coordinates,
            String refLocal, String zone, int maxContainers) {
        this.code = code;
        this.coordinates = coordinates;
        this.refLocal = refLocal;
        this.zone = zone;
        this.containers = new Container[maxContainers];
        this.containerCount = 0;
    }

    /**
     * Adiciona um contentor à caixa de suprimentos.
     * 
     * @param container o contentor a adicionar
     * @return true se o contentor foi adicionado com sucesso, false caso contrário
     */
    @Override
    public boolean addContainer(Container container) {
        if (container == null) {
            return false;
        }

        // Verificar se já existe um contentor do mesmo tipo
        for (int i = 0; i < containerCount; i++) {
            if (containers[i].getItemType().equals(container.getItemType())) {
                return false; // Já existe um contentor deste tipo
            }
        }

        // Verificar se há espaço
        if (containerCount < containers.length) {
            containers[containerCount] = container;
            containerCount++;
            return true;
        }

        return false;
    }

    /**
     * Obtém o código da caixa de suprimentos.
     * 
     * @return o código da caixa
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * Obtém um contentor específico pelo tipo de item.
     * 
     * @param itemType o tipo de item procurado
     * @return o contentor encontrado, ou null se não existir
     */
    @Override
    public Container getContainer(ItemType itemType) {
        for (int i = 0; i < containerCount; i++) {
            if (containers[i].getItemType().equals(itemType)) {
                return containers[i];
            }
        }
        return null;
    }

    /**
     * Obtém todos os contentores da caixa.
     * 
     * @return array com os contentores (apenas os válidos)
     */
    @Override
    public Container[] getContainers() {
        Container[] result = new Container[containerCount];
        System.arraycopy(containers, 0, result, 0, containerCount);
        return result;
    }

    /**
     * Obtém as coordenadas geográficas da caixa de suprimentos.
     * 
     * @return as coordenadas geográficas
     */
    @Override
    public GeographicCoordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Calcula a distância até outra caixa de suprimentos.
     * 
     * @param aidBox a outra caixa de suprimentos
     * @return a distância em metros
     */
    @Override
    public double getDistance(AidBox aidBox) {
        if (aidBox == null || aidBox.getCoordinates() == null) {
            return 0;
        }

        return coordinates.distance(aidBox.getCoordinates());
    }

    /**
     * Calcula a duração da viagem até outra caixa de suprimentos.
     * 
     * @param aidBox a outra caixa de suprimentos
     * @return a duração em segundos
     */
    @Override
    public double getDuration(AidBox aidBox) {
        if (aidBox == null || aidBox.getCoordinates() == null) {
            return 0;
        }

        return coordinates.duration(aidBox.getCoordinates());
    }

    /**
     * Obtém a referência local da caixa de suprimentos.
     * 
     * @return a referência local
     */
    @Override
    public String getRefLocal() {
        return refLocal;
    }

    /**
     * Obtém a zona onde a caixa de suprimentos está localizada.
     * 
     * @return a zona
     */
    @Override
    public String getZone() {
        return zone;
    }

    /**
     * Retorna uma representação em String da caixa de suprimentos.
     * 
     * @return representação em String
     */
    @Override
    public String toString() {
        return "AidBoxClass{" +
                "code='" + code + '\'' +
                ", zone='" + zone + '\'' +
                ", refLocal='" + refLocal + '\'' +
                ", containerCount=" + containerCount +
                ", coordinates=" + coordinates +
                '}';
    }

    /**
     * Cria um clone da caixa de suprimentos.
     * 
     * @return clone desta caixa
     * @throws CloneNotSupportedException se a clonagem não for suportada
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        AidBoxClass clone = (AidBoxClass) super.clone();
        clone.containers = new Container[containers.length];
        clone.containerCount = containerCount;

        for (int i = 0; i < containerCount; i++) {
            if (containers[i] != null) {
                clone.containers[i] = (Container) containers[i].clone();
            }
        }

        if (coordinates != null) {
            clone.coordinates = (GeographicCoordinates) coordinates.clone();
        }

        return clone;
    }
}
