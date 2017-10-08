package github.com.abonifacio.microfonoremoto;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import github.com.abonifacio.microfonoremoto.dispositivos.DispositivoListFragment;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MicApplication.setActivity(this);
        MicApplication.setMainView(findViewById(R.id.container));

        this.loadFragement();
    }

    private void loadFragement(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.content, DispositivoListFragment.newInstance());
        transaction.commit();
    }


}
