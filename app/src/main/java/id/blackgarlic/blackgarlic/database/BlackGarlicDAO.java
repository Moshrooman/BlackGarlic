package id.blackgarlic.blackgarlic.database;

/**
 * Created by JustinKwik on 2/9/16.
 */
public class BlackGarlicDAO {

    private static BlackGarlicDAO sInstance;

    public static BlackGarlicDAO getInstance() {

        if (sInstance == null) {
            sInstance = new BlackGarlicDAO();
        }

        return sInstance;

    }

}
