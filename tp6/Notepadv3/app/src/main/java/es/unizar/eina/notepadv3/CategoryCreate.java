package es.unizar.eina.notepadv3;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CategoryCreate extends AppCompatActivity {

    private EditText mIdText;
    private EditText mTitleText;
    private Long mRowId;
    private CategoryDbAdapter mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new CategoryDbAdapter(this);
        mDbHelper.open();
        setContentView(R.layout.activity_category_create);
        setTitle(R.string.create_category);

        mTitleText = (EditText) findViewById(R.id.titleCategory);
        mIdText = (EditText) findViewById(R.id.idCategory);

        Button confirmButton = (Button) findViewById(R.id.confirmCategory);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(CategoryDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = (extras != null) ? extras.getLong(CategoryDbAdapter.KEY_ROWID)
                    : null;
        }

        populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (mTitleText.getText().toString().trim().equals("")) {
                    Toast.makeText(CategoryCreate.this, "El texto no puede ser la cadena vacía", Toast.LENGTH_SHORT).show();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }

            }

        });
    }

    private void populateFields() {
        if (mRowId != null) {
            Cursor category = mDbHelper.fetchCategory(mRowId);
            startManagingCursor(category);
            mTitleText.setText(category.getString(
                    category.getColumnIndexOrThrow(CategoryDbAdapter.KEY_TITLE)));
            mIdText.setText(category.getString(
                    category.getColumnIndexOrThrow(CategoryDbAdapter.KEY_ROWID)));
        } else { //En caso de que sea nulo, es que se está creando la categoria.
            mIdText.setText("***");
        }
    }

    private void saveState() {

        String title = mTitleText.getText().toString();

        if (mRowId == null) {
            long id = 0;
            Log.d("POLLA", String.format("Existe? %s", mDbHelper.exists_category(title)));
            if (!mDbHelper.exists_category(title)) {
                id = mDbHelper.createCategory(title);
            } else {
                Toast.makeText(CategoryCreate.this, "Categoría duplicada.", Toast.LENGTH_SHORT).show();
            }
            if (id > 0) {
                mRowId = id;
            }
        } else {
            if (!mDbHelper.exists_category(title)) {
                mDbHelper.updateCategory(mRowId, title);
            } else {
                Toast.makeText(CategoryCreate.this, "Categoría duplicada.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(CategoryDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mTitleText.getText().toString().trim().equals("")) {
            saveState();
        } else {
            Toast.makeText(CategoryCreate.this, "Nota vacía", Toast.LENGTH_SHORT).show();
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
