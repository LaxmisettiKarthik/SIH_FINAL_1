package com.elite.sih_final_1;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.elite.sih_final_1.Models.PostModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FirebaseUtilities {

    Context context;
    ProgressDialog progressDialog;
    FirebaseStorage firebaseStorage;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;


    public FirebaseUtilities(Context contex){
        this.context = contex;
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    String getTimeStamp(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    public void uploadImage(Uri uri, PostModel postModel){
        progressDialog.show();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        StorageReference storageReference = firebaseStorage.getReference().child("Images/" + firebaseUser.getUid() + "/" + uri.getLastPathSegment());
        storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progressDialog.dismiss();
                            Log.d("storageReference URL", "On Success" + uri.toString());
                            postModel.setImageUrl(uri.toString());
                            post(postModel);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
                Toast.makeText(context, "Image Upload falied"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void post(PostModel postModel) {
        progressDialog.show();
        DocumentReference documentReference= firebaseFirestore.collection("profiles").document(firebaseUser.getUid());
        postModel.setDocId(documentReference.getId());

        documentReference.set(postModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                Toast.makeText(context, "Posted Successfully!!!", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
                Toast.makeText(context, "profile posting failed..!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
