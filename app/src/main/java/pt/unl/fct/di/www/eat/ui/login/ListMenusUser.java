package pt.unl.fct.di.www.eat.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import pt.unl.fct.di.www.eat.R;
import pt.unl.fct.di.www.eat.data.Cart;
import pt.unl.fct.di.www.eat.data.Menu;
import pt.unl.fct.di.www.eat.data.Option;
import pt.unl.fct.di.www.eat.data.RestaurantData;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ListMenusUser extends AppCompatActivity {

    ListView listView;
    Button btn;
    String email, restaurant;
    DatabaseReference mref;

    ArrayList<String> mTitle = new ArrayList<>();
    ArrayList<String> mDesserts = new ArrayList<>();
    ArrayList<String> mDrinks = new ArrayList<>();
    ArrayList<String> mTags = new ArrayList<>();
    ArrayList<Double> mPrice = new ArrayList<>();
    ArrayList<Double> mTime = new ArrayList<>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_search:

                super.onOptionsItemSelected(item);
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.my_toolbar, menu);

        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                System.out.println(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println(newText);
                return false;
            }
        });
        //SearchView searchView = (SearchView) searchItem.getActionView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_menus_user);

        //Adicionado para a toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.list_menus_user);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
       
        mref = FirebaseDatabase.getInstance().getReference();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("user");
            restaurant = extras.getString("restaurant");
        }

        listView = findViewById(R.id.listViewMenuUser);
        checkLogin();

        DatabaseReference rest = mref.child("Restaurants").child(restaurant);
        rest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                resetData();
                if(dataSnapshot.exists()) {
                    RestaurantData post = dataSnapshot.getValue(RestaurantData.class);
                    setData(post);
                }
                MyAdapter adapter = new MyAdapter(getApplicationContext());
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void setData(RestaurantData r){
        for(Map.Entry<String, Option> drinks : r.getDrinks().entrySet()){
            Option drink = drinks.getValue();
            if(drink.getIsAvailable())
                mDrinks.add(drink.getName());
        }

        for(Map.Entry<String, Option> desserts : r.getDesserts().entrySet()){
            Option dessert = desserts.getValue();
            if(dessert.getIsAvailable())
                mDesserts.add(dessert.getName());
        }

        for (Map.Entry<String, Menu> menus : r.getMenu().entrySet()){
            Menu menu = menus.getValue();
            if(menu.getIsAvailable()){
                mTitle.add(menu.getName());
                mPrice.add(menu.getPrice());
                mTime.add(menu.getTime());
                mTags.add(menu.getTag());
            }
        }
    }

    private void resetData(){
        mTitle.clear();
        mDesserts.clear();
        mDrinks.clear();
        mTags.clear();
        mPrice.clear();
        mTime.clear();
    }

    class MyAdapter extends ArrayAdapter<String> {

        MyAdapter(Context c){
            super(c,R.layout.row_menu_user, R.id.titleMenu, mTitle);
            //super(c,R.layout.row_menu_user, R.id.dish, title);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_menu_user,parent, false);
            TextView myTitle = row.findViewById(R.id.titleMenu);
            TextView myDish = row.findViewById(R.id.dish);
            TextView myTag = row.findViewById(R.id.tagM);
            TextView myPrice = row.findViewById(R.id.price);
            TextView myTime = row.findViewById(R.id.timeM);

            Spinner myDrinks = row.findViewById(R.id.drinks);
            if(!mDrinks.isEmpty()) {
                ArrayAdapter<String> adpt1 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, mDrinks);
                adpt1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                myDrinks.setAdapter(adpt1);
            }else{
                myDrinks.setVisibility(Spinner.INVISIBLE);
            }

            Spinner myDesserts = row.findViewById(R.id.desserts);
            if(!mDesserts.isEmpty()) {
                ArrayAdapter<String> adpt2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, mDesserts);
                adpt2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                myDesserts.setAdapter(adpt2);
            }else{
                myDesserts.setVisibility(Spinner.INVISIBLE);
            }

            btn = row.findViewById(R.id.addCart);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Cart cart = new Cart(myTitle.getText().toString(), myDrinks.getSelectedItem().toString(), myDesserts.getSelectedItem().toString(), Double.parseDouble(myPrice.getText().toString()), Double.parseDouble(myTime.getText().toString()));
                    System.out.println(cart.toString());
                }
            });

            myTitle.setText(mTitle.get(position));
            myDish.setText(mTitle.get(position));
            myTag.setText(mTags.get(position));
            myPrice.setText(mPrice.get(position).toString());
            myTime.setText(mTime.get(position).toString());
            return row;
        }
    }

    private void checkLogin(){
        DatabaseReference user = mref.child("Users").child(email);
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String token = dataSnapshot.child("token").getValue().toString();
                    if(token.equals("")){
                        redirectLogin();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void redirectLogin(){
        getIntent().removeExtra("user");
        Intent intent = new Intent(this, UserLoginActivity.class);
        startActivity(intent);
    }
}
