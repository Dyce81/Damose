package Controller;

public class CredentialChecker
{
    //0 = user troppo corto, 1 user troppo lungo, 2 pswd troppo corta, 3 pswd troppo lunga, 4 user già esistente, 5 nessun problema
    public static int registra_utente(String username, String password)
    {
        if (username.length() < 5) {return 0;}
        else if (username.length() > 15) {return 1;}
        else if (password.length() < 6) {return 2;}
        else if (password.length() > 12) {return 3;}
        return 4;
    }
}
