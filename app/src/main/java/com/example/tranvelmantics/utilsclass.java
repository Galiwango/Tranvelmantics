package com.example.tranvelmantics;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class utilsclass {
    private static int RC_SIGN_IN = 123;
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    public static utilsclass firebaseutil;
    public static FirebaseStorage firebaseStorage;
    public static StorageReference storageReference;
    public static FirebaseAuth firebaseAuth;
    private static FirebaseAuth.AuthStateListener authStateListener;
    public static ArrayList<TravelDeal> travelDeals;
    private static listActivity caller;
    public static boolean isAdmin;

    private utilsclass(){}
    public static  void openFBpref(String ref, final listActivity callerActivity){
        if(firebaseutil == null){

            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                   if(firebaseAuth.getCurrentUser()==null){
                       utilsclass.signIn();
                     //  Toast.makeText(callerActivity, "Welcome Back!", Toast.LENGTH_LONG).show();
                   }
                   else{
                       String userId = firebaseAuth.getUid();
                       checkAdmin(userId);
                      // Toast.makeText(callerActivity, "Welcome Back! Admin", Toast.LENGTH_LONG).show();
                   }


                }
            };
            connectStorage();

        }
        travelDeals = new ArrayList<>();
        databaseReference = firebaseDatabase.getReference().child(ref);
        firebaseutil = new utilsclass();
    }
    private static void checkAdmin(String uid){
        utilsclass.isAdmin = false;
        DatabaseReference ref = firebaseDatabase.getReference().child("administrators").child(uid);
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                utilsclass.isAdmin=true;
                caller.showMenu();
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
        ref.addChildEventListener(listener);
    }
    public static void signIn(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);
    }

    public static  void attachListenner(){
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    public static void detachListenner(){
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
    public  static void connectStorage(){
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("deals_pictures");
    }

}
