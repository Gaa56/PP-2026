package pt.ipp.estg.pp.core;

public interface AidBox {
    boolean addContainer(Container container);

    String getCode();
    
    Container getContainer(ItemType itemType);

    Container[] getContainers();

    GeographicCoordinates getCoordinates();

    double getDistance(AidBox aidBox);

    double getDuration(AidBox aidBox);

    String getRefLocal();

    String getZone();
}
