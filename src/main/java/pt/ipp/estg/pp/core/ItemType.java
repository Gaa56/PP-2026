package pt.ipp.estg.pp.core;

public enum ItemType {
    CLOTHING, MEDICINE, NON_PERISHABLE_FOOD, PERISHABLE_FOOD;

    public static String itemTypeToString(ItemType itemType) {
        switch (itemType) {
            case CLOTHING:
                return "Clothing";
            case MEDICINE:
                return "Medicine";
            case NON_PERISHABLE_FOOD:
                return "Non-Perishable Food";
            case PERISHABLE_FOOD:
                return "Perishable Food";
            default:
                return "Unknown";
        }
    }
}
