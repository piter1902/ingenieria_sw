package es.unizar.eina.notepadv3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase creada para poder comunicar el dialog y la actividad que lo invoca
 */
public class NoticeDialogFragment extends DialogFragment {
    // Objeto para poder conectar con la base de datos de categorías
    private CategoryDbAdapter cDHelper;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String query, long category);

        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + "debe implementar NoticeDialogListener");
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Se accede a la base de datos de categorías
        cDHelper = new CategoryDbAdapter(getActivity());
        cDHelper.open();
        // Se toma el layout que representa la ventana emergente
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.filter_notes, null);
        builder.setView(v);
        builder.setTitle("Filtrar notas");
        // Obtenemos todos los obetos del layout
        final RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radioGroupFilter);
        final RadioButton nameFilter = (RadioButton) v.findViewById(R.id.nameFilter);
        final RadioButton dateFilter = (RadioButton) v.findViewById(R.id.dateFilter);
        final RadioButton categoryFilter = (RadioButton) v.findViewById(R.id.categoryFilter);
        // Obtenemos el spinner del layout
        final Spinner categorySpinner = (Spinner) v.findViewById(R.id.spinnerFilter);

        // Obtenemos la lista de categorías almacenadas en la base de datos
        List<String> datos = fetchCategories();
        // En primer lugar de la lista desplegable, se sitúa la lista vacía.
        datos.add(0, "");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, datos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Rellenamos la lista desplegable
        categorySpinner.setAdapter(adapter);
        builder.setPositiveButton("Filtrar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Send the positive button event back to the host activity
                // Comprobamos si se ha seleccionado un obeto de la lista de categorías
                Object cat = categorySpinner.getSelectedItem();
                // Obtenemos el radioGroup de filtrado
                int idButtonSelected = radioGroup.getCheckedRadioButtonId();
                // Ha seleccionado filtrado por nombre de nota
                if (idButtonSelected == nameFilter.getId()) {
                    listener.onDialogPositiveClick(NoticeDialogFragment.this, "nameFilter", -1);
                } else if (idButtonSelected == dateFilter.getId()) { //Selecciona filtrado por fecha de creación
                    listener.onDialogPositiveClick(NoticeDialogFragment.this, "dateFilter", -1);
                } else { // En caso contrario, se ha seleccionado botón de filtrado de categoría
                    Log.d("CATEGORY","Categoria: " + cat.toString());
                    if (cat.toString() != "") {
                        //Se comprueba que haya escogido una categoría
                        long catID = cDHelper.getCatID(cat.toString());
                        listener.onDialogPositiveClick(NoticeDialogFragment.this, "categoryFilter", catID);
                    } else { // Si no se ha seleccionado una categoría, no puede filtrar por categoría xd
                        Toast.makeText(
                                getActivity(),
                                "Por favor, seleccione categoría",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        }).

                setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        listener.onDialogNegativeClick(NoticeDialogFragment.this);
                    }
                });

        return builder.create();
    }

}
