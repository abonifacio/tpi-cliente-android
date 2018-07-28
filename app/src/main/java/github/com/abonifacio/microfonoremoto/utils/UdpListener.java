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
import java.net.InetSocketAddress;

import github.com.abonifacio.microfonoremoto.MicApplication;
import github.com.abonifacio.microfonoremoto.dispositivos.Dispositivo;

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
    private static final String EXTRA_SAMPLE_RATE = "github.com.abonifacio.microfonoremoto.extra.SAMPLE_RATE";
    private static final String EXTRA_SAMPLE_SIZE = "github.com.abonifacio.microfonoremoto.extra.SAMPLE_SIZE";
    private static final String EXTRA_CHANNELS = "github.com.abonifacio.microfonoremoto.extra.CHANNELS";

    private static DatagramSocket datagramSocket;
    private static boolean running = false;
    private static String current = null;
    private static AudioTrack audioTrack;

    public UdpListener() {
        super("UdpListener");

    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startListening(Dispositivo item){
//            Integer puerto,Integer sampleRate, Integer sampleSize, Boolean stereo) {
        Intent intent = new Intent(MicApplication.getAppContext(), UdpListener.class);
        intent.setAction(ACTION_START_LISTENING);
        intent.putExtra(EXTRA_PORT,item.getPuerto());
        intent.putExtra(EXTRA_SAMPLE_RATE,item.getSampleRate());
        if(item.isStereo()){
            intent.putExtra(EXTRA_CHANNELS,AudioFormat.CHANNEL_OUT_STEREO);
        }else{
            intent.putExtra(EXTRA_CHANNELS,AudioFormat.CHANNEL_OUT_MONO);
        }
        if(item.getSampleSize()==8){
            intent.putExtra(EXTRA_SAMPLE_SIZE,AudioFormat.ENCODING_PCM_8BIT);
        }else{
            intent.putExtra(EXTRA_SAMPLE_SIZE,AudioFormat.ENCODING_PCM_16BIT);
        }
        current = item.getMac();
        MicApplication.getAppContext().startService(intent);
    }

    public static boolean isListening(){
        return running;
    }

    public static void stopListening(){
        UdpListener.running = false;
        current = null;
        try{
            if(audioTrack!=null){
                audioTrack.pause();
            }
        }catch (IllegalStateException e){}
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_LISTENING.equals(action)) {
                int port = intent.getIntExtra(EXTRA_PORT,0);
                int sampleRate = intent.getIntExtra(EXTRA_SAMPLE_RATE,8000);
                int sampleSize = intent.getIntExtra(EXTRA_SAMPLE_SIZE,AudioFormat.ENCODING_PCM_8BIT);
                int channels = intent.getIntExtra(EXTRA_CHANNELS,AudioFormat.CHANNEL_OUT_MONO);
                try {
                    handleActionStartListening(port,sampleRate,sampleSize,channels);
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
    private void handleActionStartListening(Integer port,int sampleRate,int sampleSize,int channel) throws IOException {

        running = true;
        try{
            datagramSocket = new DatagramSocket(null);
            datagramSocket.setSoTimeout(10000);
            datagramSocket.setReuseAddress(true);
//            datagramSocket.setBroadcast(true);
            datagramSocket.bind(new InetSocketAddress(MicApplication.getDeviceIp(),port));
        }catch (Exception e){
            running = false;
            return;
        }
        byte[] buffer = new byte[Conf.BUFFER_SIZE];
        DatagramPacket datagramPacket = new DatagramPacket(buffer,buffer.length);

        audioTrack = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build())
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(sampleSize)
                        .setSampleRate(sampleRate)
                        .setChannelMask(channel)
                        .build())
                .setBufferSizeInBytes(Conf.BUFFER_SIZE)
                .build();
        audioTrack.setVolume(1.0f);

        while(running){
            datagramSocket.receive(datagramPacket);
            audioTrack.write(buffer,0,datagramPacket.getLength());
            if(running){
                audioTrack.play();
            }
        }
        if(audioTrack!=null){
            audioTrack.flush();
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }
        if(datagramSocket!=null && !datagramSocket.isClosed()){
            datagramSocket.close();
        }

    }

    public static String getCurrent(){
        return current;
    }

}
