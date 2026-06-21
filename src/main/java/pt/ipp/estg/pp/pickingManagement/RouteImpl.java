package pt.ipp.estg.pp.pickingManagement;

import com.estg.pickingManagement.Route;
import com.estg.core.AidBox;
import com.estg.pickingManagement.Vehicle;
import com.estg.pickingManagement.exceptions.RouteException;

/**
 * Implementação concreta da interface {@link Route}.
 * Representa uma rota de recolha de bens associada a um veículo, contendo
 * uma sequência ordenada de caixas de ajuda (AidBoxes) que serão visitadas.
 */
public class RouteImpl implements Route {

    /** Veículo encarregue de realizar a rota de recolha. */
    private Vehicle vehicle;

    /** Array que armazena a sequência de caixas de ajuda que compõem a rota. */
    private AidBox[] aidBoxes;

    /** Número atual de caixas de ajuda registadas na rota. */
    private int numOfAidBoxes;

    /** Distância total da rota. */
    private double totalDistance;

    /** Duração total estimada para percorrer a rota. */
    private double totalDuration;

    /** Capacidade inicial padrão do array de caixas de ajuda. */
    private static final int DEFAULT_SIZE = 20;
    
    /**
     * Construtor para criar uma instância de uma rota associada a um veículo.
     *
     * @param vehicle o veículo associado a esta rota
     */
    public RouteImpl(Vehicle vehicle){
        this.vehicle = vehicle;
        this.aidBoxes = new AidBox[DEFAULT_SIZE];
        this.numOfAidBoxes = 0;
        this.totalDistance = 0.0;
        this.totalDuration = 0.0;
    }

    /**
     * Adiciona uma nova caixa de ajuda ao final da rota.
     * 
     * @param arg0 a caixa de ajuda (AidBox) a adicionar à rota
     * @throws RouteException se a caixa for nula ou se já existir na rota
     */
    @Override
    public void addAidBox(AidBox arg0) throws RouteException {
       // Verificar se a caixa de ajuda fornecida é nula
       if(arg0 == null){
           throw new RouteException("AidBox não pode ser nula");
       }
 
       // Verificar se a caixa de ajuda já se encontra na rota (evitar duplicados)
       if(containsAidBox(arg0)){
           throw new RouteException("AidBox já existe na rota");
       }

       // Expandir a capacidade do array interno se este estiver cheio
       if(numOfAidBoxes == aidBoxes.length){
           AidBox[] newAidBoxes = new AidBox[aidBoxes.length * 2];
           for(int i = 0; i < numOfAidBoxes; i++){
               newAidBoxes[i] = aidBoxes[i];
           }
           aidBoxes = newAidBoxes;
       }

       // Adicionar a caixa ao final da rota e incrementar o contador
       aidBoxes[numOfAidBoxes] = arg0;
       numOfAidBoxes++;
    }

