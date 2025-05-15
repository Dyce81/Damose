package Controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/*                                  IMPORTANTE
    Sarebbe meglio usare una libreria come OpenCSV per leggere i file .csv; per il momento
    è stato implementato un metodo (dividi_stringa).
 */

public class ReaderStaticGTFS
{
    //Questo metodo restituisce un'arraylist di array, dove ciascuna lista interna
    //indica i valori di una singola fermata; quindi l'array esterno racchiude tutte le fermate.
    //Da lì si può poi generare ogni singola fermata (oggetto) sulla mappa
    public static ArrayList<String[]> leggi_csv(String path)
    {
        //Senza try.. catch non è possibile usare FileReader
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));

            ArrayList<String[]> lista_fermate = new ArrayList<String[]>();

            String linea;
            linea = reader.readLine(); //Ignora la prima riga (contiene i nomi dei campi)
            while ((linea = reader.readLine()) != null)
            {
                String[] valori = dividi_stringa(linea, ',').toArray(new String[0]);
                lista_fermate.add(valori);
            }

            return lista_fermate;
        } catch (Exception e) {
            System.out.println("Impossibile leggere il file indicato. \n " + path);
            return new ArrayList<String[]>();
        }
    }

    //Metodo sostitutivo di String.split() - questo metodo divide una stringa in un Array in base
    //al carattere separatore scelto; a differenze di String.split(), in un caso come ",," il metodo
    //comunque restituisce un valore vuoto
    public static ArrayList<String> dividi_stringa(String valore, char separatore)
    {
        String buffer = "";
        ArrayList<String> lista = new ArrayList<String>();
        for (int c = 0; c < valore.length(); c++)
        {
            if (valore.charAt(c) == separatore)
            {
                lista.add(buffer);
                buffer = "";
                continue;
            }

            buffer += valore.charAt(c);
        }

        lista.add(buffer);
        //System.out.println(lista); // da rimuovere
        return lista;
    }
}
