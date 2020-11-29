package pt.unl.fct.di.www.eat.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String em = charSequence.toString();
                if (!validEmail(em)) {
                    email.setError("Emails must be valid!\nThey may not be empty, contain spaces or double dots (..). It must also contain a domain name with an \"@\".");
                } else {
                    email.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if(email.getError() == null)
                    loginBtn.setClickable(true);
            }
        });

        loginBtn.setOnClickListener(view -> {
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
        });
    }

    private boolean validEmail(String email) {
        if (email.contains(" ") || !email.contains("@")) return false;
        String[] splitted = email.split("@", -1);
        if (splitted.length != 2) return false;
        if (splitted[0].isEmpty() || splitted[1].isEmpty() || !splitted[1].contains("."))
            return false;
        String[] mailsplit = splitted[1].split("\\.", -1);
        if (mailsplit.length < 2) return false;
        for (String part : mailsplit) {
            if (part.isEmpty()) return false;
        }
        for (String part : splitted[0].split("\\.", -1)) {
            if (part.isEmpty()) return false;
        }
        return true;
    }

    private void openMenus(String email) {
        Intent intent = new Intent(this, ListMenusCompany.class);
        intent.putExtra("user", email);
        startActivity(intent);
    }
}
