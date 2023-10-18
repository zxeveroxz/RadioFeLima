package org.radiofeperu;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Switch;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    // Define una URL de muestra (reemplaza con tu propia URL)
    private static final String SHOUTCAST_METADATA_URL = "http://us1freenew.listen2myradio.com:14690/index.html";
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private Timer metadataTimer;
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
                if ("org.radiofeperu.ACCION_DETENER_REPRODUCCION".equals(intent.getAction())) {
                    // La reproducción se ha detenido, cambia el estado del Switch a "apagado"
                    switchStreaming.setChecked(false);
                }
            }
        };
        // Registra el BroadcastReceiver para escuchar la acción de detener
        IntentFilter filter = new IntentFilter("org.radiofeperu.ACCION_DETENER_REPRODUCCION");
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

        startMetadataUpdateTimer();
    }

    private void startMetadataUpdateTimer() {
        metadataTimer = new Timer();
        metadataTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                fetchShoutcastMetadata();
            }
        }, 0, 20000); // METADATA_UPDATE_INTERVAL es el intervalo en milisegundos
    }


    // Método para obtener los metadatos de Shoutcast
    private void fetchShoutcastMetadata() {
        // Crea un cliente OkHttpClient para realizar la solicitud
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(SHOUTCAST_METADATA_URL)
                .build();

        Log.i("Datos", "inicio");

            try {
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String responseString = response.body().string();

                    Log.d("FetchMetadataThread", "Hilo en segundo plano iniciado " );

                    // Analiza los metadatos (esto dependerá del formato real)
                    // Aquí se hace una suposición simple de que los metadatos están en un formato 'clave=valor'
                    String[] metadataParts = responseString.split(";");
                    String songTitle = "";
                    String streamUrl = "";

                    for (String metadataPart : metadataParts) {
                        if (metadataPart.contains("StreamTitle=")) {
                            songTitle = metadataPart.replace("StreamTitle=", "").replace("'", "");
                        } else if (metadataPart.contains("StreamUrl=")) {
                            streamUrl = metadataPart.replace("StreamUrl=", "").replace("'", "");
                        }
                    }

                    // Actualiza la IU con los metadatos (debes definir TextViews u otros elementos en tu IU)
                    uiHandler.post(() -> {
                        // Actualiza tus TextViews con songTitle y streamUrl
                        //textViewSongTitle.setText(songTitle);
                        //textViewStreamUrl.setText(streamUrl);
                        Log.d("FetchMetadataThread", "Hilo en segundo plano iniciado " );
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                //Log.i("Datos", e.getMessage().toString());
            }



    }


        @Override
    protected void onDestroy() {
        unregisterReceiver(stopReceiver);
        super.onDestroy();
    }
}