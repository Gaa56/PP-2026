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

import pt.ipp.estg.pp.pickingManagement.VehicleImpl;
import pt.ipp.estg.pp.pickingManagement.State;

/**
 * Implementação concreta da interface Institution.
 * Gere veículos, caixas de suprimentos (AidBoxes), mapas de recolha (PickingMaps)
 * e as distâncias entre a instituição e as AidBoxes.
 */
public class InstitutionImpl implements Institution {

    private String name;

    private Vehicle[] vehicles;
    private int numOfVehicles;

    private AidBox[] aidBoxes;
    private int numOfAidBoxes;

    private PickingMap[] pickingMaps;
    private int numOfPickingMaps;

    private AidBox[] connectedBoxes;
    private double[] distances;
    private double[] durations;
    private int connectionCount;

    private static final int DEFAULT_SIZE = 20;

    /**
     * Cria uma nova instituição com o nome fornecido.
     *
     * @param name nome da instituição
     */
    public InstitutionImpl(String name) {
        this.name = name;
        this.vehicles = new Vehicle[DEFAULT_SIZE];
        this.aidBoxes = new AidBox[DEFAULT_SIZE];
        this.pickingMaps = new PickingMap[DEFAULT_SIZE];
        this.numOfVehicles = 0;
        this.numOfAidBoxes = 0;
        this.numOfPickingMaps = 0;

        this.connectedBoxes = new AidBox[DEFAULT_SIZE];
        this.distances = new double[DEFAULT_SIZE];
        this.durations = new double[DEFAULT_SIZE];
        this.connectionCount = 0;
    }

    /**
     * Devolve o nome da instituição.
     *
     * @return o nome da instituição
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Adiciona uma nova caixa de suprimentos (AidBox) à instituição.
     * Valida se não existem contentores duplicados do mesmo tipo na nova AidBox.
     *
     * @param aidBox a AidBox a adicionar
     * @return true se a AidBox foi adicionada com sucesso, false se já existia uma com o mesmo código
     * @throws AidBoxException se a AidBox for nula ou se contiver contentores duplicados do mesmo tipo
     */
    @Override
    public boolean addAidBox(AidBox aidBox) throws AidBoxException {
        if (aidBox == null) {
            throw new AidBoxException("AidBox não pode ser nula");
        }

        // Validar contentores duplicados do mesmo tipo no novo AidBox
        Container[] containers = aidBox.getContainers();
        if (containers != null) {
            for (int i = 0; i < containers.length; i++) {
                if (containers[i] == null) continue;
                for (int j = i + 1; j < containers.length; j++) {
                    if (containers[j] == null) continue;
                    if (containers[i].getType() == containers[j].getType()) {
                        throw new AidBoxException("AidBox inválida: tem contentores duplicados do tipo " + containers[i].getType());
                    }
                }
            }
        }

        // Verificar se a aidbox já existe pelo código
        for (int i = 0; i < numOfAidBoxes; i++) {
            if (aidBoxes[i].getCode().equals(aidBox.getCode())) {
                return false;
            }
        }

        // Expandir o array se necessário
        if (numOfAidBoxes == aidBoxes.length) {
            AidBox[] newAidBoxes = new AidBox[aidBoxes.length * 2];
            for (int i = 0; i < numOfAidBoxes; i++) {
                newAidBoxes[i] = aidBoxes[i];
            }
            aidBoxes = newAidBoxes;
        }

        // Adicionar ao array
        aidBoxes[numOfAidBoxes] = aidBox;
        numOfAidBoxes++;
        return true;
    }

