package com.example.edushare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ClassroomActivity extends AppCompatActivity {
    TextView classname, teachername, teachermail, classdescription, classschedule;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    Button actionButton;
    String userType;
    String className;
    String mKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);
        classname=findViewById(R.id.classNameView);
        teachername=findViewById(R.id.classTeacherNameView);
        teachermail=findViewById(R.id.classTeacherMailView);
        actionButton=findViewById(R.id.multipleActionButton);
        classdescription=findViewById(R.id.classDescriptionView);
        classschedule=findViewById(R.id.classTimingView);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("class");
        Intent intent=getIntent();
        className=intent.getStringExtra("Class_Name");
        userType=intent.getStringExtra("User_Type");
        classname.setText(className);
        Toast.makeText(this, userType, Toast.LENGTH_SHORT).show();
        if(userType.equals("admin")) {
            actionButton.setText("Go Live Now...");
        } else if(userType.equals("official")) {
            actionButton.setText("View Live Session");
        } else { }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String cName = ds.child("classname").getValue(String.class);
                    if(className.equals(cName)) {
                        mKey=ds.getKey();
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
                                        break;
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

    public void goBackAction(View view) {
        startActivity(new Intent(ClassroomActivity.this, MainActivity.class));
        finish();
        return;
    }

    public void buttonPressAction(View view) {
        if(userType.equals("unofficial")) {
            Intent intent = new Intent(ClassroomActivity.this, JoinProcessActivity.class);
            intent.putExtra("Class_Name", className);
            startActivity(intent);
            finish();
            return;
        } else if(userType.equals("admin"))  {
        } else { }
    }

    public void viewResourcesAction(View view) {
        if(userType.equals("unofficial")) {
            Toast.makeText(this, "Admit class to view resources", Toast.LENGTH_SHORT).show();
        }
    }

    public void viewDiscussionAction(View view) {
        if(userType.equals("unofficial")) {
            Toast.makeText(this, "Admit class to join discussion", Toast.LENGTH_SHORT).show();
        }
    }

    public void editClassDescription(View view) {
        if(userType.equals("admin")) {
            updateDesorSchedule("des");
        }
    }

    public void editClassSchedule(View view) {
        if(userType.equals("admin")) {
            updateDesorSchedule("schedule");
        }
    }

    private void updateDesorSchedule(final String userfunction) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        if(userfunction.equals("des")) {
            builder.setTitle("Edit Description");
        } else {
            builder.setTitle("Edit Schedule");
        }
        LinearLayout linearLayout=new LinearLayout(this);
        final EditText uchanges=new EditText(this);
        uchanges.setHint("Write here...");
        uchanges.setMinEms(20);
        linearLayout.addView(uchanges);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userC=uchanges.getText().toString().trim();
                beginEditing(userC, userfunction);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void beginEditing(String userchanges, String eType) {
        if(userchanges=="") {
            Toast.makeText(this, "Write Something...", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> results = new HashMap<>();
            if(eType.equals("schedule")) {
                results.put("classdate", userchanges);
            } else {
                results.put("classdes", userchanges);
            }
            databaseReference.child(mKey).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ClassroomActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ClassroomActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
