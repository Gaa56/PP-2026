package pt.ipp.estg.pp.io;

import com.estg.io.Importer;
import com.estg.core.Institution;
import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.GeographicCoordinates;
import com.estg.core.ItemType;
import com.estg.core.exceptions.InstitutionException;
import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;

import pt.ipp.estg.pp.core.AidBoxImpl;
import pt.ipp.estg.pp.core.ContainerImpl;
import pt.ipp.estg.pp.core.GeographicCoordinatesImpl;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * Implementação da interface Importer para carregar dados das caixas de
 * suprimentos
 * e distâncias a partir de ficheiros JSON usando a biblioteca json-simple.
 */
public class ImporterImpl implements Importer {

    private final String aidBoxesFile;
    private final String distanceFile;

    /**
     * Construtor para caminhos personalizados.
     *
     * @param aidBoxesFile caminho para o ficheiro de caixas de suprimentos
     * @param distanceFile caminho para o ficheiro de distâncias e durações
     */
    public ImporterImpl(String aidBoxesFile, String distanceFile) {
        this.aidBoxesFile = aidBoxesFile;
        this.distanceFile = distanceFile;
    }

    /**
     * Construtor por omissão. Assume os ficheiros no diretório raiz do projeto.
     */
    public ImporterImpl() {
        this("aidboxes.json", "distances.json");
    }