    /**
     * Adiciona uma nova medição à instituição, associada a um contentor.
     * Valida se o contentor existe e se o valor da medição é válido para a sua capacidade.
     *
     * @param measurement a medição a adicionar
     * @param container   o contentor ao qual associar a medição
     * @return true se adicionado com sucesso, false se já existia uma medição para aquela data
     * @throws ContainerException   se o contentor não existir na instituição ou se algum argumento for nulo
     * @throws MeasurementException se o valor da medição for menor que 0 ou superior à capacidade do contentor
     */
    @Override
    public boolean addMeasurement(Measurement measurement, Container container) throws ContainerException, MeasurementException {
        if (measurement == null || container == null) {
            throw new ContainerException("Container ou Measurement não pode ser nulo");
        }

        // Verificar se o container existe na instituição
        boolean containerExists = false;
        for (int i = 0; i < numOfAidBoxes; i++) {
            Container[] boxContainers = aidBoxes[i].getContainers();
            if (boxContainers != null) {
                for (Container c : boxContainers) {
                    if (c != null && c.equals(container)) {
                        containerExists = true;
                        break;
                    }
                }
            }
            if (containerExists) {
                break;
            }
        }

        if (!containerExists) {
            throw new ContainerException("O Container não existe nesta instituição");
        }

        // Validar valor da medição
        if (measurement.getValue() < 0 || measurement.getValue() > container.getCapacity()) {
            throw new MeasurementException("Valor da medição inválido (deve ser entre 0 e a capacidade do contentor)");
        }

        return container.addMeasurement(measurement);
    }

    /**
     * Adiciona um novo mapa de picking (PickingMap) à instituição.
     *
     * @param pickingMap o mapa de picking a adicionar
     * @return true se adicionado com sucesso, false se já existia
     * @throws PickingMapException se o mapa de picking for nulo
     */
    @Override
    public boolean addPickingMap(PickingMap pickingMap) throws PickingMapException {
        if (pickingMap == null) {
            throw new PickingMapException("PickingMap não pode ser nulo");
        }

        // Verificar se o pickingmap já existe
        for (int i = 0; i < numOfPickingMaps; i++) {
            if (pickingMaps[i].equals(pickingMap)) {
                return false;
            }
        }

        // Expandir o array se necessário
        if (numOfPickingMaps == pickingMaps.length) {
            PickingMap[] newPickingMaps = new PickingMap[pickingMaps.length * 2];
            for (int i = 0; i < numOfPickingMaps; i++) {
                newPickingMaps[i] = pickingMaps[i];
            }
            pickingMaps = newPickingMaps;
        }

        // Adicionar ao array
        pickingMaps[numOfPickingMaps] = pickingMap;
        numOfPickingMaps++;
        return true;
    }

    /**
     * Adiciona um novo veículo à instituição.
     *
     * @param vehicle o veículo a adicionar
     * @return true se adicionado com sucesso, false se o veículo já existia
     * @throws VehicleException se o veículo for nulo
     */
    @Override
    public boolean addVehicle(Vehicle vehicle) throws VehicleException {
        if (vehicle == null) {
            throw new VehicleException("Veículo não pode ser nulo");
        }

        // Verificar se o veículo já existe
        for (int i = 0; i < numOfVehicles; i++) {
            if (vehicles[i].equals(vehicle)) {
                return false;
            }
        }

        // Expandir o array se necessário
        if (numOfVehicles == vehicles.length) {
            Vehicle[] newVehicles = new Vehicle[vehicles.length * 2];
            for (int i = 0; i < numOfVehicles; i++) {
                newVehicles[i] = vehicles[i];
            }
            vehicles = newVehicles;
        }

        // Adicionar ao array
        vehicles[numOfVehicles] = vehicle;
        numOfVehicles++;
        return true;
    }

    /**
     * Desativa um veículo na instituição.
     *
     * @param vehicle o veículo a desativar
     * @throws VehicleException se o veículo não existir ou se já estiver desativado
     */
    @Override
    public void disableVehicle(Vehicle vehicle) throws VehicleException {
        if (vehicle == null) {
            throw new VehicleException("Veículo não pode ser nulo");
        }

        // Procurar o veículo
        VehicleImpl temp = null;
        for (int i = 0; i < numOfVehicles; i++) {
            if (vehicles[i].equals(vehicle)) {
                temp = (VehicleImpl) vehicles[i];
                break;
            }
        }

        // Se for null, o veículo não existe na instituição
        if (temp == null) {
            throw new VehicleException("Veículo não existe na instituição");
        }

        // Verificar se já está desativado
        if (temp.getState() == State.DISABLE) {
            throw new VehicleException("Veículo já está desativado");
        }

        // Desativar
        temp.setStateDisable();
    }

