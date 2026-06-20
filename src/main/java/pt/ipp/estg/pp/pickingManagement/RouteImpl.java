package pt.ipp.estg.pp.pickingManagement;

import com.estg.pickingManagement.Route;
import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.pickingManagement.Vehicle;
import com.estg.pickingManagement.exceptions.RouteException;
import com.estg.core.exceptions.AidBoxException;

/**
 * Implementação da interface Route.
 * Representa o percurso de um veículo que parte da Base, visita uma sequência
 * de caixas de suprimentos (AidBox) e regressa à Base.
 */
public class RouteImpl implements Route {

    private Vehicle vehicle;
    private AidBox[] aidBoxes;
    private int boxCount;
    private static final int DEFAULT_SIZE = 10;

    /**
     * Construtor para criar uma rota associada a um veículo.
     *
     * @param vehicle o veículo responsável por esta rota
     */
    public RouteImpl(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.aidBoxes = new AidBox[DEFAULT_SIZE];
        this.boxCount = 0;
    }

    /**
     * Expande o array de AidBoxes quando a capacidade é atingida.
     */
    private void expandArray() {
        AidBox[] newArray = new AidBox[aidBoxes.length * 2];
        System.arraycopy(aidBoxes, 0, newArray, 0, boxCount);
        aidBoxes = newArray;
    }

