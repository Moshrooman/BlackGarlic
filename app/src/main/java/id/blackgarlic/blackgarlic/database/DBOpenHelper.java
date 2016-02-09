package id.blackgarlic.blackgarlic.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

/**
 * Created by JustinKwik on 2/9/16.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";

    private static final String COMMA = ", ";

    private static final String CREATE_POST_TABLE = "CREATE TABLE " + DatabaseContract.PostTable.TABLE_NAME + " ("
            + DatabaseContract.PostTable.TITLE + TEXT_TYPE + COMMA
            + DatabaseContract.PostTable.IMAGELINK + TEXT_TYPE + " )";

    private static final String DROP_POST_TABLE = "DROP TABLE IF EXISTS " + DatabaseContract.PostTable.TABLE_NAME;


    public DBOpenHelper(Context context) {
        super(context, DatabaseContract.DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_POST_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(DROP_POST_TABLE);
        onCreate(db);
    }
}
