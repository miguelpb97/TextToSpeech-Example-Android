package com.mapb.texttospeech_sample;

import android.view.View;
import android.widget.AdapterView;

/**
 * Adaptador de {@link AdapterView.OnItemSelectedListener} que permite usar una
 * lambda cuando solo nos interesa {@code onItemSelected}, ya que la interfaz
 * original de Android no es funcional (tiene dos metodos abstractos).
 */
public class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {

    /** Se invoca con la posicion seleccionada del adapter. */
    public interface OnPositionSelected {
        void onSelected(int position);
    }

    private final OnPositionSelected callback;

    public SimpleItemSelectedListener(OnPositionSelected callback) {
        this.callback = callback;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        callback.onSelected(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // No se necesita ninguna accion cuando no hay seleccion.
    }
}
