package es.unizar.eina.notepadv3;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.List;

import es.unizar.eina.send.MailImplementor;
import es.unizar.eina.send.SendAbstractionImpl;


public class Notepadv3 extends AppCompatActivity {

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
    private CategoryDbAdapter cDHelper;
    private ListView mList;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepadv3);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        cDHelper = new CategoryDbAdapter(this);
        cDHelper.open();
        mList = (ListView) findViewById(R.id.list);
        fillData();

        registerForContextMenu(mList);

    }

    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchAllNotes();
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
                fillData();
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
                    metodo = "CORREO";
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

    private List<String> fetchCategories() {
        List<String> lista_cat = new ArrayList<>();
        Cursor c = cDHelper.fetchAllCategories();
        while (c.moveToNext()) {
            lista_cat.add(c.getString(c.getColumnIndexOrThrow(CategoryDbAdapter.KEY_TITLE)));
        }
        return lista_cat;
    }

    private void filterNotes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final CharSequence[] items = new CharSequence[3];

        items[0] = "Ordenar alfabéticamente";
        items[1] = "Ordenar por categoría";
        items[2] = "Ordenar por quién la tiene mas grande";

        builder.setTitle("Filtrado")
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(
                                Notepadv3.this,
                                "Seleccionaste: " + items[which],
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
       
        builder.create().show();
    }

    protected void editNote(int position, long id) {
        Intent i = new Intent(this, NoteEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

}
