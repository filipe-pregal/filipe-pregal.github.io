package pt.unl.fct.di.www.eat.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import pt.unl.fct.di.www.eat.R;

public class EditMenu extends AppCompatActivity {

    String email;
    String menu;
    TextView t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu);

        t = findViewById(R.id.teste);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("user");
            menu = extras.getString("menu");
        }
        t.setText(menu);
    }
}
