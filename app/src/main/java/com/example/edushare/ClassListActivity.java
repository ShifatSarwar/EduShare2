package com.example.edushare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class ClassListActivity extends AppCompatActivity {
    ListView myClassListView;
    ArrayList<String> myClassResultList;
    ArrayAdapter<String> myClassResultAdapter;

    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    String userType="official";
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);
        myClassListView = findViewById(R.id.classListView);
        myClassResultList =new ArrayList<>();
        firebaseDatabase= FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        myClassResultAdapter =new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, myClassResultList);
        databaseReference=firebaseDatabase.getReference("studentlist");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String cMail = ds.child("studentmail").getValue(String.class);
                    if(firebaseUser.getEmail().equals(cMail)) {
                        myClassResultList.add(ds.child("classname").getValue(String.class));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        myClassListView.setAdapter(myClassResultAdapter);

        myClassListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToClass(myClassResultList.get(position), firebaseUser.getUid());
            }
        });
    }

    private void goToClass(final String name, final String id) {
        databaseReference=FirebaseDatabase.getInstance().getReference("class");
        Query query=databaseReference.orderByChild("classname").equalTo(name);
        ValueEventListener valueEventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot ds: dataSnapshot.getChildren()) {
                        Query query2=databaseReference.orderByChild("uid").equalTo(id);
                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Intent intent=new Intent(ClassListActivity.this, ClassroomActivity.class);
                                intent.putExtra("Class_Name", name);
                                if(dataSnapshot.exists()) {
                                    intent.putExtra("User_Type", "admin");
                                } else {
                                    intent.putExtra("User_Type", "official");
                                }
                                startActivity(intent);
                                finish();
                                return;
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
        startActivity(new Intent(ClassListActivity.this, MainActivity.class));
        finish();
        return;
    }

}
