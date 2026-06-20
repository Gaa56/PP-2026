package pt.ipp.estg.pp;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.Institution;
import pt.ipp.estg.pp.core.InstitutionImpl;
import pt.ipp.estg.pp.io.ImporterImpl;

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

        } catch (Exception e) {
            System.err.println("Ocorreu um erro durante a importação ou teste:");
            e.printStackTrace();
        }

        System.out.println("==================================================");
        System.out.println("FIM DO TESTE");
        System.out.println("==================================================");
    }
}
