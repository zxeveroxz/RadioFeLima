package org.radiofeperu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    private Switch switchStreaming;
    private BroadcastReceiver stopReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchStreaming = findViewById(R.id.switchStreaming);
        stopReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("com.servinuevafm.tutorialradio.ACCION_DETENER_REPRODUCCION".equals(intent.getAction())) {
                    // La reproducción se ha detenido, cambia el estado del Switch a "apagado"
                    switchStreaming.setChecked(false);
                }
            }
        };
        // Registra el BroadcastReceiver para escuchar la acción de detener
        IntentFilter filter = new IntentFilter("com.servinuevafm.tutorialradio.ACCION_DETENER_REPRODUCCION");
        registerReceiver(stopReceiver, filter);

        switchStreaming.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Intent serviceIntent = new Intent(this, AudioPlayerService.class);
            if (isChecked) {
                // Inicia el servicio para reproducir audio
                startService(serviceIntent);
            } else {
                // Detiene el servicio para detener la reproducción
                stopService(serviceIntent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(stopReceiver);
        super.onDestroy();
    }
}