    /**
     * Ativa um veículo na instituição.
     *
     * @param vehicle o veículo a ativar
     * @throws VehicleException se o veículo não existir ou se já estiver ativo
     */
    @Override
    public void enableVehicle(Vehicle vehicle) throws VehicleException {
        if (vehicle == null) {
            throw new VehicleException("Veículo não pode ser nulo");
        }

        // Procurar o veículo
        VehicleImpl temp = null;
        for (int i = 0; i < numOfVehicles; i++) {
            if (vehicles[i].equals(vehicle)) {
                temp = (VehicleImpl) vehicles[i];
                break;
            }
        }

        // Se for null, o veículo não existe na instituição
        if (temp == null) {
            throw new VehicleException("Veículo não existe na instituição");
        }

        // Verificar se já está ativo
        if (temp.getState() == State.ACTIVE) {
            throw new VehicleException("Veículo já está ativo");
        }

        // Ativar
        temp.setStateActive();
    }

    /**
     * Devolve uma cópia do array de caixas de suprimentos (AidBoxes) da instituição.
     *
     * @return cópia do array de caixas de suprimentos
     */
    @Override
    public AidBox[] getAidBoxes() {
        AidBox[] result = new AidBox[numOfAidBoxes];
        for (int i = 0; i < numOfAidBoxes; i++) {
            result[i] = aidBoxes[i];
        }
        return result;
    }

    /**
     * Devolve um contentor associado a uma AidBox e de um tipo específico.
     *
     * @param aidBox   a AidBox a pesquisar
     * @param itemType o tipo de contentor pretendido
     * @return o contentor correspondente
     * @throws ContainerException se a AidBox não existir na instituição ou se o contentor não existir
     */
    @Override
    public Container getContainer(AidBox aidBox, ItemType itemType) throws ContainerException {
        if (aidBox == null || itemType == null) {
            throw new ContainerException("AidBox ou ItemType não pode ser nulo");
        }

        // Verificar se o AidBox existe na instituição
        boolean aidBoxExists = false;
        for (int i = 0; i < numOfAidBoxes; i++) {
            if (aidBoxes[i].getCode().equals(aidBox.getCode())) {
                aidBoxExists = true;
                break;
            }
        }

        if (!aidBoxExists) {
            throw new ContainerException("AidBox não existe nesta instituição");
        }

        Container container = aidBox.getContainer(itemType);
        if (container == null) {
            throw new ContainerException("Contentor do tipo " + itemType + " não existe na AidBox");
        }

        return container;
    }

    /**
     * Devolve o mapa de picking atual (mais recente com base na data/hora).
     *
     * @return o mapa de picking mais recente
     * @throws PickingMapException se não existirem mapas de picking na instituição
     */
    @Override
    public PickingMap getCurrentPickingMap() throws PickingMapException {
        if (numOfPickingMaps == 0) {
            throw new PickingMapException("Não existem mapas de picking na instituição");
        }

        // Encontrar o mapa de picking mais recente (com a data mais avançada)
        PickingMap mostRecent = pickingMaps[0];
        for (int i = 1; i < numOfPickingMaps; i++) {
            if (pickingMaps[i].getDate().isAfter(mostRecent.getDate())) {
                mostRecent = pickingMaps[i];
            }
        }
        return mostRecent;
    }

    /**
     * Devolve a distância da instituição até a uma AidBox específica.
     *
     * @param aidBox a AidBox de destino
     * @return a distância em metros
     * @throws AidBoxException se a distância para a AidBox fornecida não estiver registada
     */
    @Override
    public double getDistance(AidBox aidBox) throws AidBoxException {
        if (aidBox == null) {
            throw new AidBoxException("AidBox não pode ser nula");
        }

        for (int i = 0; i < connectionCount; i++) {
            if (connectedBoxes[i].equals(aidBox)) {
                return distances[i];
            }
        }
        throw new AidBoxException("Distância até à AidBox com código " + aidBox.getCode() + " não encontrada");
    }

