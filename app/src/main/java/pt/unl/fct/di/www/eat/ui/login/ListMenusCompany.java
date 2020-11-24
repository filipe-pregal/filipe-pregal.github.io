package pt.unl.fct.di.www.eat.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import pt.unl.fct.di.www.eat.R;

public class ListMenusCompany extends AppCompatActivity {

    ListView listView;
    Button btnMenu, btnRequest;
    String email;
    String mTitle[] = {"teste1", "teste2", "teste3"};
    String mP[] = {"feijoada", "sardinhas", "bolonhesa"};

    String arrayBebidas[] = {"cola", "fanta", "compal"};
    String arrayDoces[] = {"mousse", "doce da casa", "gelado"};

    String mT[] = {"a", "b", "c"};
    String mB[] = {"d", "e", "f"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_options);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("user");
        }
        listView = findViewById(R.id.listView);

        btnMenu = findViewById(R.id.editMenus);

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    MyAdapterMenu adapterM = new MyAdapterMenu(getApplicationContext(), mTitle, mP);
                    listView.setAdapter(adapterM);
            }
        });

        btnRequest = findViewById(R.id.seeRequests);

        MyAdapterRequest adapterR = new MyAdapterRequest(getApplicationContext(), mT, mB);
        listView.setAdapter(adapterR);

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    MyAdapterRequest adapterR = new MyAdapterRequest(getApplicationContext(), mT, mB);
                    listView.setAdapter(adapterR);
            }
        });
    }

    class MyAdapterMenu extends ArrayAdapter<String> {
        Context context;
        String rTitle[];
        String rP[];

        MyAdapterMenu(Context c, String title[], String p[]){
            super(c,R.layout.row_menu_user, R.id.titleMenu, title);
            this.context = c;
            this.rTitle = title;
            this.rP = p;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_menu_user,parent, false);
            TextView myTitle = row.findViewById(R.id.titleMenu);
            TextView myP = row.findViewById(R.id.desc2);

            Spinner spinnerB = row.findViewById(R.id.desc3);
            ArrayAdapter<String> adpt1 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayBebidas);
            adpt1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerB.setAdapter(adpt1);

            Spinner spinnerS = row.findViewById(R.id.desc4);
            ArrayAdapter<String> adpt2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayDoces);
            adpt2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerS.setAdapter(adpt2);

            Button b = row.findViewById(R.id.addCart);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openEditMenu(spinnerB.getSelectedItem().toString());
                    //openEditMenu(myTitle.getText().toString());

                }
            });
            b.setText("EDIT");
            myTitle.setText(rTitle[position]);
            myP.setText(rP[position]);
            return row;
        }
    }

    class MyAdapterRequest extends ArrayAdapter<String> {
        Context context;
        String rT[];
        String rB[];

        MyAdapterRequest(Context c, String t[], String b[]){
            super(c,R.layout.row_menu_user, R.id.titleMenu, t);
            this.context = c;
            this.rT = t;
            this.rB = b;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_request,parent, false);

            TextView myT = row.findViewById(R.id.teste1);
            TextView myB = row.findViewById(R.id.teste2);

            myT.setText(rT[position]);
            myB.setText(rB[position]);
            return row;
        }
    }


    private void openEditMenu(String menu){
        Intent intent = new Intent(this, EditMenu.class);
        intent.putExtra("user", email);
        intent.putExtra("menu", menu);
        startActivity(intent);
    }
}
