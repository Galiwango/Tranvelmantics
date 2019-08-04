package com.example.tranvelmantics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.viewHolder> {
    ArrayList<TravelDeal> deals;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;


    public DealAdapter(Activity context) {
        //utilsclass.openFBpref("traveldeals",context);

        databaseReference = utilsclass.databaseReference;
        firebaseDatabase = utilsclass.firebaseDatabase;
        deals = utilsclass.travelDeals;
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                TravelDeal td = dataSnapshot.getValue(TravelDeal.class);
                td.setId(dataSnapshot.getKey());
                deals.add(td);
                notifyItemInserted(deals.size() - 1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);

    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemview = LayoutInflater.from(context)
                .inflate(R.layout.rvraws, parent, false);


        return new viewHolder(itemview);

    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        TravelDeal dl = deals.get(position);
        holder.bind(dl);

    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvtitle;
        TextView tvdescription;
        TextView tvprice;
        private ImageView imageView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            tvtitle = itemView.findViewById(R.id.tvtitle);
            tvdescription = itemView.findViewById(R.id.tvdescription);
            tvprice = itemView.findViewById(R.id.tvprice);
            imageView = itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);


        }

        public void bind(TravelDeal deal) {
            tvtitle.setText(deal.getTitle());
            tvdescription.setText(deal.getDescription());
            tvprice.setText(deal.getPrice());
            showImage(deal.getImageUrl());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            TravelDeal selecteditem = deals.get(position);
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            intent.putExtra("Deal", selecteditem);
            v.getContext().startActivity(intent);

        }
        public void showImage(String url) {
            if (url != null && url.isEmpty() == false) {

                Picasso.get()
                        .load(url)
                        .resize(80, 80)
                        .centerCrop()
                        .into(imageView);
            }
        }
    }

    }



