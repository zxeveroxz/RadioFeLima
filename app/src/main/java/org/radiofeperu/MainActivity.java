package org.radiofeperu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Timer;


public class MainActivity extends AppCompatActivity {

    // Define una URL de muestra (reemplaza con tu propia URL)
    private static final String SHOUTCAST_METADATA_URL = "http://us1freenew.listen2myradio.com:14690";
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private Timer metadataTimer;
    private Switch switchStreaming;
    private BroadcastReceiver stopReceiver;

    private ImageView logoFe;

    private WebView web1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logoFe = findViewById(R.id.logoFe);

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

        web1 = findViewById(R.id.webView1);
        cargarWeb(web1);


        switchStreaming.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Intent serviceIntent = new Intent(this, AudioPlayerService.class);
            if (isChecked) {
                startService(serviceIntent);
                //AnimationUtil.startPeriodicRotationAnimation(logoFe,1,10);
            } else {
                stopService(serviceIntent);
                //AnimationUtil.stoptPeriodicRotationAnimation();
            }
        });


        //AnimationUtil.startPeriodicAlphaAnimation(logoFe,5);
    }

    // Muestra el mensaje y agrega un botón para solicitar permiso
    private void showNotificationPermissionAlert() {
        new AlertDialog.Builder(this)
                .setTitle("Permisos de notificación")
                .setMessage("Es necesario otorgar este permiso de notificacion para mantener reproduciendo Radio Fe, aun cuando tú telefono este bloqueado")
                .setPositiveButton("Activar el permiso", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Abre la configuración de notificaciones de la aplicación
                        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No Permitir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Cambia el estado del Switch a "apagado"
                        switchStreaming.setChecked(false);
                        Toast.makeText(getApplicationContext(),"Al no permitir los permisos de notificacion, la repodruccion se detendra en un tiempo corto.", Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }

        @Override
    protected void onDestroy() {
        unregisterReceiver(stopReceiver);
        super.onDestroy();
    }


    public void  cargarWeb(WebView web1){
        web1.setWebViewClient(new WebViewClient());
        // Habilita JavaScript (si es necesario)
        web1.getSettings().setJavaScriptEnabled(true);
        // Carga una URL en el WebView
        web1.loadUrl("https://radiofelima.radio12345.com/");
    }


    private void checkNotificationPermissionAndStartStreaming(boolean isChecked) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (!notificationManager.areNotificationsEnabled()) {

                Toast.makeText(this, "Para usar esta función, habilita las notificaciones en la configuración de la aplicación.", Toast.LENGTH_LONG).show();

                // Puedes abrir la configuración de notificaciones de la aplicación de esta manera:
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivity(intent);

                // También puedes implementar una solicitud de permiso personalizada aquí.
                // Por ejemplo, mostrando un diálogo de solicitud de permiso.

                // Luego, puedes manejar la respuesta del usuario para verificar si se concedieron permisos.
            } else {
                // Si tienes permiso, puedes iniciar la animación y el servicio.
                if (isChecked) {
                    Intent serviceIntent = new Intent(this, AudioPlayerService.class);
                    startService(serviceIntent);
                    AnimationUtil.startPeriodicRotationAnimation(logoFe, 1, 10);
                } else {
                    // Detener la animación y el servicio si es necesario.
                    //stopService(serviceIntent);
                    AnimationUtil.stoptPeriodicRotationAnimation();
                }
            }
        } else {
            // Android anterior a Oreo, inicia la animación y el servicio directamente.
            if (isChecked) {
                Intent serviceIntent = new Intent(this, AudioPlayerService.class);
                startService(serviceIntent);
                AnimationUtil.startPeriodicRotationAnimation(logoFe, 1, 10);
            } else {
                // Detener la animación y el servicio si es necesario.
                //stopService(serviceIntent);
                AnimationUtil.stoptPeriodicRotationAnimation();
            }
        }
    }

}
/*

// Verifica si tienes permiso de cámara
if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        // Si no tienes permiso, solicítalo
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        }

// Verifica si tienes permiso de escritura y lectura de almacenamiento
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        // Si no tienes permiso, solicítalo
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST);
        }

// Verifica si tienes permiso de notificación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
        // Si no tienes permiso, solicítalo
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.VIBRATE}, NOTIFICATION_PERMISSION_REQUEST);
        }

 */