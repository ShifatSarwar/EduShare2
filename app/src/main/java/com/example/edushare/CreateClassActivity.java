package com.example.edushare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CreateClassActivity extends AppCompatActivity {
    private EditText readClassName, readClassDescription, readClassSchedule;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);
        readClassName=findViewById(R.id.classNameReader);
        readClassDescription=findViewById(R.id.classDescriptionReader);
        readClassSchedule=findViewById(R.id.classScheduleReader);
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference=firebaseDatabase.getReference("class");
    }

    public void registerClass(View view) {
        final String className = readClassName.getText().toString();
        final String classDescription = readClassDescription.getText().toString();
        final String classSchedule=readClassSchedule.getText().toString();

        if (className.equals("") || classDescription.equals("") || classSchedule.equals("")) {
            Toast.makeText(this, "Fill Empty Fields", Toast.LENGTH_SHORT).show();
        } else {
            String classID=databaseReference.push().getKey();
            FirebaseUser user=firebaseAuth.getCurrentUser();
            String uid=user.getUid();
            HashMap<Object,String> hashMap=new HashMap<>();
            hashMap.put("classname",className);
            hashMap.put("uid",uid);
            hashMap.put("classdes",classDescription);
            hashMap.put("classdate",classSchedule);
            hashMap.put("approveStatus", "false");
            databaseReference.child(classID).setValue(hashMap);
            Toast.makeText(this, "Classroom Created", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CreateClassActivity.this, MainActivity.class));
            finish();
            return;
        }
    }

    public void goBackAction(View view) {
        startActivity(new Intent(CreateClassActivity.this, MainActivity.class));
        finish();
        return;
    }
}
