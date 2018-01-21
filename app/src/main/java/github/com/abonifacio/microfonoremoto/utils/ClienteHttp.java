package github.com.abonifacio.microfonoremoto.utils;

import android.os.AsyncTask;
import android.util.Log;

import github.com.abonifacio.microfonoremoto.dispositivos.DispositivoService;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Augusto on 6/10/2017.
 */

public class ClienteHttp {

    private static String ERROR_HEADER = "Error-Message";

    public static Retrofit build(){
        Log.d("CONG_SERVER",Conf.SERVER_HOST);
        return new Retrofit.Builder()
                .baseUrl(Conf.SERVER_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static DispositivoService getDispositivoService(){
        return build().create(DispositivoService.class);
    }

    public static <T> void doAsync(Call<T> call, final ClienteHttp.Callback callback){
        call.enqueue(new retrofit2.Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if(response.isSuccessful()){
                    callback.onSuccess(response.body());
                }else{
                    Toaster.show(response.headers().get(ERROR_HEADER));
                    callback.onError();
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                Toaster.show("Error al acceder al servidor");
                callback.onError();
            }
        });
    }

    public static class Request<T> extends AsyncTask<Call<T>,Void,Void>{

        private final Callback<T> callback;

        public Request(Callback<T> callback){
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Call<T>... params) {
            ClienteHttp.doAsync(params[0],callback);
            return null;
        }


    }

    public interface Callback<T> {
        void onSuccess(T arg);
        void onError();
    }
}
