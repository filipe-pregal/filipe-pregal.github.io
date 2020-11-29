package pt.unl.fct.di.www.eat.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pt.unl.fct.di.www.eat.R;
import pt.unl.fct.di.www.eat.data.Request;
import pt.unl.fct.di.www.eat.data.RequestItem;

public class RequestUser extends AppCompatActivity {

    String email;
    DatabaseReference mref;
    ListView listView;
    ArrayList<String> mCode = new ArrayList<>();
    ArrayList<String> mPayment = new ArrayList<>();
    ArrayList<Double> mPriceR = new ArrayList<>();
    ArrayList<String> mTimeR = new ArrayList<>();
    ArrayList<List<RequestItem>> mItem = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_user);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("user");
        }

        mref = FirebaseDatabase.getInstance().getReference();
        checkLogin();

        listView = findViewById(R.id.listViewRequests);

        DatabaseReference requestRef = mref.child("Requests");
        requestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checkLogin();
                resetDataRequest();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        DatabaseReference d = requestRef.child(child.getKey()).child(email);
                        d.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                                while (it.hasNext()){
                                    DataSnapshot data = it.next();
                                    Request request = data.getValue(Request.class);
                                    setDataRequest(request, data.getKey());
                                }
                                MyAdapterRequest adapterR1 = new MyAdapterRequest(getApplicationContext());
                                listView.setAdapter(adapterR1);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    class MyAdapterRequest extends ArrayAdapter<String> {

        MyAdapterRequest(Context c) {
            super(c, R.layout.row_request, R.id.codeR, mCode);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_request, parent, false);

            TextView myCode = row.findViewById(R.id.codeR);
            TextView myPayment = row.findViewById(R.id.paymentR);
            TextView myPrice = row.findViewById(R.id.priceR);
            TextView myTime = row.findViewById(R.id.timeR);


            ListView list = row.findViewById(R.id.itemsR);

            ArrayList<String> item = new ArrayList<>();
            ArrayList<Double> quantity = new ArrayList<>();

            for(RequestItem a : mItem.get(position)){
                item.add(a.getItem());
                quantity.add(a.getQuantity());
            }

            myCode.setText("Code " +mCode.get(position));
            myPayment.setText(mPayment.get(position));
            myPrice.setText(mPriceR.get(position).toString().concat("€"));
            myTime.setText(mTimeR.get(position));

            MyAdapterAux adpt = new MyAdapterAux(getApplicationContext(), item, quantity);
            list.setAdapter(adpt);

            return row;
        }
    }

    class MyAdapterAux extends ArrayAdapter<String> {

        ArrayList<String> rItems;
        ArrayList<Double> rQuantity;

        MyAdapterAux(Context c, ArrayList<String> items, ArrayList<Double> quantity) {
            super(c, R.layout.row_item_request, R.id.codeR, items);
            this.rItems = items;
            this.rQuantity = quantity;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row_item_request, parent, false);

            TextView myItem = row.findViewById(R.id.itemR);
            TextView myQuantity = row.findViewById(R.id.quantityR);

            myItem.setText(rItems.get(position));
            myQuantity.setText("x"+ rQuantity.get(position).toString());

            return row;
        }
    }

    private void setDataRequest(Request r, String code){
        mCode.add(code);
        mPayment.add(r.getPayment());
        mPriceR.add(r.getPrice());
        mTimeR.add(convertTime(r.getTime()));
        mItem.add(r.getItems());
    }

    private void resetDataRequest(){
        mCode.clear();
        mPayment.clear();
        mPriceR.clear();
        mTimeR.clear();
        mItem.clear();
    }

    private void checkLogin() {
        DatabaseReference user = mref.child("Users").child(email);
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String token = dataSnapshot.child("token").getValue().toString();
                    if (token.equals("")) {
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

    private void redirectLogin() {
        getIntent().removeExtra("user");
        Intent intent = new Intent(this, UserLoginActivity.class);
        startActivity(intent);
    }

    private String convertTime(double time) {
        String[] aux = String.valueOf(time).split("\\.");
        int hours = Integer.parseInt(aux[0]);
        int minutes = (int) (Double.parseDouble("0." + aux[1]) * 60);
        if (minutes == 0)
            return hours + "h";
        if (hours == 0)
            return minutes + "m";
        return hours + "h" + minutes + "m";
    }
}
