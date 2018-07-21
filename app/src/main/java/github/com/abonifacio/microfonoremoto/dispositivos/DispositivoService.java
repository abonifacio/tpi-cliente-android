package github.com.abonifacio.microfonoremoto.dispositivos;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Augusto on 6/10/2017.
 */

public interface DispositivoService {

    @GET("dispositivos")
    Call<List<Dispositivo>> list();

    @POST("dispositivos")
    Call<String> create(@Body Dispositivo dispositivo);

    @DELETE("dispositivos/{mac}")
    Call<Void> delete(@Path("mac") String mac);

    @PUT("dispositivos")
    Call<Void> listen(@Body Dispositivo dispositivo);

}
