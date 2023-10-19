package org.radiofeperu;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    // Define una URL de muestra (reemplaza con tu propia URL)
    private static final String SHOUTCAST_METADATA_URL = "http://us1freenew.listen2myradio.com:14690";
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
                if ("org.radiofeperu.ACCION_MOSTRAR_ALERTA_NOTIFICACION".equals(intent.getAction())) {
                    // La reproducción se ha detenido, cambia el estado del Switch a "apagado"
                    //Toast.makeText(context,"Desde el braccast",Toast.LENGTH_LONG).show();
                    showNotificationPermissionAlert();
                }

            }
        };
        // Registra el BroadcastReceiver para escuchar la acción de detener
        IntentFilter filter = new IntentFilter("org.radiofeperu.ACCION_DETENER_REPRODUCCION");
        registerReceiver(stopReceiver, filter);

        IntentFilter filter2 = new IntentFilter("org.radiofeperu.ACCION_MOSTRAR_ALERTA_NOTIFICACION");
        registerReceiver(stopReceiver, filter2);


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

    // Muestra el mensaje y agrega un botón para solicitar permiso
    private void showNotificationPermissionAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Permisos de notificación")
                .setMessage("Para usar esta función, habilita las notificaciones en la configuración de la aplicación.")
                .setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Abre la configuración de notificaciones de la aplicación
                        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }




        @Override
    protected void onDestroy() {
        unregisterReceiver(stopReceiver);
        super.onDestroy();
    }
}