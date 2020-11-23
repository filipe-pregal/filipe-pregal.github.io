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
    String email;
    String restaurant;
    String mTitle[] = {"teste1", "teste2", "teste3"};
    String mP[] = {"feijoada", "sardinhas", "bolonhesa"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_menus_user);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("user");
        }

        listView = findViewById(R.id.listViewMenuUser);
        MyAdapter adapter = new MyAdapter(this, mTitle, mP);
        listView.setAdapter(adapter);

    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String rTitle[];
        String rP[];

        MyAdapter(Context c, String title[], String p[]){
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
            ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getApplicationContext(),
                    R.array.bebidas, android.R.layout.simple_spinner_item);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerB.setAdapter(adapter1);

            Spinner spinnerS = row.findViewById(R.id.desc4);
            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getApplicationContext(),
                    R.array.sobremesas, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerS.setAdapter(adapter2);

            Button b = row.findViewById(R.id.addCart);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openEditMenu(myTitle.getText().toString());
                }
            });
            b.setText("EDIT");

            myTitle.setText(rTitle[position]);
            myP.setText(rP[position]);
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
