package pt.ipp.estg.pp.core;

import com.estg.core.GeographicCoordinates;

/**
 * Representa as coordenadas geográficas (latitude e longitude) de uma localização.
 */
public class GeographicCoordinatesImpl implements GeographicCoordinates {

    private final double latitude;
    private final double longitude;

    /**
     * Cria uma coordenada padrão na origem (0.0, 0.0).
     */
    public GeographicCoordinatesImpl() {
        this(0.0, 0.0);
    }

    /**
     * Cria uma coordenada geográfica e valida se os valores estão dentro dos limites.
     *
     * @param latitude  latitude em graus (deve estar entre -90 e 90)
     * @param longitude longitude em graus (deve estar entre -180 e 180)
     * @throws IllegalArgumentException se os valores estiverem fora dos limites válidos
     */
    public GeographicCoordinatesImpl(double latitude, double longitude) {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("A latitude deve estar entre -90.0 e 90.0 graus.");
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("A longitude deve estar entre -180.0 e 180.0 graus.");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Devolve a latitude.
     */
    @Override
    public double getLatitude() {
        return latitude;
    }

    /**
     * Devolve a longitude.
     */
    @Override
    public double getLongitude() {
        return longitude;
    }

    /**
     * Compara se duas coordenadas têm os mesmos valores de latitude e longitude.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        GeographicCoordinatesImpl other = (GeographicCoordinatesImpl) obj;
        return Double.compare(this.latitude, other.latitude) == 0 &&
               Double.compare(this.longitude, other.longitude) == 0;
    }

    /**
     * Gera o código hash baseado na latitude e longitude.
     */
    @Override
    public int hashCode() {
        int result = 17;
        
        long latBits = Double.doubleToLongBits(this.latitude);
        result = 31 * result + (int) (latBits ^ (latBits >>> 32));
        
        long lonBits = Double.doubleToLongBits(this.longitude);
        result = 31 * result + (int) (lonBits ^ (lonBits >>> 32));
        
        return result;
    }

    /**
     * Devolve a coordenada representada em texto, usando StringBuilder.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append("}");
        return sb.toString();
    }
}
