package de.cineaste.android.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import de.cineaste.android.Constants;


/**
 * Created by christianbraun on 17/11/15.
 */
public abstract class BaseDao extends SQLiteOpenHelper {

    private Context context;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";

    protected SQLiteDatabase readDb;
    protected SQLiteDatabase writeDb;

    public static final int DATABASE_VERSION = Constants.DATABASE_VERSION;
    public static final String DATABASE_NAME = Constants.DATABASE_NAME;

    public static abstract class UserEntry implements BaseColumns {


        public static final String TABLE_NAME = "user";
        public static final String COLUMN_USER_NAME = "userName";
    }

    private static final String SQL_CREATE_USER_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + UserEntry.TABLE_NAME + " (" +
                    UserEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    UserEntry.COLUMN_USER_NAME + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_USER_ENTRIES =
            "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;


    public static abstract class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String COlUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_VOTE_COUNT = "voteCount";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_WATCHED = "watched";

    }


    private static final String SQL_CREATE_MOVIE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + MovieEntry.TABLE_NAME + " (" +
                    MovieEntry._ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    MovieEntry.COLUMN_MOVIE_TITLE + TEXT_TYPE + COMMA_SEP +
                    MovieEntry.COlUMN_POSTER_PATH + TEXT_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_RUNTIME + INTEGER_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_VOTE_AVERAGE + REAL_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_VOTE_COUNT + INTEGER_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_MOVIE_WATCHED + INTEGER_TYPE +
                    " )";

    private static final String SQL_DELETE_MOVIE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;


    public BaseDao( Context context ) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
        this.readDb = getReadableDatabase();
        this.writeDb = getWritableDatabase();
    }

    @Override
    public void onCreate( SQLiteDatabase db ) {
        db.execSQL( SQL_CREATE_USER_ENTRIES );
        db.execSQL( SQL_CREATE_MOVIE_ENTRIES );
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int i, int i1 ) {
        db.execSQL( SQL_DELETE_USER_ENTRIES );
        db.execSQL( SQL_DELETE_MOVIE_ENTRIES );
        onCreate( db );
    }
}