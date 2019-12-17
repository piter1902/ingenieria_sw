package es.unizar.eina.notepadv3;

import android.content.Context;
import android.util.Log;

public class Test {
    private NotesDbAdapter mDbHelper;

    public Test(Context ctx) {
        mDbHelper = new NotesDbAdapter(ctx);
        mDbHelper.open();
    }

    public void test_createNote() {
        long id_returned;
        //CP 2
        try {
            id_returned = mDbHelper.createNote("Titulo prueba", "Cuerpo prueba");
            Log.d("TEST", "createNote() con parámetros (Título prueba, Cuerpo prueba) finalizado correctamente con id " + id_returned);
        } catch (Throwable t) {
            Log.d("TEST", "createNote() con parámetros (Título prueba, Cuerpo prueba) ha fallado. Causa: " + t.getStackTrace());
        }
        //CP2
        try {
            id_returned = mDbHelper.createNote(null, "Cuerpo prueba");
            Log.d("TEST", "createNote() con parámetros (null, Cuerpo prueba) finalizado correctamente con id " + id_returned);
        } catch (Throwable t) {
            Log.d("TEST", "createNote() con parámetros (null, Cuerpo prueba) ha fallado. Causa: " + t.getStackTrace());
        }
        //CP3
        try {
            id_returned = mDbHelper.createNote("", "Cuerpo prueba");
            Log.d("TEST", "createNote() con parámetros (\"\", Cuerpo prueba) finalizado correctamente con id " + id_returned);
        } catch (Throwable t) {
            Log.d("TEST", "createNote() con parámetros (\"\", Cuerpo prueba) ha fallado. Causa: " + t.getStackTrace());
        }
        //Cp4
        try {
            id_returned = mDbHelper.createNote("Titulo prueba", "");
            Log.d("TEST", "createNote() con parámetros (Titulo prueba, \"\") finalizado correctamente con id " + id_returned);
        } catch (Throwable t) {
            Log.d("TEST", "createNote() con parámetros (Titulo prueba', \"\") ha fallado. Causa: " + t.getStackTrace());
        }
    }

    public void test_deleteNote() {
        boolean resultado;
        //CP 2
        try {
            // Se da por hecho que la nota con rowId = 2, existe
           resultado = mDbHelper.deleteNote(2);
            Log.d("TEST", "deleteNote() con parámetro (2) finalizado correctamente con resultado " + resultado);
        } catch (Throwable t) {
            Log.d("TEST", "deleteNote() con parámetro (2) ha fallado. Causa: " + t.getStackTrace());
        }
        //CP 2
        try {
            // Se da por hecho que la nota con rowId = 2, existe
            resultado = mDbHelper.deleteNote(0);
            Log.d("TEST", "deleteNote() con parámetro (0) finalizado correctamente con resultado " + resultado);
        } catch (Throwable t) {
            Log.d("TEST", "deleteNote() con parámetro (0) ha fallado. Causa: " + t.getStackTrace());
        }
    }

    public void test_updateNote() {
        boolean resultado;
        //CP 2
        try {
            // Se da por hecho que la nota con rowId = 3, existe
            resultado = mDbHelper.updateNote(3, "Titulo prueba", "Cuerpo prueba");
            Log.d("TEST", "updateNote() con parámetros (2 ,Título prueba, Cuerpo prueba) finalizado correctamente con resultado " + resultado);
        } catch (Throwable t) {
            Log.d("TEST", "updateNote() concon parámetros (2 ,Título prueba, Cuerpo prueba) ha fallado. Causa: " + t.getStackTrace());
        }
        //CP 2
        try {
            // Se da por hecho que la nota con rowId = 3, existe
            resultado = mDbHelper.updateNote(3, "", "Cuerpo prueba");
            Log.d("TEST", "updateNote() con parámetros (2, \"\", Cuerpo prueba) finalizado correctamente con resultado " + resultado);
        } catch (Throwable t) {
            Log.d("TEST", "updateNote() concon parámetros (2, \"\", Cuerpo prueba) ha fallado. Causa: " + t.getStackTrace());
        }
        //CP 3
        try {
            // Se da por hecho que la nota con rowId = 3, existe
            resultado = mDbHelper.updateNote(3, null, "Cuerpo prueba");
            Log.d("TEST", "updateNote() con parámetros (2, null, Cuerpo prueba) finalizado correctamente con resultado " + resultado);
        } catch (Throwable t) {
            Log.d("TEST", "updateNote() concon parámetros (2, null, Cuerpo prueba) ha fallado. Causa: " + t.getStackTrace());
        }
        //CP 4
        try {
            // Se da por hecho que la nota con rowId = 2, existe
            resultado = mDbHelper.updateNote(3, "Titulo prueba", "");
            Log.d("TEST", "updateNote() con parámetros (2, Titulo prueba, \"\") finalizado correctamente con resultado " + resultado);
        } catch (Throwable t) {
            Log.d("TEST", "updateNote() concon parámetros (2, Titulo prueba, \"\") ha fallado. Causa: " + t.getStackTrace());
        }
        //CP 4
        try {
            resultado = mDbHelper.updateNote(0, "Titulo prueba", "Cuerpo prueba");
            Log.d("TEST", "updateNote() con parámetros (0 ,Título prueba, Cuerpo prueba) finalizado correctamente con resultado " + resultado);
        } catch (Throwable t) {
            Log.d("TEST", "updateNote() concon parámetros (0 ,Título prueba, Cuerpo prueba) ha fallado. Causa: " + t.getStackTrace());
        }
    }
}
