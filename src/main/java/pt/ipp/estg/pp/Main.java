package pt.ipp.estg.pp;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.Institution;
import pt.ipp.estg.pp.core.InstitutionImpl;
import pt.ipp.estg.pp.io.ImporterImpl;
import pt.ipp.estg.pp.simulator.SensorSimulator;
import com.estg.core.Measurement;
import java.time.LocalDateTime;

import com.estg.core.ItemType;
import com.estg.pickingManagement.Vehicle;
import com.estg.pickingManagement.Strategy;
import com.estg.pickingManagement.RouteValidator;
import com.estg.pickingManagement.RouteGenerator;
import com.estg.pickingManagement.Report;
import com.estg.pickingManagement.Route;
import pt.ipp.estg.pp.pickingManagement.VehicleImpl;
import pt.ipp.estg.pp.pickingManagement.RefrigeratedVehicleImpl;
import pt.ipp.estg.pp.pickingManagement.State;
import pt.ipp.estg.pp.pickingManagement.StrategyImpl;
import pt.ipp.estg.pp.pickingManagement.RouteValidatorImpl;
import pt.ipp.estg.pp.pickingManagement.RouteGeneratorImpl;
import pt.ipp.estg.pp.pickingManagement.ReportImpl;

public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("INICIANDO TESTE DO IMPORTERIMPL");
        System.out.println("==================================================");

        // 1. Criar a Instituição
        Institution institution = new InstitutionImpl("Câmara Municipal de Felgueiras");
        System.out.println("Instituição criada: " + institution.getName());

        // 2. Criar o Importer (usando ficheiros padrão: aidboxes.json e distances.json)
        ImporterImpl importer = new ImporterImpl();
        System.out.println("Importer inicializado...");

        try {
            // 3. Importar os dados
            System.out.println("A importar dados dos ficheiros JSON...");
            importer.importData(institution);
            System.out.println("Dados importados com sucesso!\n");

            // 4. Mostrar os dados importados
            System.out.println("--------------------------------------------------");
            System.out.println("DETALHES DAS CAIXAS DE SUPRIMENTOS (AIDBOXES)");
            System.out.println("--------------------------------------------------");
            
            AidBox[] boxes = institution.getAidBoxes();
            if (boxes == null || boxes.length == 0) {
                System.out.println("Nenhuma caixa de suprimentos importada.");
            } else {
                for (AidBox box : boxes) {
                    System.out.println("Código: " + box.getCode());
                    System.out.println("  Referência Local: " + box.getRefLocal());
                    System.out.println("  Zona: " + box.getZone());
                    if (box.getCoordinates() != null) {
                        System.out.println("  Coordenadas: Lat " + box.getCoordinates().getLatitude() + 
                                           " / Long " + box.getCoordinates().getLongitude());
                    }
                    
                    // Mostrar contentores da caixa
                    Container[] containers = box.getContainers();
                    System.out.println("  Contentores:");
                    if (containers == null || containers.length == 0) {
                        System.out.println("    [Nenhum contentor associado]");
                    } else {
                        for (Container c : containers) {
                            System.out.println("    - " + c.toString());
                        }
                    }
                    
                    // Mostrar distância a partir da Base
                    try {
                        double distFromBase = institution.getDistance(box);
                        System.out.println("  Distância a partir da Base: " + distFromBase + " metros");
                    } catch (Exception e) {
                        System.out.println("  [Erro ao obter distância a partir da Base: " + e.getMessage() + "]");
                    }
                    
                    System.out.println();
                }
            }

            System.out.println("--------------------------------------------------");
            System.out.println("MATRIZ DE DISTÂNCIAS / DURAÇÕES ENTRE AIDBOXES");
            System.out.println("--------------------------------------------------");
            if (boxes != null) {
                for (AidBox origin : boxes) {
                    for (AidBox dest : boxes) {
                        if (origin.getCode().equals(dest.getCode())) continue;
                        try {
                            double distance = origin.getDistance(dest);
                            double duration = origin.getDuration(dest);
                            System.out.println("De " + origin.getCode() + " para " + dest.getCode() + 
                                               ": Distância = " + distance + "m | Duração = " + duration + "s");
                        } catch (Exception e) {
                            System.out.println("De " + origin.getCode() + " para " + dest.getCode() + 
                                               ": [Sem ligação direta ou erro: " + e.getMessage() + "]");
                        }
                    }
                }
            }

            // 5. Testar o SensorSimulator
            System.out.println("\n==================================================");
            System.out.println("TESTANDO O SENSORSIMULATOR");
            System.out.println("==================================================");
            
            AidBox[] boxesForSim = institution.getAidBoxes();
            if (boxesForSim != null && boxesForSim.length > 0) {
                AidBox selectedBox = boxesForSim[0];
                Container[] containersForSim = selectedBox.getContainers();
                
                if (containersForSim != null && containersForSim.length > 0) {
                    Container selectedContainer = containersForSim[0];
                    System.out.println("Contentor selecionado para simulação:");
                    System.out.println("  Código: " + selectedContainer.getCode());
                    System.out.println("  Tipo: " + selectedContainer.getType());
                    System.out.println("  Capacidade Máxima: " + selectedContainer.getCapacity() + " Kg");
                    
                    SensorSimulator simulator = new SensorSimulator(12345L); // semente fixa para consistência
                    LocalDateTime baseTime = LocalDateTime.of(2026, 7, 2, 12, 0);
                    
                    System.out.println("\nA simular e adicionar 5 medições consecutivas...");
                    for (int i = 0; i < 5; i++) {
                        LocalDateTime testTime = baseTime.plusDays(i);
                        try {
                            Measurement m = simulator.simulateAndAddMeasurement(selectedContainer, testTime);
                            System.out.println("  [Sucesso] Adicionada medição para " + testTime + " -> Peso: " + m.getValue() + " Kg");
                        } catch (Exception ex) {
                            System.err.println("  [Erro] Falha ao adicionar medição para " + testTime + ": " + ex.getMessage());
                        }
                    }
                    
                    System.out.println("\nMedições registadas no contentor:");
                    Measurement[] savedMeasurements = selectedContainer.getMeasurements();
                    if (savedMeasurements == null || savedMeasurements.length == 0) {
                        System.out.println("  Nenhuma medição encontrada.");
                    } else {
                        for (Measurement m : savedMeasurements) {
                            System.out.println("  - Data/Hora: " + m.getDate() + " | Valor: " + m.getValue() + " Kg");
                        }
                    }
                } else {
                    System.out.println("Nenhum contentor encontrado na primeira caixa de suprimentos.");
                }
            } else {
                System.out.println("Nenhuma caixa de suprimentos disponível para simulação.");
            }

            // 6. Testar o RouteGeneratorImpl
            System.out.println("\n==================================================");
            System.out.println("TESTANDO O ROUTEGENERATORIMPL");
            System.out.println("==================================================");

            // Adicionar alguns veículos à instituição
            System.out.println("A registar veículos na instituição...");
            Vehicle v1 = new VehicleImpl(500.0, ItemType.CLOTHING, State.ACTIVE);
            Vehicle v2 = new RefrigeratedVehicleImpl(800.0, ItemType.PERISHABLE_FOOD, State.ACTIVE, 100.0, 4.0);
            institution.addVehicle(v1);
            institution.addVehicle(v2);
            System.out.println("  Veículos registados com sucesso!");

            // Garantir que existem contentores cheios (> 80%) para as rotas recolherem
            System.out.println("\nA encher contentores de teste para garantir a recolha...");
            LocalDateTime fillTime = LocalDateTime.now().plusDays(10);
            for (AidBox box : institution.getAidBoxes()) {
                Container cClothing = box.getContainer(ItemType.CLOTHING);
                if (cClothing != null) {
                    try {
                        cClothing.addMeasurement(new pt.ipp.estg.pp.core.MeasurementImpl(fillTime, cClothing.getCapacity() * 0.9));
                    } catch (Exception ex) {
                        // Ignorar se já existir
                    }
                }
                Container cPerishable = box.getContainer(ItemType.PERISHABLE_FOOD);
                if (cPerishable != null) {
                    try {
                        cPerishable.addMeasurement(new pt.ipp.estg.pp.core.MeasurementImpl(fillTime, cPerishable.getCapacity() * 0.9));
                    } catch (Exception ex) {
                        // Ignorar se já existir
                    }
                }
            }

            // Criar a estratégia, validador e gerador
            Strategy strategy = new StrategyImpl();
            RouteValidator routeValidator = new RouteValidatorImpl();
            RouteGenerator routeGenerator = new RouteGeneratorImpl();
            
            // Instanciar o relatório vazio
            Report report = new ReportImpl(0, 0, 0, 0.0, 0.0, 0);

            System.out.println("\nA gerar rotas e preencher relatório...");
            Route[] generatedRoutes = routeGenerator.generateRoutes(institution, strategy, routeValidator, report);
            
            System.out.println("Rotas geradas: " + (generatedRoutes == null ? 0 : generatedRoutes.length));
            if (generatedRoutes != null) {
                for (int i = 0; i < generatedRoutes.length; i++) {
                    Route r = generatedRoutes[i];
                    System.out.println("Rota " + (i + 1) + ":");
                    System.out.println("  Veículo: " + r.getVehicle().toString());
                    System.out.println("  Distância Total: " + r.getTotalDistance() + " m");
                    System.out.println("  Duração Total: " + r.getTotalDuration() + " s");
                    System.out.print("  Caminho: Base");
                    for (AidBox box : r.getRoute()) {
                        System.out.print(" -> " + box.getCode());
                    }
                    System.out.println(" -> Base");
                }
            }

            System.out.println("\nRelatório preenchido pelo gerador:");
            System.out.println(report.toString());

        } catch (Exception e) {
            System.err.println("Ocorreu um erro durante a importação ou teste:");
            e.printStackTrace();
        }

        System.out.println("==================================================");
        System.out.println("FIM DO TESTE");
        System.out.println("==================================================");
    }
}