    @Override
    public void importData(Institution institution) throws FileNotFoundException, IOException, InstitutionException {
        if (institution == null) {
            throw new InstitutionException("A instituição não pode ser nula.");
        }

        JSONParser parser = new JSONParser();

        try {
            // 1. LER CAIXAS DE SUPRIMENTOS (AidBoxes)
            FileReader readerBoxes = new FileReader(this.aidBoxesFile);
            JSONArray boxesArray = (JSONArray) parser.parse(readerBoxes);
            readerBoxes.close();

            for (Object obj : boxesArray) {
                JSONObject boxJson = (JSONObject) obj;

                // Suporta "code" ou "Codigo"
                String code = boxJson.containsKey("code") ? (String) boxJson.get("code")
                        : (String) boxJson.get("Codigo");
                // Suporta "refLocal" ou "Zona" (se faltar refLocal)
                String refLocal = boxJson.containsKey("refLocal") ? (String) boxJson.get("refLocal")
                        : (String) boxJson.get("Zona");
                // Suporta "zone" ou "Zona"
                String zone = boxJson.containsKey("zone") ? (String) boxJson.get("zone") : (String) boxJson.get("Zona");

                double latitude = 0.0;
                double longitude = 0.0;
                if (boxJson.containsKey("coordinates")) {
                    JSONObject coordJson = (JSONObject) boxJson.get("coordinates");
                    latitude = toDouble(coordJson.get("latitude"));
                    longitude = toDouble(coordJson.get("longitude"));
                } else if (boxJson.containsKey("Latitude")) {
                    latitude = toDouble(boxJson.get("Latitude"));
                    longitude = toDouble(boxJson.get("Longitude"));
                } else {
                    latitude = toDouble(boxJson.get("latitude"));
                    longitude = toDouble(boxJson.get("longitude"));
                }

                GeographicCoordinates coords = new GeographicCoordinatesImpl(latitude, longitude);
                AidBox aidBox = new AidBoxImpl(code, coords, refLocal, zone);

                // Suporta "containers" ou "Contentores"
                JSONArray containersArray = null;
                if (boxJson.containsKey("containers")) {
                    containersArray = (JSONArray) boxJson.get("containers");
                } else if (boxJson.containsKey("Contentores")) {
                    containersArray = (JSONArray) boxJson.get("Contentores");
                }

                if (containersArray != null) {
                    for (Object contObj : containersArray) {
                        JSONObject contJson = (JSONObject) contObj;
                        // Suporta "code" ou "codigo"
                        String contCode = contJson.containsKey("code") ? (String) contJson.get("code")
                                : (String) contJson.get("codigo");
                        // Suporta "capacity" ou "capacidade"
                        double capacity = contJson.containsKey("capacity") ? toDouble(contJson.get("capacity"))
                                : toDouble(contJson.get("capacidade"));

                        // Obter ItemType. Se não houver "type", inferir do código
                        ItemType itemType = null;
                        if (contJson.containsKey("type")) {
                            String typeStr = (String) contJson.get("type");
                            itemType = ItemType.valueOf(typeStr.toUpperCase().trim());
                        } else {
                            itemType = getItemTypeFromCode(contCode);
                        }

                        if (itemType != null) {
                            Container container = new ContainerImpl(capacity, contCode, itemType);
                            try {
                                aidBox.addContainer(container);
                            } catch (ContainerException e) {
                                System.err.println("Erro ao adicionar contentor: " + e.getMessage());
                            }
                        }
                    }
                }

                try {
                    institution.addAidBox(aidBox);
                } catch (AidBoxException e) {
                    throw new InstitutionException("Erro ao associar caixa: " + e.getMessage());
                }
            }

            // 2. LER DISTÂNCIAS (Distances)
            FileReader readerDistances = new FileReader(this.distanceFile);
            JSONArray distancesArray = (JSONArray) parser.parse(readerDistances);
            readerDistances.close();

            AidBox[] registeredBoxes = institution.getAidBoxes();

            for (Object obj : distancesArray) {
                JSONObject connJson = (JSONObject) obj;

                String fromCode = (String) connJson.get("from");

                // Se o "to" for um JSONArray (nova estrutura de distâncias em árvore)
                if (connJson.get("to") instanceof JSONArray) {
                    JSONArray toList = (JSONArray) connJson.get("to");
                    for (Object toObj : toList) {
                        JSONObject toMap = (JSONObject) toObj;
                        String toName = (String) toMap.get("name");
                        double distance = toDouble(toMap.get("distance"));
                        double duration = toDouble(toMap.get("duration"));

                        if (toName.equalsIgnoreCase("Base")) {
                            // Regista a distância da Base até a esta AidBox de origem
                            AidBox origin = findAidBox(registeredBoxes, fromCode);
                            if (origin != null) {
                                if (institution instanceof pt.ipp.estg.pp.core.InstitutionImpl) {
                                    ((pt.ipp.estg.pp.core.InstitutionImpl) institution).addDistance(origin, distance);
                                }
                            }
                        } else {
                            AidBox origin = findAidBox(registeredBoxes, fromCode);
                            AidBox destination = findAidBox(registeredBoxes, toName);
                            if (origin != null && destination != null && origin instanceof AidBoxImpl) {
                                ((AidBoxImpl) origin).addDistance(destination, distance, duration);
                            }
                        }
                    }
                } else {
                    // Estrutura plana antiga
                    String toCode = (String) connJson.get("to");
                    double distance = toDouble(connJson.get("distance"));
                    double duration = toDouble(connJson.get("duration"));

                    AidBox destination = findAidBox(registeredBoxes, toCode);

                    if (destination != null) {
                        if (fromCode.equalsIgnoreCase("Base")) {
                            if (institution instanceof pt.ipp.estg.pp.core.InstitutionImpl) {
                                ((pt.ipp.estg.pp.core.InstitutionImpl) institution).addDistance(destination, distance);
                            }
                        } else {
                            AidBox origin = findAidBox(registeredBoxes, fromCode);
                            if (origin != null && origin instanceof AidBoxImpl) {
                                ((AidBoxImpl) origin).addDistance(destination, distance, duration);
                            }
                        }
                    }
                }
            }

        } catch (ParseException e) {
            throw new IOException("Erro a ler o JSON: " + e.getMessage(), e);
        }
    }

    private ItemType getItemTypeFromCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        char first = Character.toUpperCase(code.charAt(0));
        switch (first) {
            case 'N':
                return ItemType.NON_PERISHABLE_FOOD;
            case 'P':
                return ItemType.PERISHABLE_FOOD;
            case 'M':
                return ItemType.MEDICINE;
            case 'V':
                return ItemType.CLOTHING;
            default:
                return null;
        }
    }

    private AidBox findAidBox(AidBox[] boxes, String code) {
        if (boxes == null || code == null)
            return null;
        for (AidBox box : boxes) {
            if (box.getCode().equalsIgnoreCase(code)) {
                return box;
            }
        }
        return null;
    }

    private double toDouble(Object val) {
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        return 0.0;
    }
}
