package pt.ipp.estg.pp.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import pt.ipp.estg.pp.core.Institution;
import pt.ipp.estg.pp.core.exceptions.InstitutionException;

public interface Importer {
    void importData (Institution institution) throws FileNotFoundException, IOException, InstitutionException;
}
