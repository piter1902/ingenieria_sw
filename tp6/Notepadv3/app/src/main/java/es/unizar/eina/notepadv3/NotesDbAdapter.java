package es.unizar.eina.notepadv3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
public class NotesDbAdapter {

    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";
    // Para la gestion de las categorias
    public static final String KEY_CATEGORY = "category";


    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */

    private static final String DATABASE_CREATE_CATEGORIES =
            "create table categories ( _id integer primary key autoincrement, "
                    + "title text not null unique);";

    private static final String DATABASE_CREATE_NOTES = "create table notes (_id integer primary key autoincrement, "
            + "title text not null, body text not null, category integer," +
            " CONSTRAINT FK_categories foreign key (category) references categories(_id));";

    /*
    private static final String DATABASE_CREATE_NOTES = "create table notes (_id integer primary key autoincrement, "
            + "title text not null, body text not null, category integer);";
    */
    private static final String DATABASE_NAME = "dataNotes";
    private static final String DATABASE_TABLE = "notes";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "Me estoy creando!!!!");
            db.execSQL(DATABASE_CREATE_CATEGORIES);
            db.execSQL(DATABASE_CREATE_NOTES);
            // Creacion de la categoría 0 -> Sin Categoria
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_ROWID, 0);
            initialValues.put(KEY_TITLE, "Sin Categoria");
            Log.d(TAG, "Insertando valor predefinido {0, Sin Categoria}");
            db.insert(CategoryDbAdapter.DATABASE_TABLE, null, initialValues);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
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
    public NotesDbAdapter(Context ctx) {
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
    public NotesDbAdapter open() throws SQLException {
        Log.d(TAG, "Me estoy abriendo.");
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
     * @param title    the title of the note
     * @param body     the body of the note
     * @param category id of the category which note pertains to.
     * @return rowId or -1 if failed
     */
    public long createNote(String title, String body, long category) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_BODY, body);
        initialValues.put(KEY_CATEGORY, category);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchNotesByName() {

        //return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
        //       KEY_BODY}, null, null, null, null, null);
        return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE,
                KEY_BODY, KEY_CATEGORY}, null, null, null, null, KEY_TITLE);
    }

    public Cursor fetchNotesByCategory(long category) {

        //return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
        //       KEY_BODY}, null, null, null, null, null);
        return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE,
                KEY_BODY, KEY_CATEGORY}, KEY_CATEGORY + "=" + category, null, null, null, null);
    }

    public Cursor fetchNotesByDate() {

        //return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
        //       KEY_BODY}, null, null, null, null, null);
        return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE,
                KEY_BODY, KEY_CATEGORY}, null, null, null, null, KEY_ROWID);
    }

    public Cursor fetchAllNotesGroupByCategory() {
        String rawQuery = "SELECT N." + NotesDbAdapter.KEY_ROWID + ", C." + CategoryDbAdapter.KEY_TITLE + " AS catTitle, N." + NotesDbAdapter.KEY_TITLE + " AS notesTitle FROM " +
                NotesDbAdapter.DATABASE_TABLE + " N INNER JOIN " + CategoryDbAdapter.DATABASE_TABLE + " C ON N." + NotesDbAdapter.KEY_CATEGORY + " = C."+
                CategoryDbAdapter.KEY_ROWID + " ORDER BY C." + CategoryDbAdapter.KEY_TITLE;
        return mDb.rawQuery(rawQuery, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchNote(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID,
                                KEY_TITLE, KEY_BODY, KEY_CATEGORY}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     *
     * @param rowId       id of note to update
     * @param title       value to set note title to
     * @param body        value to set note body to
     * @param category_id value of id of category which note pertains to.
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateNote(long rowId, String title, String body, long category_id) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_BODY, body);
        args.put(KEY_CATEGORY, category_id);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}