    /**
     * Regista a distância da instituição até a uma AidBox específica.
     * Método auxiliar útil para uso pelo Importer.
     *
     * @param destination AidBox de destino
     * @param distance    distância em metros
     */
    public void addDistance(AidBox destination, double distance) {
        addDistance(destination, distance, 0.0);
    }

    /**
     * Regista a distância e duração da instituição até a uma AidBox específica.
     *
     * @param destination AidBox de destino
     * @param distance    distância em metros
     * @param duration    duração em segundos
     */
    public void addDistance(AidBox destination, double distance, double duration) {
        if (connectionCount == connectedBoxes.length) {
            AidBox[] newBoxes = new AidBox[connectedBoxes.length * 2];
            double[] newDistances = new double[distances.length * 2];
            double[] newDurations = new double[durations.length * 2];
            for (int i = 0; i < connectionCount; i++) {
                newBoxes[i] = connectedBoxes[i];
                newDistances[i] = distances[i];
                newDurations[i] = durations[i];
            }
            connectedBoxes = newBoxes;
            distances = newDistances;
            durations = newDurations;
        }
        connectedBoxes[connectionCount] = destination;
        distances[connectionCount] = distance;
        durations[connectionCount] = duration;
        connectionCount++;
    }

    /**
     * Devolve a duração estimada em segundos da instituição até a uma AidBox específica.
     *
     * @param aidBox a AidBox de destino
     * @return a duração em segundos
     * @throws AidBoxException se a duração não estiver registada
     */
    public double getDuration(AidBox aidBox) throws AidBoxException {
        if (aidBox == null) {
            throw new AidBoxException("AidBox não pode ser nula");
        }
        for (int i = 0; i < connectionCount; i++) {
            if (connectedBoxes[i].equals(aidBox)) {
                return durations[i];
            }
        }
        throw new AidBoxException("Duração até à AidBox com código " + aidBox.getCode() + " não encontrada");
    }

    /**
     * Devolve todos os mapas de picking registados na instituição.
     *
     * @return array contendo os mapas de picking
     */
    @Override
    public PickingMap[] getPickingMaps() {
        PickingMap[] result = new PickingMap[numOfPickingMaps];
        for (int i = 0; i < numOfPickingMaps; i++) {
            result[i] = pickingMaps[i];
        }
        return result;
    }

    /**
     * Devolve os mapas de picking cujas datas estejam dentro do intervalo especificado (inclusivo).
     *
     * @param from data de início
     * @param to   data de fim
     * @return array de mapas de picking no intervalo indicado
     */
    @Override
    public PickingMap[] getPickingMaps(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            return new PickingMap[0];
        }

        // Contar correspondências
        int count = 0;
        for (int i = 0; i < numOfPickingMaps; i++) {
            LocalDateTime mapDate = pickingMaps[i].getDate();
            if ((mapDate.isAfter(from) || mapDate.isEqual(from)) &&
                    (mapDate.isBefore(to) || mapDate.isEqual(to))) {
                count++;
            }
        }

        // Preencher array
        PickingMap[] result = new PickingMap[count];
        int index = 0;
        for (int i = 0; i < numOfPickingMaps; i++) {
            LocalDateTime mapDate = pickingMaps[i].getDate();
            if ((mapDate.isAfter(from) || mapDate.isEqual(from)) &&
                    (mapDate.isBefore(to) || mapDate.isEqual(to))) {
                result[index++] = pickingMaps[i];
            }
        }
        return result;
    }

    /**
     * Devolve uma cópia do array contendo os veículos associados à instituição.
     *
     * @return cópia do array de veículos
     */
    @Override
    public Vehicle[] getVehicles() {
        Vehicle[] result = new Vehicle[numOfVehicles];
        for (int i = 0; i < numOfVehicles; i++) {
            result[i] = vehicles[i];
        }
        return result;
    }
}
