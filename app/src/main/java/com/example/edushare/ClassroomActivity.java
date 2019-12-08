package com.example.edushare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ClassroomActivity extends AppCompatActivity {
    TextView classname, teachername, teachermail, classdescription, classschedule;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);
        classname=findViewById(R.id.classNameView);
        teachername=findViewById(R.id.classTeacherNameView);
        teachermail=findViewById(R.id.classTeacherMailView);
        classdescription=findViewById(R.id.classDescriptionView);
        classschedule=findViewById(R.id.classTimingView);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("class");
        Intent intent=getIntent();
        className=intent.getStringExtra("Class_Name");
        classname.setText(className);;

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String cName = ds.child("classname").getValue(String.class);
                    if(className.equals(cName)) {
                        final String mDes = ds.child("classdes").getValue(String.class);
                        final String mSchedule=ds.child("classdate").getValue(String.class);
                        classdescription.setText(mDes);
                        classschedule.setText(mSchedule);
                        final String teacherID=ds.child("uid").getValue(String.class);
                        databaseReference=firebaseDatabase.getReference("users");

                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                                    final String val=ds.child("uid").getValue(String.class);
                                    if(val.equals(teacherID)) {
                                        final String cTName=ds.child("name").getValue(String.class);
                                        final String cTMail=ds.child("email").getValue(String.class);
                                        teachername.setText(cTName);
                                        teachermail.setText(cTMail);
                                    }
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
        });
    }

    public void goToLiveRoomAction(View view) {
    }

    public void viewResourcesAction(View view) {}

    public void viewDiscussionAction(View view) {}

    public void goBackAction(View view) {
        startActivity(new Intent(ClassroomActivity.this, MainActivity.class));
        finish();
        return;
    }
}
