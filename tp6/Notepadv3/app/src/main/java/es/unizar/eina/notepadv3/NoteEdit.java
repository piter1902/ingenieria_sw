package es.unizar.eina.notepadv3;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NoteEdit extends AppCompatActivity {

    private EditText mIdText;
    private EditText mTitleText;
    private EditText mBodyText;
    private Spinner mSpinner;
    private Long mRowId;
    // ID para el tratamiento de las categorias
    private Long catRowID;
    // Para acceso a las BD de la aplicacion
    private NotesDbAdapter mDbHelper;
    private CategoryDbAdapter cDHelper;

    private final String TAG = "NOTEEDIT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        setContentView(R.layout.note_edit);
        setTitle(R.string.edit_note);

        cDHelper = new CategoryDbAdapter(this);
        cDHelper.open();

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mIdText = (EditText) findViewById(R.id.id);
        mSpinner = (Spinner) findViewById(R.id.spinner);

        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);


        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null) ? extras.getLong(NotesDbAdapter.KEY_ROWID)
                    : null;
        }

        populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (mTitleText.getText().toString().trim().equals("")) {
                    Toast.makeText(NoteEdit.this, "El texto no puede ser la cadena vacía", Toast.LENGTH_SHORT).show();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }

        });
    }

    private void populateFields() {
        List<String> datos = fetchCategories();
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);


            catRowID = note.getLong(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_CATEGORY));
            // TODO: esto no deberia salir null -> Parece que funciona
            Log.d(TAG, String.format("catRowID: %d", catRowID));

            // TODO: comprobar que esto funciona: SPOILER -> Parece que si
            if (catRowID != null && catRowID > 0) {
                // La nota tiene categoría -> Reordenamos el array para ponerla en primera posicion
                Cursor c = cDHelper.fetchCategory(catRowID);
                // En caso contrario no se hace nada
                if (c.getCount() != 0) {
                    // Existe la categoria
//                    c.moveToNext();
                    String cat_name = c.getString(c.getColumnIndexOrThrow(CategoryDbAdapter.KEY_TITLE));
                    datos.remove(cat_name);
                    datos.add(0, cat_name);
                }
            } else {
                // Caso de que nota no tiene categoría. Si queremos que así permanezca, se le tiene que dar la opción.
                datos.add(0, "");
            }


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, datos);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            mSpinner.setAdapter(adapter);
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
            mIdText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWID)));
        } else { //En caso de que sea nulo, es que se está creando la nota.
            mIdText.setText("***");
            // Si estamos creando nota, la categoría por defecto va a ser la 0, cuyo title=""
            datos.add(0, "");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, datos);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            mSpinner.setAdapter(adapter);
        }
    }

    private List<String> fetchCategories() {
        List<String> lista_cat = new ArrayList<>();
        Cursor c = cDHelper.fetchAllCategories();
        while (c.moveToNext()) {
            lista_cat.add(c.getString(c.getColumnIndexOrThrow(CategoryDbAdapter.KEY_TITLE)));
        }
        return lista_cat;
    }

    private void saveState() {

        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();
        Object cat = mSpinner.getSelectedItem();

        long catID = 0;
        // Comprobamos que ha seleccionado categoría
        if (cat.toString() != "") {
            Log.d(TAG, "Seleccionado categoría por defecto.");
                catID = cDHelper.getCatID(cat.toString());
        }

        if (mRowId == null) {
            long id = mDbHelper.createNote(title, body, catID);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateNote(mRowId, title, body, catID);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mTitleText.getText().toString().trim().equals("")) {
            saveState();
        } else {
            Toast.makeText(NoteEdit.this, "Nota vacía", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_edit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
