package es.unizar.eina.notepadv3;

import android.app.AlertDialog;
//import android.app.DialogFragment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import es.unizar.eina.send.SendAbstractionImpl;


public class Notepadv3 extends AppCompatActivity implements NoticeDialogFragment.NoticeDialogListener {

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int ACTIVITY_EDIT_CATEGORY = 2;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;
    private static final int SEND_NOTE_ID = Menu.FIRST + 3;
    private static final int EDIT_CATEGORY_ID = Menu.FIRST + 4;
    private static final int FILTER_NOTE = Menu.FIRST + 5;

    private NotesDbAdapter mDbHelper;
    private ListView mList;
    private String actualQuery;
    private long actualCategory;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepadv3);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        mList = (ListView) findViewById(R.id.list);
        // En un primer momento, las notas se muestran organizadas por nombre
        if (actualQuery == null || actualQuery.trim().equals(""))
            actualQuery = "nameFilter";
        fillData(actualQuery, actualCategory);

        registerForContextMenu(mList);

    }

    private void fillData(String query, long category) {
        // Get all of the notes from the database and create the item list
        actualQuery = query;
        actualCategory = category;
        Cursor notesCursor = null;
        switch (query) {
            case "nameFilter":
                notesCursor = mDbHelper.fetchNotesByName();
                break;
            case "dateFilter":
                notesCursor = mDbHelper.fetchNotesByDate();
                break;
            case "categoryFilter":
                notesCursor = mDbHelper.fetchNotesByCategory(category);
                break;
            case "allNotesGroupByCategory":
                notesCursor = mDbHelper.fetchAllNotesGroupByCategory();
                break;
        }

        startManagingCursor(notesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{NotesDbAdapter.KEY_TITLE};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.notes_row, notesCursor, from, to);
        mList.setAdapter(notes);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, INSERT_ID, Menu.NONE, R.string.menu_insert);
        menu.add(Menu.NONE, EDIT_CATEGORY_ID, Menu.NONE, R.string.menu_edit_all_categories);
        menu.add(Menu.NONE, FILTER_NOTE, Menu.NONE, R.string.menu_filter_notes);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
            case EDIT_CATEGORY_ID:
                editCategory();
                return true;
            case FILTER_NOTE:
                filterNotes();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, DELETE_ID, Menu.NONE, R.string.menu_delete);
        menu.add(Menu.NONE, EDIT_ID, Menu.NONE, R.string.menu_edit);
        menu.add(Menu.NONE, SEND_NOTE_ID, Menu.NONE, R.string.menu_share_note);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteNote(info.id);
                fillData(actualQuery, actualCategory);
                return true;
            case EDIT_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                editNote(info.position, info.id);
                return true;
            case SEND_NOTE_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Cursor note = mDbHelper.fetchNote(info.id);
                String title = note.getString(
                        note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));
                String body = note.getString(
                        note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));
                String metodo = null;
                if (body.length() < 100) {
                    metodo = "SMS";
                } else {
                    metodo = "MAIL";
                }
                new SendAbstractionImpl(this, metodo).send(title, body);
        }
        return super.onContextItemSelected(item);
    }

    private void createNote() {
        Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    private void editCategory() {
        Intent i = new Intent(this, CategoryEdit.class);
        startActivityForResult(i, ACTIVITY_EDIT_CATEGORY);
    }


    private void filterNotes() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NoticeDialogFragment();
        FragmentManager prueba = getSupportFragmentManager();
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");

    }

    protected void editNote(int position, long id) {
        Intent i = new Intent(this, NoteEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData(actualQuery, actualCategory);
    }

    /**
     * Método que conecta la actividad con el diálogo. Se activa cuando es seleccionado el botón de filtrar
     *
     * @param dialog   diálogo del que proviene
     * @param query    cadena que indica cómo se van a mostrar las notas en pantalla
     * @param category categoría según la que filtraremos notas. Si no se ha seleccionado
     *                 filtrado por categoría, su valor es NULL
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String query, long category) {
        fillData(query, category);
    }


    /**
     * Método que conecta la actividad con el diálogo. Se activa cuando es seleccionado el botón de cancelar
     *
     * @param dialog
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

        Toast.makeText(
                this,
                "Seleccionado botón OFFF",
                Toast.LENGTH_SHORT)
                .show();
    }
}
