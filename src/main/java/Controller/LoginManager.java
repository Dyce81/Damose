package Controller;

public class LoginManager
{
    public static boolean logged = false;

    public static int controllaCredenziali(String username, String password)
    {
        if (username.length() < 5) {return 0;}
        else if (username.length() > 15) {return 1;}
        else if (password.length() < 6) {return 2;}
        else if (password.length() > 12) {return 3;}
        else {return 4;}
    }

    public static boolean accedi(String username, String password)
    {
        String hashedPassword = DatabaseManager.getUserPasswordHash(username);
        if (hashedPassword == null) {return false;}
        if (DatabaseManager.checkPassword(password, hashedPassword) && DatabaseManager.userExists(username))
        {
            logged = true;
            return true;
        }
        else {return false;}

    }

    public static boolean registra(String username, String password)
    {
        if (DatabaseManager.userExists(username)) {return false;}
        else
        {
            DatabaseManager.addUser(username, password);
            logged = true;
            return true;
        }
    }

    public static void disconnetti()
    {
        logged = false;
    }
}