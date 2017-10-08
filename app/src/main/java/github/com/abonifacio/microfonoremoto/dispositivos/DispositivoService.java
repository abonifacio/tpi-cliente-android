package github.com.abonifacio.microfonoremoto.dispositivos;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

/**
 * Created by Augusto on 6/10/2017.
 */

public interface DispositivoService {

    @GET("dispositivos")
    Call<List<Dispositivo>> list();

//    @POST("dispositivos")
//    Call<Integer> add(@Body Dispositivo dispositivo);

    @PUT("dispositivos")
    Call<ResponseBody> listen(@Body Dispositivo dispositivo);

//    @DELETE("dispositivos/{port}")
//    Call<ResponseBody> delete(@Path("port") Integer puerto);
}
