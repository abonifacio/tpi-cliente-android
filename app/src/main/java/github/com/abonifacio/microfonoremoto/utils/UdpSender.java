package github.com.abonifacio.microfonoremoto.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * Created by Augusto on 21/7/2018.
 */

public class UdpSender {

    private DatagramSocket datagramSocket;

    private InetSocketAddress serverAddress;

    private UdpSender(DatagramSocket datagramSocket,int port){
        this.datagramSocket = datagramSocket;
        this.serverAddress = new InetSocketAddress(Conf.getServerIp(),port);
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

}
