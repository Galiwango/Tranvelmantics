package com.example.tranvelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.Resource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    public static final int HANDLE = 42;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    EditText textTitle;
    EditText textPrice;
    EditText textDescription;
    private TravelDeal deal;
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if(deal==null)
            deal = new TravelDeal();

        Button button = findViewById(R.id.button);
        imageView =findViewById(R.id.imageView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(intent.createChooser(intent,"Insert picture"), HANDLE);

            }
        });


       // utilsclass.openFBpref("traveldeals",listActivity.class);
        firebaseDatabase = utilsclass.firebaseDatabase;
        databaseReference = utilsclass.databaseReference;
        textTitle = findViewById(R.id.title);
        textPrice = findViewById(R.id.price);
        textDescription = findViewById(R.id.description);

        if(deal.getImageUrl()!=null)
        showImage(deal.getImageUrl());

        this.deal = deal;
        textTitle.setText(deal.getTitle());
        textDescription.setText(deal.getDescription());
        textPrice.setText(deal.getPrice());




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);
        if(utilsclass.isAdmin==true){
            menu.findItem(R.id.save_menu).setVisible(true);
            menu.findItem(R.id.delete_menu).setVisible(true);
            enableEditText(true);
        }else {
            menu.findItem(R.id.save_menu).setVisible(false);
            menu.findItem(R.id.delete_menu).setVisible(false);
            enableEditText(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this,"Deal saved", Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return  true;
            case R.id.delete_menu:
                deleteEntry();
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_SHORT).show();
                backToList();
                return  true;
             default:
                 return  super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==HANDLE && resultCode==RESULT_OK){
            Uri imageUri = data.getData();
            final StorageReference ref = utilsclass.storageReference.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String downloadUrl = uri.toString();
                            deal.setImageUrl(downloadUrl);
                            String pictureName = taskSnapshot.getStorage().getPath();
                           deal.setImageName(pictureName);
                            showImage(downloadUrl);
                        }
                    });

                }
            });

        }
    }

    private void clean() {
        textTitle.setText("");
        textPrice.setText("");
        textDescription.setText("");
    }

    private void saveDeal() {

        deal.setTitle(textTitle.getText().toString());
        deal.setPrice(textPrice.getText().toString());
        deal.setDescription(textDescription.getText().toString());
        if (deal.getId()==null) {
            databaseReference.push().setValue(deal);
        }
        else{
            databaseReference.child(deal.getId()).setValue(deal);
        }

    }
    private void deleteEntry(){

        if(deal==null){
            Toast.makeText(this,"this entry doesn't exist", Toast.LENGTH_LONG).show();
            return;
        }
        databaseReference.child(deal.getId()).removeValue();
        if(deal.getImageName() != null && deal.getImageName().isEmpty()==false ){
            StorageReference picref = utilsclass.firebaseStorage.getReference().child(deal.getImageName());
            picref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
        startActivity(new Intent(this, MainActivity.class));


    }
    private void enableEditText(boolean isEnable){
        textTitle.setEnabled(isEnable);
        textPrice.setEnabled(isEnable);
        textDescription.setEnabled(isEnable);
    }
public void showImage(String url){
        if(url!=null && url.isEmpty()==false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width,width*2/3)
                    .centerCrop()
                    .into(imageView);
        }

}
    private void backToList(){
        Intent intent =new Intent(this,listActivity.class);
        startActivity(intent);

    }
}
