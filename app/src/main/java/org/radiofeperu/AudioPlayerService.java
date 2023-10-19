package org.radiofeperu;

import static androidx.core.app.NotificationManagerCompat.*;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

public class AudioPlayerService extends Service {
    private ExoPlayer exoPlayer;
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();

        exoPlayer = new ExoPlayer.Builder(this).build();

        // Configura el reproductor y carga el audio
       long currentTimeMillis = System.currentTimeMillis();
        MediaItem mediaItem = MediaItem.fromUri("https://us1freenew.listen2myradio.com/live.mp3?typeportmount=s1_14690_stream_" + currentTimeMillis);
        //MediaItem mediaItem = MediaItem.fromUri("http://162.210.192.98:14690/");
        //MediaItem mediaItem = MediaItem.fromUri("http://us1freenew.listen2myradio.com:14690");
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showNotification();
        exoPlayer.setPlayWhenReady(true); // Inicia la reproducción

        return START_STICKY; // El servicio se reiniciará si es detenido por el sistema
    }

    @Override
    public void onDestroy() {
        exoPlayer.stop();
        exoPlayer.clearMediaItems();
        exoPlayer.release();
        stopNotification();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {




            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            if (!notificationManager.areNotificationsEnabled()) {
                Toast.makeText(this, "Para usar esta función, habilita las notificaciones en la configuración de la aplicación.", Toast.LENGTH_LONG).show();

                requestNotificationPermissionAlert();

                return;
            }

            notificationManager.createNotificationChannel(channel);


        }

        // Configura la acción que se abrirá al hacer clic en la notificación
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // En el servicio cuando se presione el botón de detener
        Intent stopIntent = new Intent("org.radiofeperu.ACCION_DETENER_REPRODUCCION");
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        // Construye la notificación
        Notification notification = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle("En vivo \uD83D\uDD0A")
                .setContentText("Radio Fe - 1220 AM")
                .setSmallIcon(R.drawable.play)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_delete, "Detener", stopPendingIntent)
                .setOngoing(true)  // Hace que la notificación sea permanente
                .build();

        // Muestra la notificación en primer plano
        startForeground(NOTIFICATION_ID, notification);
    }


    private void requestNotificationPermissionAlert() {
        Intent broadcastIntent = new Intent("org.radiofeperu.ACCION_MOSTRAR_ALERTA_NOTIFICACION");
        sendBroadcast(broadcastIntent);
    }



    private void stopNotification() {
        stopForeground(true);
        from(this).cancel(NOTIFICATION_ID);
    }
}