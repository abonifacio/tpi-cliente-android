package github.com.abonifacio.microfonoremoto.dispositivos;

/**
 * Created by Augusto on 6/10/2017.
 */

public class Dispositivo {

    private String nombre;
    private String ip;
    private Integer puerto;
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
}
