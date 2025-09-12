package Controller;

public class LoginManager
{
    public static boolean logged = false;
    public static String username;

    public static int controllaCredenziali(String username, String password)
    {
        if (username.length() < 5) {return 0;}
        else if (username.length() > 15) {return 1;}
        else if (password.length() < 6) {return 2;}
        else if (password.length() > 12) {return 3;}
        else {return 4;}
    }

    public static boolean accedi(String user, String password)
    {
        if (DatabaseManager.userExists(user))
        {
            String hashedPassword = DatabaseManager.getUserPasswordHash(user);
            if (hashedPassword == null) {return false;}
            if (DatabaseManager.checkPassword(password, hashedPassword))
            {
                logged = true;
                username = user;
                return true;
            }
        }
        return false;

    }

    public static boolean registra(String user, String password)
    {
        if (DatabaseManager.userExists(user)) {return false;}
        else
        {
            DatabaseManager.addUser(user, password);
            logged = true;
            username = user;
            return true;
        }
    }

    public static void disconnetti()
    {
        logged = false;
        username = "";
    }

    public static boolean eliminaAccount(String username)
    {
        logged = false;
        return DatabaseManager.removeUser(username);
    }
}