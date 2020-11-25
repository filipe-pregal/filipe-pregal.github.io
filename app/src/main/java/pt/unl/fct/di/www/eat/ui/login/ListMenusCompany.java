package pt.unl.fct.di.www.eat.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import pt.unl.fct.di.www.eat.R;
import pt.unl.fct.di.www.eat.data.Menu;
import pt.unl.fct.di.www.eat.data.Option;
import pt.unl.fct.di.www.eat.data.RestaurantData;

public class ListMenusCompany extends AppCompatActivity {

    ListView listView;
    Button btnMenu, btnRequest;
    String email;
    DatabaseReference mref;

    ArrayList<String> mTitle = new ArrayList<>();
    ArrayList<String> mDesserts = new ArrayList<>();
    ArrayList<String> mDrinks = new ArrayList<>();
    ArrayList<String> mTags = new ArrayList<>();
    ArrayList<String> mPrice = new ArrayList<>();
    ArrayList<String> mTime = new ArrayList<>();
    ArrayList<Boolean> mAvailableDrinks = new ArrayList<>();
    ArrayList<Boolean> mAvailableDesserts = new ArrayList<>();
    ArrayList<Boolean> mAvailableMenus = new ArrayList<>();

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
        mref = FirebaseDatabase.getInstance().getReference();
        checkLogin();


        btnRequest = findViewById(R.id.seeRequests);

        MyAdapterRequest adapterR = new MyAdapterRequest(getApplicationContext());
        listView.setAdapter(adapterR);

        btnRequest.setOnClickListener(view -> {
            MyAdapterRequest adapterR1 = new MyAdapterRequest(getApplicationContext());
            listView.setAdapter(adapterR1);
        });

        btnMenu = findViewById(R.id.editMenus);

        btnMenu.setOnClickListener(view -> {
            DatabaseReference rest = mref.child("Restaurants").child(email);
            rest.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        resetData();
                        RestaurantData post = dataSnapshot.getValue(RestaurantData.class);
                        setData(post);
                    }
                    MyAdapterMenu adapter = new MyAdapterMenu(getApplicationContext());//, mTitle, mTags, mPrice, mTime, mAvailableDrinks, mAvailableDesserts, mAvailableMenus);
                    listView.setAdapter(adapter);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        });
    }

    private void setData(RestaurantData r){
        for(Map.Entry<String, Option> drinks : r.getDrinks().entrySet()){
            Option drink = drinks.getValue();
            mDrinks.add(drink.getName());
            mAvailableDrinks.add(drink.getIsAvailable());
        }

        for(Map.Entry<String, Option> desserts : r.getDesserts().entrySet()){
            Option dessert = desserts.getValue();
            mDesserts.add(dessert.getName());
            mAvailableDesserts.add(dessert.getIsAvailable());
        }

        for (Map.Entry<String, Menu> menus : r.getMenu().entrySet()){
            Menu menu = menus.getValue();
            mTitle.add(menu.getName());
            mPrice.add(menu.getPrice().toString());
            mTime.add(menu.getTime().toString());
            mTags.add(menu.getTag());
            mAvailableMenus.add(menu.getIsAvailable());
        }
    }

    private void resetData(){
        mTitle.clear();
        mDesserts.clear();
        mDrinks.clear();
        mTags.clear();
        mPrice.clear();
        mTime.clear();
        mAvailableDesserts.clear();
        mAvailableDrinks.clear();
        mAvailableMenus.clear();
    }

    class MyAdapterMenu extends ArrayAdapter<String> {

        MyAdapterMenu(Context c){
            super(c,R.layout.row_menu_user, R.id.titleMenu, mTitle);
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
            ArrayAdapter<String> adpt1 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, mDrinks);
            adpt1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            myDrinks.setAdapter(adpt1);

            Spinner myDesserts = row.findViewById(R.id.desserts);
            ArrayAdapter<String> adpt2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, mDesserts);
            adpt2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            myDesserts.setAdapter(adpt2);

            Button b = row.findViewById(R.id.addCart);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openEditMenu(myDesserts.getSelectedItem().toString());
                    //openEditMenu(myTitle.getText().toString());

                }
            });
            b.setText("EDIT");
            myTitle.setText(mTitle.get(position));
            myDish.setText(mTitle.get(position));
            myTag.setText(mTags.get(position));
            myPrice.setText(mPrice.get(position));
            myTime.setText(mTime.get(position));

            if(mAvailableMenus.get(position) == false)
                row.setBackgroundColor(Color.GRAY);
            return row;
        }
    }

    class MyAdapterRequest extends ArrayAdapter<String> {

        MyAdapterRequest(Context c){
            super(c,R.layout.row_menu_user, R.id.titleMenu, mT);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_request,parent, false);

            TextView myT = row.findViewById(R.id.teste1);
            TextView myB = row.findViewById(R.id.teste2);

            myT.setText(mT[position]);
            myB.setText(mB[position]);
            return row;
        }
    }


    private void openEditMenu(String menu){
        Intent intent = new Intent(this, EditMenu.class);
        intent.putExtra("user", email);
        intent.putExtra("menu", menu);
        startActivity(intent);
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
        Intent intent = new Intent(this, CompanyLoginActivity.class);
        startActivity(intent);
    }
}