    /**
     * Verifica se uma determinada caixa de ajuda já se encontra na rota.
     *
     * @param arg0 a caixa de ajuda a pesquisar
     * @return true se a caixa estiver na rota, false caso contrário (ou se for nula)
     */
    @Override
    public boolean containsAidBox(AidBox arg0){
        if(arg0 == null){
            return false;
        }
        // Percorrer a rota para verificar igualdade com a caixa fornecida
        for(int i = 0; i < numOfAidBoxes; i++){
            if(aidBoxes[i].equals(arg0)){
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna uma cópia ordenada do array de caixas de ajuda que constituem a rota.
     *
     * @return um array de {@link AidBox} contendo as caixas de ajuda na ordem da rota
     */
    @Override
    public AidBox[] getRoute() {
        // Retorna uma cópia contendo apenas as posições preenchidas do array
        AidBox[] route = new AidBox[numOfAidBoxes];
        for(int i = 0; i < numOfAidBoxes; i++){
            route[i] = aidBoxes[i];
        }
        return route;
    }

    /**
     * Devolve a distância total percorrida na rota.
     *
     * @return a distância total
     */
    @Override
    public double getTotalDistance() {
        return this.totalDistance;
    }

    /**
     * Devolve a duração total gasta no trajeto da rota.
     *
     * @return a duração total
     */
    @Override
    public double getTotalDuration() {
        return this.totalDuration;
    }

    /**
     * Retorna o veículo associado a esta rota.
     *
     * @return o veículo da rota
     */
    @Override
    public Vehicle getVehicle() {
       return this.vehicle;
    }

    /**
     * Insere uma nova caixa de ajuda na rota imediatamente a seguir a uma outra de referência.
     *
     * @param arg0 a caixa de ajuda de referência que já existe na rota
     * @param arg1 a nova caixa de ajuda a ser inserida a seguir
     * @throws RouteException se qualquer caixa for nula, se a nova já existir, ou se a de referência não for encontrada
     */
    @Override
    public void insertAfter(AidBox arg0, AidBox arg1) throws RouteException {
        if(arg0 == null || arg1 == null){
            throw new RouteException("AidBox não pode ser nula");
        }

        // Verificar se a nova caixa já existe na rota (evitar duplicados)
        if(containsAidBox(arg1)){
            throw new RouteException("AidBox já existe na rota");
        }

        // Expandir a capacidade do array interno se este estiver cheio
        if(numOfAidBoxes == aidBoxes.length){
            AidBox[] newAidBoxes = new AidBox[aidBoxes.length * 2];
            for(int i = 0; i < numOfAidBoxes; i++){
                newAidBoxes[i] = aidBoxes[i];
            }
            aidBoxes = newAidBoxes;
        }

        // Procurar o índice da caixa de referência (arg0)
        int index = -1;
        for( int i = 0; i < numOfAidBoxes; i++){
            if(aidBoxes[i].equals(arg0)){
                index = i;
                break;
            }
        }

        // Caso a caixa de referência não seja encontrada
        if(index == -1){
            throw new RouteException("AidBox não encontrada na rota");
        }

        // Deslocar os elementos seguintes para a direita (de trás para a frente)
        for (int i = numOfAidBoxes; i > index + 1; i--) {
            // Move a caixa atual para a posição à direita, libertando espaço
            aidBoxes[i] = aidBoxes[i - 1];
        }

        // Adicionar a nova caixa na posição libertada e incrementar o contador
        aidBoxes[index + 1] = arg1;
        numOfAidBoxes++;
    }

    /**
     * Remove uma caixa de ajuda da rota e reorganiza os elementos restantes.
     *
     * @param arg0 a caixa de ajuda a remover
     * @return a caixa de ajuda que foi removida da rota
     * @throws RouteException se a caixa for nula ou se não existir na rota
     */
    @Override
    public AidBox removeAidBox(AidBox arg0) throws RouteException {
        if (arg0 == null) {
            throw new RouteException("AidBox não pode ser nula");
        }

        // Procurar o índice da caixa a ser removida
        int index = -1;
        for (int i = 0; i < numOfAidBoxes; i++) {
            if (aidBoxes[i].equals(arg0)) {
                index = i;
                break;
            }
        }

        // Se a caixa não foi encontrada na rota, lança exceção
        if (index == -1) {
            throw new RouteException("AidBox não encontrada na rota");
        }

        // Guardar referência da caixa removida para retorno
        AidBox removedBox = aidBoxes[index];

        // Deslocar elementos seguintes uma posição para a esquerda (da frente para trás)
        for (int i = index; i < numOfAidBoxes - 1; i++) {
            aidBoxes[i] = aidBoxes[i + 1];
        }

        // Limpar a última posição preenchida e decrementar o contador
        aidBoxes[numOfAidBoxes - 1] = null;
        numOfAidBoxes--;

        return removedBox;
    }

    /**
     * Substitui uma caixa de ajuda existente na rota por uma nova caixa na mesma posição.
     *
     * @param arg0 a caixa de ajuda a substituir
     * @param arg1 a nova caixa de ajuda que tomará o seu lugar
     * @throws RouteException se qualquer uma das caixas for nula ou se a de referência não existir
     */
    @Override
    public void replaceAidBox(AidBox arg0, AidBox arg1) throws RouteException {
       if(arg0 == null || arg1 == null){
            throw new RouteException("AidBox não pode ser nula");
       }

       // Procurar a posição da caixa de ajuda que vai ser substituída
       int index = -1;
       for(int i = 0; i < numOfAidBoxes; i++){
         if(aidBoxes[i].equals(arg0)){
            index = i;
            break;
         }
       }
    
       // Se a caixa de referência não for encontrada na rota
       if(index == -1){
           throw new RouteException("AidBox não encontrada");
       }

       // Substituir a caixa no mesmo índice (o tamanho da rota não se altera)
       aidBoxes[index] = arg1;
    }
}
