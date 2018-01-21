package github.com.abonifacio.microfonoremoto.dispositivos;

/**
 * Created by Augusto on 6/10/2017.
 */

public class Dispositivo {

    private String nombre;
    private String ip;
    private Integer puerto;
    private Integer sampleRate;
    private Integer sampleSize;
    private String mac;
    private Boolean stereo;
    private int status;

    public final static int ERROR = 1;
    public final static int LOADING = 2;
    public final static int PLAYING = 3;
    public final static int READY = 4;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPuerto() {
        return puerto;
    }

    public void setPuerto(Integer puerto) {
        this.puerto = puerto;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

    public Integer getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(Integer sampleSize) {
        this.sampleSize = sampleSize;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Boolean getStereo() {
        return stereo;
    }

    public void setStereo(Boolean stereo) {
        this.stereo = stereo;
    }

    public boolean isStereo(){
        return this.stereo!=null && this.stereo;
    }
}
