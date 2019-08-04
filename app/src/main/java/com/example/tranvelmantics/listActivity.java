package com.example.tranvelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class listActivity extends AppCompatActivity {

    ArrayList<TravelDeal> deals;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        /*
        utilsclass.openFBpref("travelmantics",this);

        RecyclerView rw = findViewById(R.id.rdViews);
        DealAdapter dealAdapter= new DealAdapter(this);
        rw.setAdapter(dealAdapter);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        rw.setLayoutManager(linearLayout);
        utilsclass.attachListenner();
        */
    }

    @Override
    protected void onPause() {
        super.onPause();
        utilsclass.detachListenner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        utilsclass.openFBpref("traveldeals",this);

        RecyclerView rw = findViewById(R.id.rdViews);
        DealAdapter dealAdapter= new DealAdapter(this);
        rw.setAdapter(dealAdapter);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        rw.setLayoutManager(linearLayout);
        utilsclass.attachListenner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.listactivity_menu, menu);

        MenuItem insertMenu = menu.findItem(R.id.insert);
        if(utilsclass.isAdmin==true)
            insertMenu.setVisible(true);
        else
            insertMenu.setVisible(false);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.insert:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.logout:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                                utilsclass.attachListenner();
                            }
                        });

            default:
                 return super.onOptionsItemSelected(item);
        }
    }
    public void showMenu(){
        invalidateOptionsMenu();
    }

}
