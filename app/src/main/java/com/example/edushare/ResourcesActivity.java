package com.example.edushare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ResourcesActivity extends AppCompatActivity {
    String userType;
    String className;
    Uri pdfUri;
    Button uploadButton;
    ProgressDialog progressDialog;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    ListView resourceList;
    TextView selectFileTextView, selectStatusTextView, classNameTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);
        Intent intent=getIntent();
        userType=intent.getStringExtra("User_Type");
        className=intent.getStringExtra("Class_Name");
        uploadButton=findViewById(R.id.uploader);
        selectFileTextView=findViewById(R.id.selectFileLabel);
        resourceList=findViewById(R.id.resourceListView);
        selectStatusTextView=findViewById(R.id.selectStatusLabel);
        classNameTextView=findViewById(R.id.classNameView);

        classNameTextView.setText(className);
        if(userType.equals("admin")) {
            selectStatusTextView.setVisibility(View.VISIBLE);
            selectFileTextView.setVisibility(View.VISIBLE);
            uploadButton.setVisibility(View.VISIBLE);
        }
    }

    public void selectResourceToUpload(View view) {
        if(ContextCompat.checkSelfPermission(ResourcesActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            selectYourFile();
        } else {
            ActivityCompat.requestPermissions(ResourcesActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},10);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==10 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            selectYourFile();
        } else {
            Toast.makeText(this, "Allow Permission to Continue...", Toast.LENGTH_SHORT).show();
        }

    }

    private void selectYourFile() {
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            selectStatusTextView.setText("File Selected");
        } else {
            Toast.makeText(this, "Select a File", Toast.LENGTH_SHORT).show();
        }
    }

   /* public void uploadFile(View view) {
        if(pdfUri!=null) {
            Toast.makeText(this, "dd", Toast.LENGTH_SHORT).show();
            progressDialog=new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Uploading File...");
            progressDialog.setProgress(0);
            progressDialog.show();
            StorageReference storageReference=FirebaseStorage.getInstance().getReference().child("Class Resources").child(className);
       //     final StorageReference resourcesReference=storageReference.child(download);
            resourcesReference.putFile(pdfUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(ResourcesActivity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
                        final String downloadUrl=resourcesReference.getDownloadUrl().toString();
                        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference=firebaseDatabase.getReference("Resources");
                        databaseReference.child(className).child(fileName).setValue(downloadUrl);
                        progressDialog.dismiss();

                    } else {
                        Toast.makeText(ResourcesActivity.this, "Something is wrong...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Choose File to Upload", Toast.LENGTH_SHORT).show();
        }
    }

    */

    public void goBackAction(View view) {
        Intent intent=new Intent(ResourcesActivity.this,ClassroomActivity.class);
        intent.putExtra("Class_Name",className);
        intent.putExtra("User_Type", userType);
        startActivity(intent);
        finish();
        return;
    }
}
