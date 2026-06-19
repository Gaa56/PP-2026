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
 * Implementação da interface Importer para carregar dados das caixas de suprimentos
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

                String code = (String) boxJson.get("code");
                String refLocal = (String) boxJson.get("refLocal");
                String zone = (String) boxJson.get("zone");

                double latitude = 0.0;
                double longitude = 0.0;
                if (boxJson.containsKey("coordinates")) {
                    JSONObject coordJson = (JSONObject) boxJson.get("coordinates");
                    latitude = toDouble(coordJson.get("latitude"));
                    longitude = toDouble(coordJson.get("longitude"));
                } else {
                    latitude = toDouble(boxJson.get("latitude"));
                    longitude = toDouble(boxJson.get("longitude"));
                }

                GeographicCoordinates coords = new GeographicCoordinatesImpl(latitude, longitude);
                AidBox aidBox = new AidBoxImpl(code, coords, refLocal, zone);

                JSONArray containersArray = (JSONArray) boxJson.get("containers");
                if (containersArray != null) {
                    for (Object contObj : containersArray) {
                        JSONObject contJson = (JSONObject) contObj;
                        String contCode = (String) contJson.get("code");
                        double capacity = toDouble(contJson.get("capacity"));
                        
                        String typeStr = (String) contJson.get("type");
                        ItemType itemType = ItemType.valueOf(typeStr.toUpperCase().trim());

                        Container container = new ContainerImpl(capacity, contCode, itemType);
                        try {
                            aidBox.addContainer(container);
                        } catch (ContainerException e) {
                            System.err.println("Erro ao adicionar contentor: " + e.getMessage());
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

        } catch (ParseException e) {
            throw new IOException("Erro a ler o JSON: " + e.getMessage(), e);
        }
    }

    private AidBox findAidBox(AidBox[] boxes, String code) {
        if (boxes == null || code == null) return null;
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
