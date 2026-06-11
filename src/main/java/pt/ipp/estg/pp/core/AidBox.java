package pt.ipp.estg.pp.core;

public interface AidBox extends Cloneable{
    public boolean addContainer(Container container);

    public String getCode();
    
    public Container getContainer(ItemType itemType);

    public Container[] getContainers();

    public GeographicCoordinates getCoordinates();

    public double getDistance(AidBox aidBox);

    public double getDuration(AidBox aidBox);

    public String getRefLocal();

    public String getZone();
}
