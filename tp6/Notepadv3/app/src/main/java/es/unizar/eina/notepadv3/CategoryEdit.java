package es.unizar.eina.notepadv3;

        import android.content.Intent;
        import android.database.Cursor;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.ContextMenu;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ListView;
        import android.widget.SimpleCursorAdapter;
        import android.widget.Toast;

        import es.unizar.eina.send.SendAbstractionImpl;

public class CategoryEdit extends AppCompatActivity {

    private CategoryDbAdapter mDbHelper;
    private ListView mList;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit );
        mDbHelper = new CategoryDbAdapter(this);
        mDbHelper.open();
        mList = (ListView)findViewById(R.id.listCategory);
        fillData();

        registerForContextMenu(mList);

    }

    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor categoryCursor = mDbHelper.fetchAllCategoriesVisible();
        startManagingCursor(categoryCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[] { CategoryDbAdapter.KEY_TITLE };

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[] { R.id.text2};

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter categories =
                new SimpleCursorAdapter(this, R.layout.categories_row, categoryCursor, from, to);
        mList.setAdapter(categories);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, INSERT_ID, Menu.NONE, R.string.menu_insert);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                createCategory();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, DELETE_ID, Menu.NONE, R.string.menu_delete_category);
        menu.add(Menu.NONE, EDIT_ID, Menu.NONE, R.string.menu_edit_category);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                deleteCategory(item);
                return true;
            case EDIT_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                editCategory(info.position, info.id);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteCategory(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        mDbHelper.deleteCategory(info.id);
        fillData();
    }


    protected void editCategory(int position, long id) {
        Intent i = new Intent(this, CategoryCreate.class);
        i.putExtra(CategoryDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    private void createCategory() {
        Intent i = new Intent(this, CategoryCreate.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}
