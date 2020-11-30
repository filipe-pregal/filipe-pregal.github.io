package pt.unl.fct.di.www.eat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import pt.unl.fct.di.www.eat.ui.login.CompanyLoginActivity;
import pt.unl.fct.di.www.eat.ui.login.List_restaurantsActivity;
import pt.unl.fct.di.www.eat.ui.login.UserLoginActivity;

public class StartActivity extends AppCompatActivity {

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        findViewById(R.id.reUser);
        ImageButton userButton = findViewById(R.id.user);
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUser();
            }
        });

        ImageButton companyButton = findViewById(R.id.company);
        companyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCompany();
            }
        });
    }

    public void openUser() {
        Intent intent = new Intent(this, UserLoginActivity.class);
        startActivity(intent);
    }

    public void openCompany() {
        Intent intent = new Intent(this, CompanyLoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        try{
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("myuser", Context.MODE_PRIVATE);
            String email = new String();
            email = sp.getString("user", email);
            Intent it;
            // caso cliente
            if (email.length() > 0) {
                it = new Intent(getApplicationContext(), List_restaurantsActivity.class);
                it.putExtra("user", email);
                startActivity(it);
            } else {
                it = new Intent(getApplicationContext(), UserLoginActivity.class);
                startActivity(it);
            }
        } catch(Exception e ){

        }

        super.onStart();
    }
}
