package es.unizar.eina.notepadv3;

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

import es.unizar.eina.send.MailImplementor;
import es.unizar.eina.send.SendAbstractionImpl;


public class Notepadv3 extends AppCompatActivity {

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;
    private static final int SEND_NOTE_ID = Menu.FIRST + 3;
    private static final int TEST_ID = Menu.FIRST + 4;
    private static final int TEST_CARGA_ID = Menu.FIRST + 5;
    private static final int TEST_SOBRECARGA_ID = Menu.FIRST + 6;

    private NotesDbAdapter mDbHelper;
    private ListView mList;
    private long notePosition;

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
        Toast.makeText(Notepadv3.this, "Posicion a mostrar " + notePosition, Toast.LENGTH_SHORT).show();
        mList.setSelection((int) notePosition);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, INSERT_ID, Menu.NONE, R.string.menu_insert);
        menu.add(Menu.NONE, TEST_ID, Menu.NONE, R.string.menu_test);
        menu.add(Menu.NONE, TEST_CARGA_ID, Menu.NONE, R.string.menu_carga_test);
        menu.add(Menu.NONE, TEST_SOBRECARGA_ID, Menu.NONE, R.string.menu_sobrecarga_test);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
            case TEST_ID:
                ejecutar_test();
                return true;
            case TEST_CARGA_ID:
                ejecutar_test_carga();
                return true;
            case TEST_SOBRECARGA_ID:
                ejecutar_test_sobrecarga();
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
                int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
                notePosition = position;
                mDbHelper.deleteNote(info.id);
                fillData();
                return true;
            case EDIT_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
                notePosition = position;
                editNote(info.position, info.id);
                return true;
            case SEND_NOTE_ID:
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
                notePosition = position;
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
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createNote() {
        // Se actualiza la ultima posicion de nota visitada
        notePosition = mList.getCount();
        Intent i = new Intent(this, NoteEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    /**
     * Método encargado de ejecutar casos de prueba necesarios para práctica 6
     */
    private void ejecutar_test() {
        Test test = new Test(this);
        //Pruebas de createNote
        test.test_createNote();
        //Pruebas updateNote
        test.test_updateNote();
        //Pruebas deleteNote
        test.test_deleteNote();
        Toast.makeText(Notepadv3.this, "Test ejecutados.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Método encargado de realizar las pruebas de carga
     */
    private void ejecutar_test_carga() {
        Test test = new Test(this);
        // Creamos las 1000 notas
        test.test_carga();
        Toast.makeText(Notepadv3.this, "Test carga ejecutados.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Método encargado de realizar las pruebas de sobrecarga
     */
    private void ejecutar_test_sobrecarga() {
        Test test = new Test(this);
        // Creamos las 1000 notas
        test.test_sobrecarga();
        Toast.makeText(Notepadv3.this, "Test sobrecarga ejecutados.", Toast.LENGTH_SHORT).show();
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
