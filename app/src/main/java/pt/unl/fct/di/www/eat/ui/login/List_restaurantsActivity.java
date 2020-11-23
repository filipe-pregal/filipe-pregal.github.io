package pt.unl.fct.di.www.eat.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import pt.unl.fct.di.www.eat.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class List_restaurantsActivity extends AppCompatActivity {

    ListView listView;
    String email;
    String mTitle[] = {"teste1", "teste2", "teste3"};
    String mTag[] = {"peixe", "carne", "vegetariano"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_restaurants);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        //actionBar.setLogo();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("user");
        }

        listView = findViewById(R.id.listView);

        MyAdapter adapter = new MyAdapter(this, mTitle, mTag);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = (String) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                openMenus(s);
            }
        });
    }

    private void openMenus(String restaurant){
        Intent intent = new Intent(this, ListMenusUser.class);
        intent.putExtra("user", email);
        intent.putExtra("restaurant", restaurant);
        startActivity(intent);
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String rTitle[];
        String rTag[];

        MyAdapter(Context c, String title[], String tag[]){
            super(c,R.layout.row_restaurant, R.id.titleRestaurant, title);
            this.context = c;
            this.rTitle = title;
            this.rTag = tag;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_restaurant,parent, false);
            TextView myTitle = row.findViewById(R.id.titleRestaurant);
            TextView myTag = row.findViewById(R.id.tag);
            TextView myPrice = row.findViewById(R.id.time);

            myTitle.setText(rTitle[position]);
            myTag.setText(rTag[position]);

            return row;
        }
    }
}