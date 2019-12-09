package com.example.edushare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class JoinProcessActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    TextView classnameview;
    FirebaseAuth firebaseAuth;
    String className;
    ArrayList<Boolean> classInformation;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_process);
        classnameview=findViewById(R.id.classNameConfirmView);
        classInformation=new ArrayList<>();
        Intent intent=getIntent();
        className=intent.getStringExtra("Class_Name");
        classnameview.setText(className);
    }

    public void paidVersionJoinAction(View view) {
        Toast.makeText(this, "Not available now, use trial version", Toast.LENGTH_SHORT).show();
    }

    public void freeTrialJoinAction(View view) {
        checkUserCourseAddStatus();
    }

    private void checkUserCourseAddStatus() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("studentlist");

        String userMail = firebaseUser.getEmail();

        Query query=databaseReference.orderByChild("studentmail").equalTo(userMail);

        ValueEventListener valueEventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot ds: dataSnapshot.getChildren()) {
                        Query query2=databaseReference.orderByChild("classname").equalTo(className);
                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    Toast.makeText(JoinProcessActivity.this, "Course Already Added", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(JoinProcessActivity.this, MainActivity.class));
                                    finish();
                                    return;
                                } else {
                                    firebaseAuth = FirebaseAuth.getInstance();
                                    firebaseUser = firebaseAuth.getCurrentUser();
                                    String classlistID = databaseReference.push().getKey();
                                    databaseReference = FirebaseDatabase.getInstance().getReference("studentlist");
                                    final String userMail = firebaseUser.getEmail();
                                    HashMap<Object, String> hashMap = new HashMap<>();
                                    hashMap.put("classname", className);
                                    hashMap.put("studentmail", userMail);
                                    databaseReference.child(classlistID).setValue(hashMap);
                                    Toast.makeText(JoinProcessActivity.this, "You have successfully joined the class", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(JoinProcessActivity.this, MainActivity.class));
                                    finish();
                                    return;
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    public void goBackAction(View view) {
        startActivity(new Intent(JoinProcessActivity.this, SearchActivity.class));
        finish();
        return;
    }

}
