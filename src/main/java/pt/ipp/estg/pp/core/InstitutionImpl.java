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

public class InstitutionImpl implements Institution {


    private String name;
      

    private Vehicle[] vehicles;
    private int numOfVehicles;
    
    private AidBox[] aidBoxes;
    private int numOfAidBoxes;

    private PickingMap[] pickingMaps;
    private int numOfPickingMaps;

    private static final int DEFAULT_SIZE = 20;
 

    public InstitutionImpl(String name) {
        this.name = name;
        this.vehicles = new Vehicle[DEFAULT_SIZE];
        this.aidBoxes = new AidBox[DEFAULT_SIZE];
        this.pickingMaps = new PickingMap[DEFAULT_SIZE];
        this.numOfVehicles = 0;
        this.numOfAidBoxes = 0;
        this.numOfPickingMaps = 0;
    }

    @Override
    public boolean addAidBox(AidBox arg0) throws AidBoxException {
        if(arg0 == null){
           throw new AidBoxException("AidBox não pode ser nula");
        }

        //Verificar se a aidbox ja existe pelo código
        for(int i = 0; i < numOfAidBoxes;i++){
            if(aidBoxes[i].getCode().equals(arg0.getCode())){
                return false;
            }
        }

        //Expandir o array
        if(numOfAidBoxes == aidBoxes.length){
            AidBox[] newAidBoxes = new AidBox[aidBoxes.length * 2];
            //Passar as aidBoxes para o novo array
            for(int i = 0;i < aidBoxes.length;i++ ){
                newAidBoxes[i] = aidBoxes[i];
            }
            //Atualizar a variável aidBoxes com o novo array
            aidBoxes = newAidBoxes;
        }        

        //Adicionar ao array
        aidBoxes[numOfAidBoxes] = arg0;
        numOfAidBoxes++;
        return true;
        
    }

    @Override
    public boolean addMeasurement(Measurement arg0, Container arg1) throws ContainerException, MeasurementException {
        if(arg0 == null || arg1 == null){
           throw new ContainerException("Container ou Measurement não pode ser nulo");
        }

        //Verificar se o container existe na aidbox
        boolean containerExists = false;
        for(int i = 0; i < numOfAidBoxes;i++){
            if(aidBoxes[i].getContainers() != null){
                for(int j = 0; j < aidBoxes[i].getContainers().length;j++){
                    if(aidBoxes[i].getContainers()[j].equals(arg1)){
                        containerExists = true;
                        break;
                    }
                }
            }
            if(containerExists){
                break;
            }
        }

        //Se a variavel containerExists ainda for falso, é pk nao existe
        if(!containerExists){
           throw new ContainerException("Container não existe");
        }

        return arg1.addMeasurement(arg0);
    }

    @Override
    public boolean addPickingMap(PickingMap arg0) throws PickingMapException {
        if(arg0 == null){
           throw new PickingMapException("PickingMap não pode ser nulo");
        }

        //Verificar se o pickingmap ja existe pelo código
        for(int i = 0; i < numOfPickingMaps;i++){
            if(pickingMaps[i].equals(arg0)){
                return false;
            }
        }

        //Expandir o array
        if(numOfPickingMaps == pickingMaps.length){
            PickingMap[] newPickingMaps = new PickingMap[pickingMaps.length * 2];
            for(int i = 0;i < pickingMaps.length;i++){
                newPickingMaps[i] = pickingMaps[i];
            }
            pickingMaps = newPickingMaps;
        }

        //Adicionar ao array
        pickingMaps[numOfPickingMaps] = arg0;
        numOfPickingMaps++;
        return true;
    }

    @Override
    public boolean addVehicle(Vehicle arg0) throws VehicleException {
        if(arg0 == null){
            throw new VehicleException("Veiculo nao pode ser nulo");
        }

        //Verificar se o veiculo ja existe
        for(int i = 0; i < numOfVehicles;i++){
            if(vehicles[i].equals(arg0)){
                return false;
            }
        }

        //Expandir o array
        if(numOfVehicles == vehicles.length){
            Vehicle[] newVehicles = new Vehicle[DEFAULT_SIZE * 2];
            for(int i = 0; i < vehicles.length;i++){
                newVehicles[i] = vehicles[i];
            }
            vehicles = newVehicles;
        }

        //Adicionar ao array
        vehicles[numOfVehicles] = arg0;
        numOfVehicles++;
        return true;
    }

    @Override
    public void disableVehicle(Vehicle arg0) throws VehicleException {
        if(arg0 == null){
            throw new VehicleException("Veiculo nao pode ser nulo");
        }

        //Procurar o veículo
        //Variavel temporaria
        VehicleImpl temp = null;
        for(int i = 0; i < numOfVehicles; i++){
            if(vehicles[i].equals(arg0)){
                temp = (VehicleImpl) vehicles[i];
                break;
            }
        }
       
        //Se for null é pk nao existe na instituição
        if(temp == null) {
            throw new VehicleException("Veiculo nao existe na instituicao");
        }

        //Verificar se está desativado
        if(temp.getState() == State.DISABLED){
            throw new VehicleException("Veiculo ja esta desativado");
        }
        
        //Desativar
        temp.setStateDisable();
    }

        @Override
    public void enableVehicle(Vehicle arg0) throws VehicleException {
        if(arg0 == null){
            throw new VehicleException("Veiculo nao pode ser nulo");
        }
   
        //Procurar o veículo
        //Variavel temporaria
        VehicleImpl temp = null;
        for(int i = 0; i < numOfVehicles; i++){
            if(vehicles[i].equals(arg0)){
                temp = (VehicleImpl) vehicles[i];
                break;
            }
        }

        //Se for null é pk nao existe na instituição
        if(temp == null) {
            throw new VehicleException("Veiculo nao existe na instituicao");
        }

        //Verificar se está ativo
        if(temp.getState() == State.ACTIVE){
            throw new VehicleException("Veiculo ja esta ativo");
        }

        //Ativar
        temp.setStateActive();
    }


    @Override
    public AidBox[] getAidBoxes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAidBoxes'");
    }

    @Override
    public Container getContainer(AidBox arg0, ItemType arg1) throws ContainerException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getContainer'");
    }

    @Override
    public PickingMap getCurrentPickingMap() throws PickingMapException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCurrentPickingMap'");
    }

    @Override
    public double getDistance(AidBox arg0) throws AidBoxException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDistance'");
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }

    @Override
    public PickingMap[] getPickingMaps() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPickingMaps'");
    }

    @Override
    public PickingMap[] getPickingMaps(LocalDateTime arg0, LocalDateTime arg1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPickingMaps'");
    }

    @Override
    public Vehicle[] getVehicles() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getVehicles'");
    }
    // TODO: Implementar métodos da interface
}
