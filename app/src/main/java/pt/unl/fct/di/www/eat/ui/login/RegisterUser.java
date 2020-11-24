package pt.unl.fct.di.www.eat.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import pt.unl.fct.di.www.eat.data.UserData;

public class RegisterUser extends AppCompatActivity {
    EditText name, email, password, confirmPassword;
    Button registerBtn;
    DatabaseReference d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        name = findViewById(R.id.Name);
        email = findViewById(R.id.Email);
        password = findViewById(R.id.Password);
        confirmPassword = findViewById(R.id.PasswordConfirmation);
        registerBtn = findViewById(R.id.reUser);
        registerBtn.setEnabled(false);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                executeValidateBtn();
            }
        });

        // erros na confirmaçao de email
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
                executeValidateBtn();
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                executeValidationOfConfirmPassword();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                executeValidateBtn();
            }
        });

        // Erros de confirmação de password
       confirmPassword.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               executeValidationOfConfirmPassword();
           }

           @Override
           public void afterTextChanged(Editable editable) {
               executeValidateBtn();
           }
       });


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = email.getText().toString();
                String emailToSearch = em.replace(".", "_");
                d = FirebaseDatabase.getInstance().getReference("Users");
                d.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(emailToSearch)){
                            email.setError("This email is already used.");
                        }
                        else{
                            String n = name.getText().toString();
                            String p = password.getText().toString();
                            String cf = confirmPassword.getText().toString();
                            if(p.equals(cf)){
                                UserData u = new UserData(em, n, p);
                                d.child(emailToSearch).setValue(u);
                                openLogin();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT);
                    }
                });
            }
        });

    }
    private boolean validEmail(String email) {
        if (email.contains(" ") || !email.contains("@")) return false;
        String[] splitted = email.split("@", -1);
        if (splitted.length!=2) return false;
        if (splitted[0].isEmpty() || splitted[1].isEmpty() || !splitted[1].contains(".")) return false;
        String[] mailsplit = splitted[1].split("\\.", -1);
        if (mailsplit.length<2) return false;
        for (String part: mailsplit) {
            if (part.isEmpty()) return false;
        }
        for (String part: splitted[0].split("\\.", -1)) {
            if (part.isEmpty()) return false;
        }
        return true;
    }

    private boolean validBtn() {
        return !str(email).equals("") && !str(name).equals("") && !str(password).equals("") && !str(confirmPassword).equals("") &&
                email.getError()==null && confirmPassword.getError()==null;
    }


    private void executeValidationOfConfirmPassword() {
        String cp = str(confirmPassword);
        String p = str(password);
        if (!cp.equals(p)) {
            confirmPassword.setError("Passwords do not match!");
        } else {
            confirmPassword.setError(null);
        }
    }

    private void executeValidateBtn() {
        if(validBtn())
            registerBtn.setEnabled(true);
        else
            registerBtn.setEnabled(false);
    }

    private static String str(EditText view) {
        return view.getText().toString();
    }

    public void openLogin(){
        Intent intent = new Intent(this, UserLoginActivity.class);
        startActivity(intent);
    }
}