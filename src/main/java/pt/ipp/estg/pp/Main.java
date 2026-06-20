package pt.ipp.estg.pp;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.Institution;
import com.estg.core.ItemType;
import com.estg.core.Measurement;
import com.estg.core.exceptions.VehicleException;
import com.estg.core.exceptions.PickingMapException;
import com.estg.core.exceptions.ContainerException;
import com.estg.pickingManagement.PickingMap;
import com.estg.pickingManagement.Route;
import com.estg.pickingManagement.Vehicle;
import com.estg.pickingManagement.exceptions.RouteException;

import pt.ipp.estg.pp.core.InstitutionImpl;
import pt.ipp.estg.pp.core.AidBoxImpl;
import pt.ipp.estg.pp.core.ContainerImpl;
import pt.ipp.estg.pp.core.GeographicCoordinatesImpl;
import pt.ipp.estg.pp.core.MeasurementImpl;
import pt.ipp.estg.pp.io.ImporterImpl;
import pt.ipp.estg.pp.pickingManagement.*;
import pt.ipp.estg.pp.simulator.SensorSimulator;

import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {

    private static Route[] tempRoutes = new Route[20];
    private static int numOfTempRoutes = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Institution institution = new InstitutionImpl("Câmara Municipal de Felgueiras");

        System.out.println("Bem-vindo ao Sistema de Picking Management!");

        int option;
        do {
            option = MenuOptions.showMenu(scanner);

            switch (option) {
                case 1:
                    manageVehicles(scanner, institution);
                    break;
                case 2:
                    manageRoutes(scanner, institution);
                    break;
                case 3:
                    manageInstitution(scanner, institution);
                    break;
                case 4:
                    generateRoutes(institution);
                    break;
                case 5:
                    showPickingMaps(institution);
                    break;
                case 6:
                    importData(institution);
                    break;
                case 0:
                    System.out.println("A sair do sistema. Adeus!");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        } while (option != 0);

        scanner.close();
    }

    // --- SUB-MENUS ---

    private static void manageVehicles(Scanner scanner, Institution institution) {
        int option;
        do {
            option = MenuOptions.showVehicleMenu(scanner);
            switch (option) {
                case 1:
                    addVehicleInteractive(scanner, institution);
                    break;
                case 2:
                    disableVehicleInteractive(scanner, institution);
                    break;
                case 3:
                    enableVehicleInteractive(scanner, institution);
                    break;
                case 4:
                    listVehicles(institution);
                    break;
                case 5:
                    addVehicles(institution);
                    break;
                case 0:
                    System.out.println("A voltar ao Menu Principal...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        } while (option != 0);
    }

    private static void manageRoutes(Scanner scanner, Institution institution) {
        int option;
        do {
            option = MenuOptions.showRouteMenu(scanner);
            switch (option) {
                case 1:
                    createEmptyRouteInteractive(scanner, institution);
                    break;
                case 2:
                    addAidBoxToRouteInteractive(scanner, institution);
                    break;
                case 3:
                    removeAidBoxFromRouteInteractive(scanner, institution);
                    break;
                case 4:
                    listTempRoutes();
                    break;
                case 5:
                    saveRoutesToPickingMap(institution);
                    break;
                case 0:
                    System.out.println("A voltar ao Menu Principal...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        } while (option != 0);
    }

    private static void manageInstitution(Scanner scanner, Institution institution) {
        int option;
        do {
            option = MenuOptions.showAidBoxMenu(scanner);
            switch (option) {
                case 1:
                    addAidBoxInteractive(scanner, institution);
                    break;
                case 2:
                    addContainerInteractive(scanner, institution);
                    break;
                case 3:
                    showContainersInfo(institution);
                    break;
                case 4:
                    showSpecificAidBoxInfo(scanner, institution);
                    break;
                case 5:
                    SensorSimulator.generateRandomMeasurements(institution);
                    break;
                case 0:
                    System.out.println("A voltar ao Menu Principal...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        } while (option != 0);
    }

    // --- VEHICLE HANDLERS ---

    private static void addVehicleInteractive(Scanner scanner, Institution institution) {
        System.out.println("\n--- Adicionar Novo Veículo ---");
        double cap = readDouble(scanner, "Capacidade Máxima (kg): ");
        ItemType type = chooseItemType(scanner);
        System.out.print("É um veículo refrigerado? (S/N): ");
        String isRefrigInput = scanner.next();
        boolean isRefrig = isRefrigInput.equalsIgnoreCase("s") || isRefrigInput.equalsIgnoreCase("sim");

        Vehicle vehicle;
        if (isRefrig) {
            double maxKm = readDouble(scanner, "Limite máximo de quilómetros a circular com carga: ");
            double temp = readDouble(scanner, "Temperatura ideal da câmara refrigerada (ºC): ");
            vehicle = new RefrigeratedVehicleImpl(cap, type, State.ACTIVE, maxKm, temp);
        } else {
            vehicle = new VehicleImpl(cap, type, State.ACTIVE);
        }

        try {
            boolean added = institution.addVehicle(vehicle);
            if (added) {
                System.out.println("Veículo adicionado com sucesso!");
            } else {
                System.out.println("Aviso: Veículo já existe na instituição!");
            }
        } catch (VehicleException e) {
            System.out.println("Erro ao adicionar veículo: " + e.getMessage());
        }
    }

    private static void disableVehicleInteractive(Scanner scanner, Institution institution) {
        Vehicle[] vehicles = institution.getVehicles();
        if (vehicles == null || vehicles.length == 0) {
            System.out.println("Não existem veículos registados na instituição.");
            return;
        }
        System.out.println("\n--- Desativar Veículo ---");
        for (int i = 0; i < vehicles.length; i++) {
            System.out.println((i + 1) + " - " + vehicles[i]);
        }
        System.out.print("Escolha o veículo a desativar (1-" + vehicles.length + "): ");
        try {
            int idx = scanner.nextInt();
            if (idx >= 1 && idx <= vehicles.length) {
                institution.disableVehicle(vehicles[idx - 1]);
                System.out.println("Veículo desativado com sucesso!");
            } else {
                System.out.println("Erro: Opção inválida.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Erro: Introduza um número válido!");
            scanner.next();
        } catch (VehicleException e) {
            System.out.println("Erro ao desativar veículo: " + e.getMessage());
        }
    }

    private static void enableVehicleInteractive(Scanner scanner, Institution institution) {
        Vehicle[] vehicles = institution.getVehicles();
        if (vehicles == null || vehicles.length == 0) {
            System.out.println("Não existem veículos registados na instituição.");
            return;
        }
        System.out.println("\n--- Ativar Veículo ---");
        for (int i = 0; i < vehicles.length; i++) {
            System.out.println((i + 1) + " - " + vehicles[i]);
        }
        System.out.print("Escolha o veículo a ativar (1-" + vehicles.length + "): ");
        try {
            int idx = scanner.nextInt();
            if (idx >= 1 && idx <= vehicles.length) {
                institution.enableVehicle(vehicles[idx - 1]);
                System.out.println("Veículo ativado com sucesso!");
            } else {
                System.out.println("Erro: Opção inválida.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Erro: Introduza um número válido!");
            scanner.next();
        } catch (VehicleException e) {
            System.out.println("Erro ao ativar veículo: " + e.getMessage());
        }
    }

    private static void listVehicles(Institution institution) {
        Vehicle[] vehicles = institution.getVehicles();
        if (vehicles == null || vehicles.length == 0) {
            System.out.println("Não existem veículos registados na instituição.");
            return;
        }
        System.out.println("\n--- Lista de Veículos ---");
        for (int i = 0; i < vehicles.length; i++) {
            System.out.println((i + 1) + " - " + vehicles[i]);
        }
    }

    private static void addVehicles(Institution institution) {
        System.out.println("\n[Adicionar Veículos Padrão]");
        try {
            Vehicle v1 = new VehicleImpl(500.0, ItemType.PERISHABLE_FOOD, State.ACTIVE);
            Vehicle v2 = new VehicleImpl(300.0, ItemType.NON_PERISHABLE_FOOD, State.ACTIVE);
            Vehicle v3 = new VehicleImpl(200.0, ItemType.CLOTHING, State.ACTIVE);
            Vehicle v4 = new VehicleImpl(150.0, ItemType.MEDICINE, State.ACTIVE);
            Vehicle vRefrig = new RefrigeratedVehicleImpl(400.0, ItemType.PERISHABLE_FOOD, State.ACTIVE, 50.0, -5.0);

            institution.addVehicle(v1);
            institution.addVehicle(v2);
            institution.addVehicle(v3);
            institution.addVehicle(v4);
            institution.addVehicle(vRefrig);

            System.out.println("Foram adicionados 5 veículos padrão (ativos e com diferentes tipos)!");
        } catch (VehicleException e) {
            System.out.println("Aviso: " + e.getMessage() + " (Alguns veículos já podem ter sido adicionados)");
        }
    }

    // --- ROUTE HANDLERS ---

    private static void createEmptyRouteInteractive(Scanner scanner, Institution institution) {
        System.out.println("\n--- Criar Rota Vazia ---");
        Vehicle selectedVehicle = chooseVehicle(scanner, institution);
        if (selectedVehicle == null) {
            System.out.println("Cancelado: Nenhum veículo selecionado.");
            return;
        }

        if (selectedVehicle instanceof VehicleImpl && ((VehicleImpl) selectedVehicle).getState() == State.DISABLE) {
            System.out.println("Erro: Não é possível criar uma rota para um veículo desativado!");
            return;
        }

        RouteImpl route = new RouteImpl(selectedVehicle);

        if (numOfTempRoutes == tempRoutes.length) {
            Route[] newArr = new Route[tempRoutes.length * 2];
            System.arraycopy(tempRoutes, 0, newArr, 0, numOfTempRoutes);
            tempRoutes = newArr;
        }
        tempRoutes[numOfTempRoutes++] = route;
        System.out.println("Rota vazia criada com sucesso para o veículo (" + selectedVehicle.getSupplyType() + ")!");
    }

    private static void addAidBoxToRouteInteractive(Scanner scanner, Institution institution) {
        System.out.println("\n--- Adicionar Paragem (AidBox) a Rota ---");
        if (numOfTempRoutes == 0) {
            System.out.println("Não existem rotas temporárias criadas. Crie uma rota primeiro!");
            return;
        }

        System.out.println("Selecione a rota:");
        for (int i = 0; i < numOfTempRoutes; i++) {
            System.out.println((i + 1) + " - Rota " + (i + 1) + " (Veículo: " + tempRoutes[i].getVehicle().getSupplyType() + " | Paragens: " + tempRoutes[i].getRoute().length + ")");
        }
        System.out.print("Escolha (1-" + numOfTempRoutes + "): ");
        int routeOpt;
        try {
            routeOpt = scanner.nextInt();
            if (routeOpt < 1 || routeOpt > numOfTempRoutes) {
                System.out.println("Opção inválida!");
                return;
            }
        } catch (InputMismatchException e) {
            System.out.println("Erro: Introduza um número válido!");
            scanner.next();
            return;
        }

        Route selectedRoute = tempRoutes[routeOpt - 1];
        ItemType vType = selectedRoute.getVehicle().getSupplyType();

        AidBox[] allBoxes = institution.getAidBoxes();
        if (allBoxes == null || allBoxes.length == 0) {
            System.out.println("Não existem AidBoxes registadas na instituição.");
            return;
        }

        int countCompatible = 0;
        for (AidBox box : allBoxes) {
            if (box.getContainer(vType) != null && !selectedRoute.containsAidBox(box)) {
                countCompatible++;
            }
        }

        if (countCompatible == 0) {
            System.out.println("Não existem AidBoxes compatíveis disponíveis para adicionar a esta rota.");
            return;
        }

        AidBox[] compatibleBoxes = new AidBox[countCompatible];
        int index = 0;
        for (AidBox box : allBoxes) {
            if (box.getContainer(vType) != null && !selectedRoute.containsAidBox(box)) {
                compatibleBoxes[index++] = box;
            }
        }

        System.out.println("Selecione a AidBox compatível a adicionar:");
        for (int i = 0; i < compatibleBoxes.length; i++) {
            System.out.println((i + 1) + " - Código: " + compatibleBoxes[i].getCode() + " | Ref Local: " + compatibleBoxes[i].getRefLocal());
        }
        System.out.print("Escolha (1-" + compatibleBoxes.length + "): ");
        int boxOpt;
        try {
            boxOpt = scanner.nextInt();
            if (boxOpt < 1 || boxOpt > compatibleBoxes.length) {
                System.out.println("Opção inválida!");
                return;
            }
        } catch (InputMismatchException e) {
            System.out.println("Erro: Introduza um número válido!");
            scanner.next();
            return;
        }

        AidBox selectedBox = compatibleBoxes[boxOpt - 1];
        try {
            selectedRoute.addAidBox(selectedBox);
            System.out.println("AidBox " + selectedBox.getCode() + " adicionada com sucesso à rota!");
        } catch (RouteException e) {
            System.out.println("Erro ao adicionar AidBox à rota: " + e.getMessage());
        }
    }

    private static void removeAidBoxFromRouteInteractive(Scanner scanner, Institution institution) {
        System.out.println("\n--- Remover Paragem (AidBox) de Rota ---");
        if (numOfTempRoutes == 0) {
            System.out.println("Não existem rotas temporárias criadas.");
            return;
        }

        System.out.println("Selecione a rota:");
        for (int i = 0; i < numOfTempRoutes; i++) {
            System.out.println((i + 1) + " - Rota " + (i + 1) + " (Veículo: " + tempRoutes[i].getVehicle().getSupplyType() + " | Paragens: " + tempRoutes[i].getRoute().length + ")");
        }
        System.out.print("Escolha (1-" + numOfTempRoutes + "): ");
        int routeOpt;
        try {
            routeOpt = scanner.nextInt();
            if (routeOpt < 1 || routeOpt > numOfTempRoutes) {
                System.out.println("Opção inválida!");
                return;
            }
        } catch (InputMismatchException e) {
            System.out.println("Erro: Introduza um número válido!");
            scanner.next();
            return;
        }

        Route selectedRoute = tempRoutes[routeOpt - 1];
        AidBox[] routeBoxes = selectedRoute.getRoute();
        if (routeBoxes == null || routeBoxes.length == 0) {
            System.out.println("Esta rota não tem paragens para remover.");
            return;
        }

        System.out.println("Selecione a paragem a remover:");
        for (int i = 0; i < routeBoxes.length; i++) {
            System.out.println((i + 1) + " - " + routeBoxes[i].getCode() + " (" + routeBoxes[i].getRefLocal() + ")");
        }
        System.out.print("Escolha (1-" + routeBoxes.length + "): ");
        int removeOpt;
        try {
            removeOpt = scanner.nextInt();
            if (removeOpt < 1 || removeOpt > routeBoxes.length) {
                System.out.println("Opção inválida!");
                return;
            }
        } catch (InputMismatchException e) {
            System.out.println("Erro: Introduza um número válido!");
            scanner.next();
            return;
        }

        AidBox toRemove = routeBoxes[removeOpt - 1];
        try {
            selectedRoute.removeAidBox(toRemove);
            System.out.println("AidBox " + toRemove.getCode() + " removida da rota!");
        } catch (RouteException e) {
            System.out.println("Erro ao remover AidBox da rota: " + e.getMessage());
        }
    }

    private static void listTempRoutes() {
        System.out.println("\n--- Lista de Rotas Temporárias (" + numOfTempRoutes + ") ---");
        if (numOfTempRoutes == 0) {
            System.out.println("Nenhuma rota temporária criada.");
            return;
        }
        for (int i = 0; i < numOfTempRoutes; i++) {
            System.out.println("Rota " + (i + 1) + ":");
            System.out.println("  Veículo: " + tempRoutes[i].getVehicle());
            AidBox[] stops = tempRoutes[i].getRoute();
            System.out.print("  Paragens: ");
            if (stops == null || stops.length == 0) {
                System.out.println("(Nenhuma paragem)");
            } else {
                for (int j = 0; j < stops.length; j++) {
                    System.out.print(stops[j].getCode());
                    if (j < stops.length - 1) {
                        System.out.print(" -> ");
                    }
                }
                System.out.println();
            }
            System.out.println("  Distância Total: " + tempRoutes[i].getTotalDistance() + " m");
            System.out.println("  Duração Total: " + tempRoutes[i].getTotalDuration() + " s");
        }
    }

    private static void saveRoutesToPickingMap(Institution institution) {
        System.out.println("\n--- Gravar Rotas no PickingMap ---");
        if (numOfTempRoutes == 0) {
            System.out.println("Não existem rotas temporárias criadas para gravar.");
            return;
        }

        Route[] routesArray = new Route[numOfTempRoutes];
        System.arraycopy(tempRoutes, 0, routesArray, 0, numOfTempRoutes);

        PickingMapImpl pickingMap = new PickingMapImpl(LocalDateTime.now(), routesArray);
        try {
            boolean added = institution.addPickingMap(pickingMap);
            if (added) {
                System.out.println("Sucesso: " + numOfTempRoutes + " rotas gravadas num novo PickingMap!");
                
                // Esvaziar os contentores recolhidos pelas rotas manuais
                System.out.println("Esvaziando contentores recolhidos...");
                for (Route route : routesArray) {
                    if (route == null) continue;
                    Vehicle vehicle = route.getVehicle();
                    if (vehicle == null) continue;
                    
                    AidBox[] routeBoxes = route.getRoute();
                    if (routeBoxes != null) {
                        for (AidBox box : routeBoxes) {
                            if (box == null) continue;
                            Container container = box.getContainer(vehicle.getSupplyType());
                            if (container != null) {
                                try {
                                    Measurement emptyMeasurement = new MeasurementImpl(LocalDateTime.now(), 0.0);
                                    institution.addMeasurement(emptyMeasurement, container);
                                    System.out.println("  -> Contentor " + container.getCode() + " na AidBox " + box.getCode() + " foi recolhido e esvaziado.");
                                } catch (Exception e) {
                                    // Ignorar
                                }
                            }
                        }
                    }
                }
                
                numOfTempRoutes = 0;
            } else {
                System.out.println("Erro: PickingMap idêntico já existe na instituição.");
            }
        } catch (PickingMapException e) {
            System.out.println("Erro ao gravar PickingMap: " + e.getMessage());
        }
    }

    // --- AIDBOX / INSTITUTION HANDLERS ---

    private static void addAidBoxInteractive(Scanner scanner, Institution institution) {
        System.out.println("\n--- Adicionar Nova AidBox ---");
        System.out.print("Código único da AidBox: ");
        String code = scanner.next();
        System.out.print("Zona: ");
        String zone = scanner.next();
        System.out.print("Referência Local: ");
        scanner.nextLine(); // clear buffer
        String refLocal = scanner.nextLine();
        double lat = readDouble(scanner, "Latitude (-90 a 90): ");
        double lon = readDouble(scanner, "Longitude (-180 a 180): ");

        try {
            GeographicCoordinatesImpl coords = new GeographicCoordinatesImpl(lat, lon);
            AidBoxImpl box = new AidBoxImpl(code, coords, refLocal, zone);
            boolean added = institution.addAidBox(box);
            if (added) {
                System.out.println("AidBox adicionada com sucesso!");
            } else {
                System.out.println("Erro: Já existe uma AidBox com esse código!");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Erro nas coordenadas: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao adicionar AidBox: " + e.getMessage());
        }
    }

    private static void addContainerInteractive(Scanner scanner, Institution institution) {
        System.out.println("\n--- Adicionar Contentor a AidBox ---");
        AidBox[] boxes = institution.getAidBoxes();
        if (boxes == null || boxes.length == 0) {
            System.out.println("Não existem AidBoxes registadas na instituição. Importe os dados ou adicione uma primeiro!");
            return;
        }

        System.out.println("Selecione a AidBox:");
        for (int i = 0; i < boxes.length; i++) {
            System.out.println((i + 1) + " - " + boxes[i].getCode() + " (" + boxes[i].getRefLocal() + ")");
        }
        System.out.print("Escolha (1-" + boxes.length + "): ");
        int boxOpt;
        try {
            boxOpt = scanner.nextInt();
            if (boxOpt < 1 || boxOpt > boxes.length) {
                System.out.println("Opção inválida!");
                return;
            }
        } catch (InputMismatchException e) {
            System.out.println("Erro: Introduza um número válido!");
            scanner.next();
            return;
        }

        AidBox selectedBox = boxes[boxOpt - 1];
        System.out.print("Código único do Contentor: ");
        String code = scanner.next();
        ItemType type = chooseItemType(scanner);
        double capacity = readDouble(scanner, "Capacidade do contentor (kg): ");

        try {
            ContainerImpl container = new ContainerImpl(capacity, code, type);
            boolean added = selectedBox.addContainer(container);
            if (added) {
                System.out.println("Contentor adicionado com sucesso à AidBox " + selectedBox.getCode() + "!");
            } else {
                System.out.println("Erro: Não foi possível adicionar o contentor (pode já existir ou atingiu o limite)!");
            }
        } catch (ContainerException e) {
            System.out.println("Erro de contentor: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao adicionar contentor: " + e.getMessage());
        }
    }

    // --- OTHER CORE ACTIONS ---

    private static void generateRoutes(Institution institution) {
        System.out.println("\n[Gerar Rotas Automaticamente]");
        
        if (institution.getAidBoxes() == null || institution.getAidBoxes().length == 0) {
            System.out.println("Não existem AidBoxes. Por favor importe os dados primeiro ou crie-as manualmente.");
            return;
        }
        if (institution.getVehicles() == null || institution.getVehicles().length == 0) {
            System.out.println("Não existem Veículos. Por favor adicione veículos primeiro.");
            return;
        }

        SensorSimulator.generateRandomMeasurements(institution);

        StrategyImpl strategy = new StrategyImpl();
        RouteValidatorImpl validator = new RouteValidatorImpl();
        ReportImpl report = new ReportImpl();
        RouteGeneratorImpl generator = new RouteGeneratorImpl();

        Route[] generatedRoutes = generator.generateRoutes(institution, strategy, validator, report);

        System.out.println("\n--- RELATÓRIO DA OPERAÇÃO ---");
        System.out.println("Data: " + report.getDate());
        System.out.println("Rotas criadas: " + generatedRoutes.length);
        System.out.println("Veículos utilizados: " + report.getUsedVehicles());
        System.out.println("Veículos poupados: " + report.getNotUsedVehicles());
        System.out.println("Contentores para recolha: " + report.getPickedContainers());
        System.out.println("Contentores não recolhidos: " + report.getNonPickedContainers());
        System.out.println("Distância total percorrida: " + report.getTotalDistance() + " m");
        System.out.println("-----------------------------\n");

        if (generatedRoutes.length > 0) {
            System.out.println("Detalhes das Rotas Geradas:");
            for (int i = 0; i < generatedRoutes.length; i++) {
                Route r = generatedRoutes[i];
                if (r == null) continue;
                System.out.println("  Rota " + (i + 1) + ":");
                System.out.println("    Veículo: " + r.getVehicle().getSupplyType() + " (Cap: " + r.getVehicle().getMaxCapacity() + " kg)");
                System.out.println("    Distância: " + String.format("%.2f", r.getTotalDistance() / 1000.0) + " km");
                System.out.println("    Duração: " + String.format("%.2f", r.getTotalDuration() / 60.0) + " min");

                AidBox[] routeBoxes = r.getRoute();
                System.out.print("    Percurso: Base");
                if (routeBoxes != null) {
                    for (AidBox box : routeBoxes) {
                        if (box != null) {
                            System.out.print(" -> " + box.getCode());
                        }
                    }
                }
                System.out.println(" -> Base");
            }
            System.out.println();

            // Esvaziar os contentores recolhidos pelas rotas automáticas
            System.out.println("Esvaziando contentores recolhidos...");
            for (Route route : generatedRoutes) {
                if (route == null) continue;
                Vehicle vehicle = route.getVehicle();
                if (vehicle == null) continue;
                
                AidBox[] routeBoxes = route.getRoute();
                if (routeBoxes != null) {
                    for (AidBox box : routeBoxes) {
                        if (box == null) continue;
                        Container container = box.getContainer(vehicle.getSupplyType());
                        if (container != null) {
                            try {
                                Measurement emptyMeasurement = new MeasurementImpl(LocalDateTime.now(), 0.0);
                                institution.addMeasurement(emptyMeasurement, container);
                                System.out.println("  -> Contentor " + container.getCode() + " na AidBox " + box.getCode() + " foi recolhido e esvaziado.");
                            } catch (Exception e) {
                                // Ignorar
                            }
                        }
                    }
                }
            }
            System.out.println();
        }

        PickingMapImpl pickingMap = new PickingMapImpl(LocalDateTime.now(), generatedRoutes);
        try {
            institution.addPickingMap(pickingMap);
            System.out.println("Novo PickingMap gerado e gravado no histórico da Instituição.");
        } catch (PickingMapException e) {
            System.out.println("Erro ao gravar PickingMap: " + e.getMessage());
        }
    }

    private static void showPickingMaps(Institution institution) {
        System.out.println("\n[Informação dos Picking Maps]");
        PickingMap[] maps = institution.getPickingMaps();
        if (maps == null || maps.length == 0) {
            System.out.println("Nenhum mapa de recolha (Picking Map) foi gerado ainda.");
            return;
        }

        System.out.println("Total de mapas de recolha no histórico: " + maps.length);
        for (int i = 0; i < maps.length; i++) {
            System.out.println("\nMapa " + (i + 1) + " (Data: " + maps[i].getDate() + ")");
            Route[] routes = maps[i].getRoutes();
            for (int j = 0; j < routes.length; j++) {
                System.out.println("  Rota " + (j + 1) + " -> Veículo: " + routes[j].getVehicle().getSupplyType() 
                                   + " | AidBoxes visitadas: " + routes[j].getRoute().length);
            }
        }
    }

    private static void importData(Institution institution) {
        System.out.println("\n[Importação de Dados]");
        ImporterImpl importer = new ImporterImpl();
        try {
            importer.importData(institution);
            System.out.println("Dados importados com sucesso a partir dos ficheiros JSON!");
            AidBox[] boxes = institution.getAidBoxes();
            int boxesCount = (boxes == null) ? 0 : boxes.length;
            int containersCount = 0;
            if (boxes != null) {
                for (AidBox b : boxes) {
                    if (b.getContainers() != null) {
                        containersCount += b.getContainers().length;
                    }
                }
            }
            System.out.println(" - " + boxesCount + " AidBoxes importadas.");
            System.out.println(" - " + containersCount + " Contentores importados.");
            System.out.println("Nota: Os veículos e as rotas temporárias não vêm dos ficheiros JSON. Deves adicioná-los através do menu.");
        } catch (Exception e) {
            System.out.println("Erro na importação: " + e.getMessage());
        }
    }

    private static void showContainersInfo(Institution institution) {
        System.out.println("\n[Informação da Instituição]");
        System.out.println("Nome: " + institution.getName());
        
        AidBox[] boxes = institution.getAidBoxes();
        int numBoxes = (boxes == null) ? 0 : boxes.length;
        System.out.println("Número de AidBoxes: " + numBoxes);
        
        Vehicle[] vehicles = institution.getVehicles();
        int numVehicles = (vehicles == null) ? 0 : vehicles.length;
        System.out.println("Número de Veículos: " + numVehicles);

        System.out.println("\n[Informação dos Contentores]");
        if (boxes == null || boxes.length == 0) {
            System.out.println("Nenhum contentor disponível. Importe os dados primeiro ou adicione-os manualmente.");
            return;
        }

        for (AidBox box : boxes) {
            System.out.println("\nAidBox: " + box.getCode() + " (" + box.getRefLocal() + ")");
            Container[] containers = box.getContainers();
            for (Container c : containers) {
                System.out.print("  - Contentor " + c.getCode() + " (" + c.getType() + ") | Cap: " + c.getCapacity() + "kg");
                Measurement[] ms = c.getMeasurements();
                if (ms != null && ms.length > 0) {
                    double actualLoad = ms[ms.length - 1].getValue();
                    double percent = (actualLoad / c.getCapacity()) * 100;
                    System.out.printf(" | Peso Atual: %.2f kg (%.1f%%)\n", actualLoad, percent);
                } else {
                    System.out.println(" | Peso Atual: 0.00 kg (0.0%)");
                }
            }
        }
    }

    // --- HELPER READ METHODS ---

    private static double readDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return scanner.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("Erro: Introduza um número válido!");
                scanner.next(); // clear buffer
            }
        }
    }

    private static ItemType chooseItemType(Scanner scanner) {
        while (true) {
            System.out.println("Selecione o tipo de carga:");
            System.out.println("1 - Alimentos Perecíveis (PERISHABLE_FOOD)");
            System.out.println("2 - Alimentos Não Perecíveis (NON_PERISHABLE_FOOD)");
            System.out.println("3 - Vestuário (CLOTHING)");
            System.out.println("4 - Medicamentos (MEDICINE)");
            System.out.print("Escolha (1-4): ");
            try {
                int opt = scanner.nextInt();
                switch (opt) {
                    case 1: return ItemType.PERISHABLE_FOOD;
                    case 2: return ItemType.NON_PERISHABLE_FOOD;
                    case 3: return ItemType.CLOTHING;
                    case 4: return ItemType.MEDICINE;
                    default: System.out.println("Opção inválida!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Erro: Introduza um número inteiro válido!");
                scanner.next(); // clear buffer
            }
        }
    }

    private static Vehicle chooseVehicle(Scanner scanner, Institution institution) {
        Vehicle[] vehicles = institution.getVehicles();
        if (vehicles == null || vehicles.length == 0) {
            System.out.println("Não existem veículos registados na instituição.");
            return null;
        }

        System.out.println("\nSelecione um veículo:");
        for (int i = 0; i < vehicles.length; i++) {
            System.out.println((i + 1) + " - " + vehicles[i]);
        }
        System.out.print("Escolha (1-" + vehicles.length + "): ");
        try {
            int idx = scanner.nextInt();
            if (idx >= 1 && idx <= vehicles.length) {
                return vehicles[idx - 1];
            } else {
                System.out.println("Opção fora do intervalo!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Erro: Introduza um número válido!");
            scanner.next();
        }
        return null;
    }

    private static void showSpecificAidBoxInfo(Scanner scanner, Institution institution) {
        System.out.println("\n--- Consultar AidBox Específica ---");
        System.out.print("Introduza o código da AidBox: ");
        String code = scanner.next().trim();

        AidBox[] boxes = institution.getAidBoxes();
        if (boxes != null) {
            for (AidBox box : boxes) {
                if (box.getCode().equalsIgnoreCase(code)) {
                    System.out.println("\nDetalhes da AidBox:");
                    System.out.println("Código: " + box.getCode());
                    System.out.println("Referência Local: " + box.getRefLocal());
                    System.out.println("Zona: " + box.getZone());
                    System.out.println("Coordenadas: Lat: " + box.getCoordinates().getLatitude() + " | Lon: " + box.getCoordinates().getLongitude());
                    System.out.println("Contentores:");
                    Container[] containers = box.getContainers();
                    if (containers == null || containers.length == 0) {
                        System.out.println("  (Nenhum contentor associado)");
                    } else {
                        for (Container c : containers) {
                            System.out.print("  - Contentor " + c.getCode() + " (" + c.getType() + ") | Cap: " + c.getCapacity() + "kg");
                            Measurement[] ms = c.getMeasurements();
                            if (ms != null && ms.length > 0) {
                                double actualLoad = ms[ms.length - 1].getValue();
                                double percent = (actualLoad / c.getCapacity()) * 100;
                                System.out.printf(" | Peso Atual: %.2f kg (%.1f%%)\n", actualLoad, percent);
                            } else {
                                System.out.println(" | Peso Atual: 0.00 kg (0.0%)");
                            }
                        }
                    }
                    return;
                }
            }
        }
        System.out.println("Erro: Código de AidBox inválido ou inexistente.");
    }
}