    /**
     * Encontra o índice de uma AidBox na rota.
     *
     * @param aidBox a AidBox a procurar
     * @return o índice da AidBox, ou -1 se não encontrada
     */
    private int findIndex(AidBox aidBox) {
        for (int i = 0; i < boxCount; i++) {
            if (aidBoxes[i].equals(aidBox)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Verifica se a AidBox é compatível com o veículo desta rota.
     * Uma AidBox é compatível se tiver pelo menos um contentor do mesmo tipo
     * que o veículo transporta.
     *
     * @param aidBox a AidBox a verificar
     * @return true se compatível
     */
    private boolean isCompatible(AidBox aidBox) {
        Container container = aidBox.getContainer(vehicle.getSupplyType());
        return container != null;
    }

    /**
     * Adiciona uma nova AidBox à rota.
     *
     * @param aidBox a AidBox a adicionar
     * @throws RouteException se a AidBox for nula, já existir na rota,
     *                        ou não for compatível com o veículo
     */
    @Override
    public void addAidBox(AidBox aidBox) throws RouteException {
        if (aidBox == null) {
            throw new RouteException("AidBox não pode ser nula");
        }
        if (containsAidBox(aidBox)) {
            throw new RouteException("AidBox já existe na rota");
        }
        if (!isCompatible(aidBox)) {
            throw new RouteException("AidBox não é compatível com o veículo desta rota");
        }

        if (boxCount == aidBoxes.length) {
            expandArray();
        }

        aidBoxes[boxCount] = aidBox;
        boxCount++;
    }

    /**
     * Verifica se a rota contém uma determinada AidBox.
     *
     * @param aidBox a AidBox a verificar
     * @return true se a rota contém a AidBox
     */
    @Override
    public boolean containsAidBox(AidBox aidBox) {
        return findIndex(aidBox) != -1;
    }

    /**
     * Devolve uma cópia do array de AidBoxes da rota.
     *
     * @return cópia do array de AidBoxes
     */
    @Override
    public AidBox[] getRoute() {
        AidBox[] result = new AidBox[boxCount];
        System.arraycopy(aidBoxes, 0, result, 0, boxCount);
        return result;
    }

    /**
     * Calcula a distância total da rota (Base -> AidBox1 -> ... -> AidBoxN -> Base).
     * Utiliza as distâncias registadas entre AidBoxes e da instituição.
     *
     * @return a distância total em metros
     */
    @Override
    public double getTotalDistance() {
        if (boxCount == 0) {
            return 0.0;
        }

        double total = 0.0;

        // Somar distâncias entre caixas consecutivas
        for (int i = 0; i < boxCount - 1; i++) {
            try {
                total += aidBoxes[i].getDistance(aidBoxes[i + 1]);
            } catch (AidBoxException e) {
                // Se não existir ligação direta, ignorar (rota inválida)
            }
        }

        return total;
    }

    /**
     * Calcula a duração total da rota (Base -> AidBox1 -> ... -> AidBoxN -> Base).
     *
     * @return a duração total em segundos
     */
    @Override
    public double getTotalDuration() {
        if (boxCount == 0) {
            return 0.0;
        }

        double total = 0.0;

        // Somar durações entre caixas consecutivas
        for (int i = 0; i < boxCount - 1; i++) {
            try {
                total += aidBoxes[i].getDuration(aidBoxes[i + 1]);
            } catch (AidBoxException e) {
                // Se não existir ligação direta, ignorar
            }
        }

        return total;
    }

    /**
     * Devolve o veículo associado a esta rota.
     *
     * @return o veículo da rota
     */
    @Override
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Insere uma AidBox depois de outra AidBox existente na rota.
     *
     * @param after    a AidBox existente na rota (inserir depois desta)
     * @param toInsert a AidBox a inserir
     * @throws RouteException se alguma AidBox for nula, se a AidBox de referência
     *                        não existir, se a AidBox a inserir já existir,
     *                        ou se não for compatível com o veículo
     */
    @Override
    public void insertAfter(AidBox after, AidBox toInsert) throws RouteException {
        if (after == null || toInsert == null) {
            throw new RouteException("AidBox não pode ser nula");
        }

        int afterIndex = findIndex(after);
        if (afterIndex == -1) {
            throw new RouteException("AidBox de referência não existe na rota");
        }

        if (containsAidBox(toInsert)) {
            throw new RouteException("AidBox a inserir já existe na rota");
        }

        if (!isCompatible(toInsert)) {
            throw new RouteException("AidBox a inserir não é compatível com o veículo desta rota");
        }

        if (boxCount == aidBoxes.length) {
            expandArray();
        }

        // Deslocar elementos para a direita a partir da posição afterIndex + 1
        for (int i = boxCount; i > afterIndex + 1; i--) {
            aidBoxes[i] = aidBoxes[i - 1];
        }

        aidBoxes[afterIndex + 1] = toInsert;
        boxCount++;
    }

    /**
     * Remove uma AidBox da rota.
     *
     * @param aidBox a AidBox a remover
     * @return a AidBox removida
     * @throws RouteException se a AidBox for nula ou não existir na rota
     */
    @Override
    public AidBox removeAidBox(AidBox aidBox) throws RouteException {
        if (aidBox == null) {
            throw new RouteException("AidBox não pode ser nula");
        }

        int index = findIndex(aidBox);
        if (index == -1) {
            throw new RouteException("AidBox não existe na rota");
        }

        AidBox removed = aidBoxes[index];

        // Deslocar elementos para a esquerda
        for (int i = index; i < boxCount - 1; i++) {
            aidBoxes[i] = aidBoxes[i + 1];
        }

        aidBoxes[boxCount - 1] = null;
        boxCount--;

        return removed;
    }

    /**
     * Substitui uma AidBox existente por outra na rota.
     *
     * @param from a AidBox a substituir
     * @param to   a AidBox que vai substituir
     * @throws RouteException se alguma AidBox for nula, se a AidBox a substituir
     *                        não existir, se a AidBox substituta já existir,
     *                        ou se não for compatível com o veículo
     */
    @Override
    public void replaceAidBox(AidBox from, AidBox to) throws RouteException {
        if (from == null || to == null) {
            throw new RouteException("AidBox não pode ser nula");
        }

        int fromIndex = findIndex(from);
        if (fromIndex == -1) {
            throw new RouteException("AidBox a substituir não existe na rota");
        }

        if (containsAidBox(to)) {
            throw new RouteException("AidBox substituta já existe na rota");
        }

        if (!isCompatible(to)) {
            throw new RouteException("AidBox substituta não é compatível com o veículo desta rota");
        }

        aidBoxes[fromIndex] = to;
    }

    /**
     * Devolve a representação textual da rota.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RouteImpl{vehicle=").append(vehicle)
                .append(", aidBoxes=[");
        for (int i = 0; i < boxCount; i++) {
            sb.append(aidBoxes[i].getCode());
            if (i < boxCount - 1) {
                sb.append(" -> ");
            }
        }
        sb.append("], totalDistance=").append(getTotalDistance())
                .append("m, totalDuration=").append(getTotalDuration())
                .append("s}");
        return sb.toString();
    }
}
