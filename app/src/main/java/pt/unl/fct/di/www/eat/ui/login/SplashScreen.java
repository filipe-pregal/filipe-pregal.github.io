package pt.unl.fct.di.www.eat.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

import pt.unl.fct.di.www.eat.R;
import pt.unl.fct.di.www.eat.StartActivity;

public class SplashScreen<pref> extends AppCompatActivity {
    SharedPreferences sp;
    String email = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        email = new String();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, StartActivity.class));
            }
        }, 3000);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onRestart() {
        sp = getApplicationContext().getSharedPreferences("myuser", Context.MODE_PRIVATE);
        String e = email.toString();
        String email = sp.getString("email", e);
        if(email.length()>0){
            Intent it = new Intent(getApplicationContext(), List_restaurantsActivity.class);
            it.putExtra("email",e);
        }
            super.onRestart();
    }
}