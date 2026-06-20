package pt.ipp.estg.pp;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuOptions {

    private static int readOption(Scanner input) {
        int option = -1;
        try {
            option = input.nextInt();
        } catch (InputMismatchException exception) {
            System.out.println("Opção inválida! Por favor, introduza um número inteiro.");
            input.next(); // clear the invalid token
        }
        return option;
    }

    public static int showMenu(Scanner input) {
        System.out.println("\n##----------- Menu Principal -----------##");
        System.out.println("|----------------------------------------|");
        System.out.println("| Opção 1 - Gestão de Veículos           |");
        System.out.println("| Opção 2 - Gestão de Rotas Manuais      |");
        System.out.println("| Opção 3 - Gestão da Instituição        |");
        System.out.println("| Opção 4 - Gerar Rotas Automaticamente  |");
        System.out.println("| Opção 5 - Histórico de Picking Maps    |");
        System.out.println("| Opção 6 - Importar Dados (JSON)        |");
        System.out.println("| Opção 0 - Sair                         |");
        System.out.println("|----------------------------------------|");
        System.out.print("Escolha uma opção: ");
        return readOption(input);
    }

    public static int showVehicleMenu(Scanner input) {
        System.out.println("\n##--------- Gestão de Veículos ----------##");
        System.out.println("|----------------------------------------|");
        System.out.println("| Opção 1 - Adicionar Veículo            |");
        System.out.println("| Opção 2 - Desativar Veículo            |");
        System.out.println("| Opção 3 - Ativar Veículo               |");
        System.out.println("| Opção 4 - Listar Veículos              |");
        System.out.println("| Opção 5 - Adicionar Veículos Padrão    |");
        System.out.println("| Opção 0 - Voltar ao Menu Principal     |");
        System.out.println("|----------------------------------------|");
        System.out.print("Escolha uma opção: ");
        return readOption(input);
    }

    public static int showRouteMenu(Scanner input) {
        System.out.println("\n##------- Gestão de Rotas Manuais -------##");
        System.out.println("|----------------------------------------|");
        System.out.println("| Opção 1 - Criar Rota Vazia             |");
        System.out.println("| Opção 2 - Adicionar Paragem (AidBox)   |");
        System.out.println("| Opção 3 - Remover Paragem (AidBox)     |");
        System.out.println("| Opção 4 - Listar Rotas Temporárias     |");
        System.out.println("| Opção 5 - Gravar Rotas no PickingMap   |");
        System.out.println("| Opção 0 - Voltar ao Menu Principal     |");
        System.out.println("|----------------------------------------|");
        System.out.print("Escolha uma opção: ");
        return readOption(input);
    }

    public static int showAidBoxMenu(Scanner input) {
        System.out.println("\n##-------- Gestão da Instituição --------##");
        System.out.println("|----------------------------------------|");
        System.out.println("| Opção 1 - Adicionar AidBox             |");
        System.out.println("| Opção 2 - Adicionar Contentor a AidBox |");
        System.out.println("| Opção 3 - Listar AidBoxes/Contentores  |");
        System.out.println("| Opção 0 - Voltar ao Menu Principal     |");
        System.out.println("|----------------------------------------|");
        System.out.print("Escolha uma opção: ");
        return readOption(input);
    }
}
