package github.com.abonifacio.microfonoremoto;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.Arrays;

import github.com.abonifacio.microfonoremoto.dispositivos.DispositivoService;
import github.com.abonifacio.microfonoremoto.utils.Conf;
import github.com.abonifacio.microfonoremoto.utils.Toaster;

/**
 * Created by Augusto on 15/10/2017.
 */

public abstract class BaseActivty extends AppCompatActivity {



    private static final int RECORD_ACTIVITY_REQUEST_CODE = 101;
    private static final int RECORD_PERMISSION_REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getMainLayout());
        MicApplication.setActivity(this);
        MicApplication.setMainView(findViewById(this.getContainerId()));

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

    }

    protected abstract int getMainLayout();

    protected abstract int getContainerId();

    @Override
    protected void onDestroy() {
        MicApplication.savePreferences();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.action,menu);
        return true;
    }

    private void openConfigurationDialog(){
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));

        final EditText et = new EditText(this);
        et.setText(Conf.SERVER_HOST);
        et.setHint("http://...");
        linearLayout.addView(et);

        final Spinner sampleRateSpinner = this.getSpinner(
                MicApplication.getThisDispositivo().getSampleRate(),
                "Frecuencia de muestreo",8000, 11025, 16000, 22050,44100);
        linearLayout.addView(sampleRateSpinner);
        final Spinner sampleSizeSpinner = this.getSpinner(
                MicApplication.getThisDispositivo().getSampleSize(),
                "Bits por muestra",8,16);
        linearLayout.addView(sampleSizeSpinner);

        final Spinner stereoSpinner = this.getSpinner(
                MicApplication.getThisDispositivo().isStereo() ? "Estereo" : "Mono",
                "Canales","Mono","Estereo");
        linearLayout.addView(stereoSpinner);

        AlertDialog popup = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle("Configuracion")
                .setMessage("Dirección del servidor")
                .setView(linearLayout)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ip = et.getText().toString();
                        Conf.SERVER_HOST = ip;
                        MicApplication.setThisDispositivoValues(
                                (int) sampleRateSpinner.getSelectedItem(),
                                (int) sampleSizeSpinner.getSelectedItem(),
                                "Estereo".equals(stereoSpinner.getSelectedItem()));
                    }
                })
                .setNegativeButton("Cancelar",null)
                .show();
    }

    private <T> Spinner getSpinner(T defaultValue, String nombre, T... params){
        Spinner tmp = new Spinner(this);
        tmp.setPrompt(nombre);
        int defaultPosition = defaultValue!=null ? Arrays.asList(params).indexOf(defaultValue) : -1;
        if(defaultPosition==-1) defaultPosition = 0;
        ArrayAdapter<T> tmpAdapter = new ArrayAdapter<T>(this, android.R.layout.simple_spinner_dropdown_item, params);
        tmp.setAdapter(tmpAdapter);
        tmp.setSelection(defaultPosition);
        return tmp;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                this.openConfigurationDialog();
                return true;
            case R.id.action_record:
                if(this.checkRecordingPermission()){
                    super.startActivityForResult(new Intent(this,MicrofonoActivity.class),RECORD_ACTIVITY_REQUEST_CODE);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkRecordingPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},RECORD_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==RECORD_ACTIVITY_REQUEST_CODE){
            if(resultCode==MicrofonoActivity.BAD_SAMPLE_RATE){
                Toaster.show("La frecuencia de muestro no es soportada por el microfono");
            }else if(resultCode==MicrofonoActivity.ERROR_CREATE_UDP){
                Toaster.show("Error de conexion UDP");
            }else if(resultCode==MicrofonoActivity.ERROR_REGISTER){
                Toaster.show("Error registrando el dispositivo en el servidor");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==RECORD_PERMISSION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                super.startActivityForResult(new Intent(this,MicrofonoActivity.class),RECORD_ACTIVITY_REQUEST_CODE);
            }else{
                Toaster.show("Se necesitan los permisos para grabar");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
