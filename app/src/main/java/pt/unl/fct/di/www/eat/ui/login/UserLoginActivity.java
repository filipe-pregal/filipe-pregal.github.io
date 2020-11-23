package pt.unl.fct.di.www.eat.ui.login;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pt.unl.fct.di.www.eat.R;

public class UserLoginActivity extends AppCompatActivity {

    EditText email, pwd;
    Button loginBtn, registerBtn;
    DatabaseReference d;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.emailUser);
        pwd = findViewById(R.id.passwordUser);
        loginBtn = findViewById(R.id.loginUser);
        loginBtn.setEnabled(true);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = email.getText().toString();
                String emailToSearch = em.replace(".", "\\");
                DatabaseReference d = FirebaseDatabase.getInstance().getReference().child("Users").child(emailToSearch);
                d.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String emailT = dataSnapshot.child("email").getValue().toString();
                            String pwdT = dataSnapshot.child("password").getValue().toString();
                            String role = dataSnapshot.child("role").getValue().toString();

                            if (emailT.equals(em) && pwdT.equals(pwd.getText().toString()) && role.equals("USER")) {
                                //Toast.makeText(getApplicationContext(), "login", Toast.LENGTH_SHORT).show();
                                openRestaurants(em);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        registerBtn = findViewById(R.id.register);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegister();
            }
        });
    }

    private void openRegister(){
        Intent intent = new Intent(this, RegisterUser.class);
        startActivity(intent);
    }

    private void openRestaurants(String email){
        Intent intent = new Intent(this, List_restaurantsActivity.class);
        intent.putExtra("user", email);
        startActivity(intent);
    }
}
