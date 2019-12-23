package es.unizar.eina.notepadv3;

import android.content.Context;
import android.util.Log;

public class Test {
    private NotesDbAdapter mDbHelper;

    public Test(Context ctx) {
        // Al crear el test, se elimina la base de datos para la correcta ejecución de los mismos
        ctx.deleteDatabase("data");
        mDbHelper = new NotesDbAdapter(ctx);
        mDbHelper.open();
        Log.d("TEST", "---- Inicializando test ----");
    }

    public void test_createNote() {
        long id_returned;
        //CP 2
        try {
            id_returned = mDbHelper.createNote("Titulo prueba", "Cuerpo prueba");
            Log.d("TEST", "createNote() con parámetros (Título prueba, Cuerpo prueba) finalizado correctamente con id " + id_returned);
        } catch (Throwable t) {
            Log.d("TEST", "createNote() con parámetros (Título prueba, Cuerpo prueba) ha fallado. Causa: " + t);
        }
        //CP2
        try {
            id_returned = mDbHelper.createNote(null, "Cuerpo prueba");
            Log.d("TEST", "createNote() con parámetros (null, Cuerpo prueba) finalizado correctamente con id " + id_returned);
        } catch (Throwable t) {
            Log.d("TEST", "createNote() con parámetros (null, Cuerpo prueba) ha fallado. Causa: " + t);
        }
        //CP3
        try {
            id_returned = mDbHelper.createNote("", "Cuerpo prueba");
            Log.d("TEST", "createNote() con parámetros (\"\", Cuerpo prueba) finalizado correctamente con id " + id_returned);
        } catch (Throwable t) {
            Log.d("TEST", "createNote() con parámetros (\"\", Cuerpo prueba) ha fallado. Causa: " + t);
        }
        //Cp4
        try {
            id_returned = mDbHelper.createNote("Titulo prueba", null);
            Log.d("TEST", "createNote() con parámetros (Titulo prueba, null) finalizado correctamente con id " + id_returned);
        } catch (Throwable t) {
            Log.d("TEST", "createNote() con parámetros (Titulo prueba', null) ha fallado. Causa: " + t);
        }
    }

    public void test_deleteNote() {
        boolean resultado;
        //CP 2
        try {
            // Se da por hecho que la nota con rowId = 1, existe
            resultado = mDbHelper.deleteNote(1);
            Log.d("TEST", "deleteNote() con parámetro (1) finalizado correctamente con resultado " + resultado);
        } catch (Throwable t) {
            Log.d("TEST", "deleteNote() con parámetro (1) ha fallado. Causa: " + t);
        }
        //CP 2
        try {
            // Se da por hecho que la nota con rowId = 0, existe
            resultado = mDbHelper.deleteNote(0);
            Log.d("TEST", "deleteNote() con parámetro (0) finalizado correctamente con resultado " + resultado);
        } catch (Throwable t) {
            Log.d("TEST", "deleteNote() con parámetro (0) ha fallado. Causa: " + t);
        }
    }

    public void test_updateNote() {
        boolean resultado;
        //CP 2
        try {
            // Se da por hecho que la nota con rowId = 1, existe
            resultado = mDbHelper.updateNote(1, "Titulo prueba", "Cuerpo prueba");
            Log.d("TEST", "updateNote() con parámetros (1 ,Título prueba, Cuerpo prueba) finalizado correctamente con resultado " + resultado);
        } catch (Throwable t) {
            Log.d("TEST", "updateNote() concon parámetros (1 ,Título prueba, Cuerpo prueba) ha fallado. Causa: " + t);
        }
        //CP 2
        try {
            // Se da por hecho que la nota con rowId = 1, existe
            resultado = mDbHelper.updateNote(1, "", "Cuerpo prueba");
            Log.d("TEST", "updateNote() con parámetros (1, \"\", Cuerpo prueba) finalizado correctamente con resultado " + resultado);
        } catch (Throwable t) {
            Log.d("TEST", "updateNote() concon parámetros (1, \"\", Cuerpo prueba) ha fallado. Causa: " + t);
        }
        //CP 3
        try {
            // Se da por hecho que la nota con rowId = 1, existe
            resultado = mDbHelper.updateNote(1, null, "Cuerpo prueba");
            Log.d("TEST", "updateNote() con parámetros (1, null, Cuerpo prueba) finalizado correctamente con resultado " + resultado);
        } catch (Throwable t) {
            Log.d("TEST", "updateNote() concon parámetros (1, null, Cuerpo prueba) ha fallado. Causa: " + t);
        }
        //CP 4
        try {
            // Se da por hecho que la nota con rowId = 1, existe
            resultado = mDbHelper.updateNote(1, "Titulo prueba", null);
            Log.d("TEST", "updateNote() con parámetros (1, Titulo prueba, null) finalizado correctamente con resultado " + resultado);
        } catch (Throwable t) {
            Log.d("TEST", "updateNote() concon parámetros (1, Titulo prueba, null) ha fallado. Causa: " + t);
        }
        //CP 4
        try {
            resultado = mDbHelper.updateNote(0, "Titulo prueba", "Cuerpo prueba");
            Log.d("TEST", "updateNote() con parámetros (0 ,Título prueba, Cuerpo prueba) finalizado correctamente con resultado " + resultado);
        } catch (Throwable t) {
            Log.d("TEST", "updateNote() concon parámetros (0 ,Título prueba, Cuerpo prueba) ha fallado. Causa: " + t);
        }
    }
}
