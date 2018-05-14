package com.ardasatata.testmap_pedagangapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends Activity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference dataPedagang;
    private Pedagang pedagang;

    EditText ETname;
    EditText ETemail;
    EditText ETinfo;

    ImageView BTsave;

    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        dataPedagang = FirebaseDatabase.getInstance().getReference("pedagang").child(firebaseAuth.getCurrentUser().getUid());

        ETname = findViewById(R.id.profileName);
        ETemail = findViewById(R.id.profileEmail);
        ETinfo = findViewById(R.id.profileInfoDagang);
        BTsave = findViewById(R.id.profileButtonSave);

        if (firebaseAuth.getCurrentUser() == null) {
            // user is already logged in
            // open profile activity
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            //close this activity
            finish();

        }

        dataPedagang.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pedagang = dataSnapshot.getValue(Pedagang.class);
                ETname.setText(pedagang.getNamaDagang());
                ETemail.setText(pedagang.getEmail());
                ETinfo.setText(pedagang.getInfo());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        BTsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataPedagang.child("namaDagang").setValue(ETname.getText().toString());
                dataPedagang.child("email").setValue(ETemail.getText().toString());
                dataPedagang.child("info").setValue(ETinfo.getText().toString());

                Toast toast = Toast.makeText(getApplicationContext(), "Profile Saved", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }
}
