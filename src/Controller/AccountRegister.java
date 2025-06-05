package Controller;
import java.util.Vector;

public class AccountRegister
{
    //Da sistemare: username e password non vengono memorizzati se si chiude l'applicazione
    static final Vector<String> usernames = new Vector<>();
    static final Vector<String> passwords = new Vector<>();

    //0 = user troppo corto, 1 user troppo lungo, 2 pswd troppo corta, 3 pswd troppo lunga, 4 user già esistente, 5 nessun problema
    public static int registra_utente(String username, String password)
    {
        if (username.length() < 5) {return 0;}
        else if (username.length() > 15) {return 1;}
        else if (password.length() < 6) {return 2;}
        else if (password.length() > 12) {return 3;}
        for (String i : usernames)
        {
            if (username.equals(i)) {return 4;}
        }
        usernames.add(username);
        passwords.add(password);
        return 5;
    }

    public static void mostra_credenziali()
    {
        System.out.println(usernames);
        System.out.println(passwords);
    }

    public static void elimina_credenziali()
    {
        usernames.clear();
        passwords.clear();
    }
}
