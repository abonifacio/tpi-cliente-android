package github.com.abonifacio.microfonoremoto.utils;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioFormat;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import github.com.abonifacio.microfonoremoto.MicApplication;

/**
 * Created by Augusto on 21/7/2018.
 */

public class UdpSender {
//        extends IntentService {

//    private static final String ACTION_START_RECORDING = "github.com.abonifacio.microfonoremoto.action.START_RECORDING";
//    private static final String EXTRA_PORT = "github.com.abonifacio.microfonoremoto.extra.PORT";

    private DatagramSocket datagramSocket;

    private InetSocketAddress serverAddress;
//    private int port;

//    public UdpSender(){
//        super("UdpListener");
//    }

    private UdpSender(DatagramSocket datagramSocket,int port){
        this.datagramSocket = datagramSocket;
        this.serverAddress = new InetSocketAddress(Conf.getServerIp(),port);
//        try {
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//            this.datagramSocket = null;
//        }
//        this.port = port;
    }

    public static UdpSender start(int port){
        try{
            DatagramSocket so = new DatagramSocket();
            so.setSoTimeout(10000);
            return new UdpSender(so,port);
        }catch (Exception e){
            Toaster.show(e.getMessage());
        }
        return new UdpSender(null,port);
    }

    public boolean send(byte[] data,int size){
        if(datagramSocket==null) return false;
        try {
            datagramSocket.send(new DatagramPacket(data,size,serverAddress));
        } catch (IOException e) {
            Toaster.show(e.getMessage());
            datagramSocket = null;
            return false;
        }
        return true;
    }

    public void stop(){
        if(datagramSocket!=null && !datagramSocket.isClosed()){
            datagramSocket.close();
        }
    }

//    @Override
//    protected void onHandleIntent(@Nullable Intent intent) {
//        if (intent != null) {
//            final String action = intent.getAction();
//            if (ACTION_START_RECORDING.equals(action)) {
//                int port = intent.getIntExtra(EXTRA_PORT,0);
//                try {
//                    handleActionStartListening(port,sampleRate,sampleSize,channels);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    stopListening();
//                }
//            }
//        }
//    }
}
