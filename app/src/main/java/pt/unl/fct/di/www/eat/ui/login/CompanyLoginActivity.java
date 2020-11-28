package pt.unl.fct.di.www.eat.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import pt.unl.fct.di.www.eat.R;

public class CompanyLoginActivity extends AppCompatActivity {
    EditText email, pwd;
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_login);

        email = findViewById(R.id.usernameCompany);
        pwd = findViewById(R.id.passwordCompany);
        loginBtn = findViewById(R.id.loginCompany);
        loginBtn.setEnabled(true);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = email.getText().toString();
                String emailToSearch = em.replace(".", "_");
                DatabaseReference d = FirebaseDatabase.getInstance().getReference().child("Users").child(emailToSearch);
                d.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String emailT = dataSnapshot.child("email").getValue().toString();
                            String pwdT = dataSnapshot.child("password").getValue().toString();
                            String role = dataSnapshot.child("role").getValue().toString();
                            String token = dataSnapshot.child("token").getValue().toString();

                            if (emailT.equals(em) && pwdT.equals(pwd.getText().toString())) {
                                if (role.equals("COMPANY")) {
                                    if (token.equals("")) {
                                        String random = UUID.randomUUID().toString().substring(0, 8);
                                        d.child("token").setValue(random);
                                    }
                                    openMenus(emailToSearch);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Not logging with an company account.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Email or password are incorrect.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Email or password are incorrect.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void openMenus(String email) {
        Intent intent = new Intent(this, ListMenusCompany.class);
        intent.putExtra("user", email);
        startActivity(intent);
    }
}
