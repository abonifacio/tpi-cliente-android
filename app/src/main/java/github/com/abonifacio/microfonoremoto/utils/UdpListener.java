package github.com.abonifacio.microfonoremoto.utils;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import github.com.abonifacio.microfonoremoto.MicApplication;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UdpListener extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_START_LISTENING = "github.com.abonifacio.microfonoremoto.action.START_LISTENING";
    private static final String EXTRA_PORT = "github.com.abonifacio.microfonoremoto.extra.PORT";

    private static DatagramSocket datagramSocket;
    private static boolean running = false;

    public UdpListener() {
        super("UdpListener");

    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startListening(Integer puerto) {
        Intent intent = new Intent(MicApplication.getAppContext(), UdpListener.class);
        intent.setAction(ACTION_START_LISTENING);
        intent.putExtra(EXTRA_PORT,puerto);
        MicApplication.getAppContext().startService(intent);
    }

    public static boolean isListening(){
        return running;
    }

    public static void stopListening(){
        UdpListener.running = false;
        if(datagramSocket!=null && !datagramSocket.isClosed()){
            datagramSocket.close();
        }
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_LISTENING.equals(action)) {
                 Integer port = intent.getIntExtra(EXTRA_PORT,0);
                try {
                    handleActionStartListening(port);
                } catch (IOException e) {
                    e.printStackTrace();
                    stopListening();
                }
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionStartListening(Integer port) throws IOException {

        datagramSocket = new DatagramSocket(port);
        datagramSocket.setSoTimeout(10000);
        datagramSocket.setReuseAddress(true);
        running = true;
        byte[] buffer = new byte[64512];
        DatagramPacket datagramPacket = new DatagramPacket(buffer,buffer.length);

        AudioTrack audio = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(44100)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                        .build())
                .setBufferSizeInBytes(64512)
                .build();
        audio.setVolume(1.0f);

        while(running){
            datagramSocket.receive(datagramPacket);
            audio.write(buffer,0,buffer.length);
            audio.play();
            Log.d("Paquete recibido",new String(buffer,0,16));
        }
    }

}
