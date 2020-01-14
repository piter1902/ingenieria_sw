package es.unizar.eina.notepadv3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * <p>
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class CategoryDbAdapter {

    public static final String KEY_TITLE = "title";
    public static final String KEY_ROWID = "_id";
    private static final String TAG = "CategoryDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
            "create table categories (_id integer primary key autoincrement, "
                    + "title text not null unique);";


    private static final String DATABASE_NAME = "dataNotes";
    public static final String DATABASE_TABLE = "categories";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    /**
     * Metodo que devuelve el id asociado a la categoria
     *
     * @param cat categoria a obtener el ID
     * @return id de la categoria o -1 si no existe
     */
    public long getCatID(String cat) {
        Cursor c = mDb.query(true, DATABASE_TABLE, new String[]{
                        KEY_ROWID}, KEY_TITLE + "='" + cat + "'", null,
                null, null, null, null);

        if (c.getCount() != 0) {
            c.moveToNext();
            return c.getInt(c.getColumnIndexOrThrow(KEY_ROWID));
        } else {
            // Es 0 -> No existe
            return -1;
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
                db.execSQL(DATABASE_CREATE);

            // TODO: ejecutar aqui el insert de la categoria 0 con cadena vacia? (sin agrupar)
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS categories");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public CategoryDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     * initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public CategoryDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @param title the title of the note
     * @return rowId or -1 if failed
     */
    public long createCategory(String title) throws SQLException {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        Log.d(TAG, "Creo tabla");
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of category to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteCategory(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllCategories() {

        //return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
        //       KEY_BODY}, null, null, null, null, null);
        return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID,
                KEY_TITLE}, null, null, null, null, KEY_TITLE);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchCategory(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[]{
                                KEY_ROWID, KEY_TITLE}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     *
     */
    public boolean exists_category(String title) {
        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE, new String[]{
                                KEY_ROWID, KEY_TITLE}, KEY_TITLE + "=" + '"' + title + '"', null,
                        null, null, null, null);
        return mCursor.getCount() != 0;
    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * @param rowId id of note to update
     * @param title value to set note title to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateCategory(long rowId, String title) throws SQLException {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}