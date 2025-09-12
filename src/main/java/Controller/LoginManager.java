package Controller;

/**
 * Gestisce il login e le operazioni sugli account degli utenti.
 */
public class LoginManager
{
    public static boolean logged = false;
    public static String username;

    /**
     * Controlla che le credenziali inserire dall'utente rispettino i giusti criteri.
     * @param username nome utente da controllare.
     * @param password password da controllare.
     * @return int che specifica l'errore commesso dall'utente nel digitare la password o il nome.
     * 0 = username troppo corto;
     * 1 = username troppo lungo;
     * 2 = password troppo corta;
     * 3 = password troppo lunga;
     * 4 = la password rispetta tutti i criteri.
     */
    public static int controllaCredenziali(String username, String password)
    {
        if (username.length() < 5) return 0;
        else if (username.length() > 15) return 1;
        else if (password.length() < 6) return 2;
        else if (password.length() > 12) return 3;
        else return 4;
    }

    /**
     * Prova ad effettuare l'accesso con le credenziali inserite.
     * @param user il nome utente inserito.
     * @param password la password inserita.
     * @return true se l'accesso è andato a buon fine; false altrimenti.
     */
    public static boolean accedi(String user, String password)
    {
        if (DatabaseManager.userExists(user))
        {
            String hashedPassword = DatabaseManager.getUserPasswordHash(user);
            if (hashedPassword == null) return false;
            if (DatabaseManager.checkPassword(password, hashedPassword))
            {
                logged = true;
                username = user;
                return true;
            }
        }
        return false;
    }

    /**
     * Registra un nuovo utente.
     * @param user il nome utente da registrare.
     * @param password la password da registrare.
     * @return restituisce true se la registrazione è andata a buon fine, false altrimenti.
     */
    public static boolean registra(String user, String password)
    {
        if (DatabaseManager.userExists(user)) return false;
        else
        {
            DatabaseManager.addUser(user, password);
            logged = true;
            username = user;
            return true;
        }
    }

    /**
     * Disconnette l'utente attualmente loggato.
     */
    public static void disconnetti()
    {
        logged = false;
        username = "";
    }

    /**
     * Elimina l'account selezionato.
     * @param username il nome dell'utente da eliminare.
     * @return restituisce true se l'operazione è andata a buon fine, false altrimenti.
     */
    public static boolean eliminaAccount(String username)
    {
        logged = false;
        return DatabaseManager.removeUser(username);
    }
}