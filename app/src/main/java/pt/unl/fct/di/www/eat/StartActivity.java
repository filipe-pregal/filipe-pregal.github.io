package pt.unl.fct.di.www.eat;

import androidx.appcompat.app.AppCompatActivity;

import pt.unl.fct.di.www.eat.ui.login.CompanyLoginActivity;
import pt.unl.fct.di.www.eat.ui.login.UserLoginActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class StartActivity extends AppCompatActivity {

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

    public void openUser(){
        Intent intent = new Intent(this, UserLoginActivity.class);
        startActivity(intent);
    }

    public void openCompany(){
        Intent intent = new Intent(this, CompanyLoginActivity.class);
        startActivity(intent);
    }

}
