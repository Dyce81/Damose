package Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *  Una semplice classe che legge un file. Contiene un metodo per dividere il testo di un file in
 *  più stringhe.
 */
public class FileReader
{
    /**
     * Legge il file di testo specificato e restituisce una lista di stringhe ottenute
     * dividendo il contenuto del file utilizzando la virgola come separatore.
     * @param filePath il percorso del file da leggere.
     * @return lista di stringhe.
     * @throws IOException errore generico durante la lettura del file.
     */
    public static String[] getFrasi(String filePath) throws IOException
    {
        Path path = Paths.get(filePath);
        String contenutoFile = Files.readString(path);

        return contenutoFile.split(",");
    }
}
