package com.elite.sih_final_1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;



import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.elite.sih_final_1.Models.PostModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    private static final int PERMISSION_IMAGE = 23;
    private Spinner spinner;
    private ImageView pickImage;
    private EditText firstNameText, lastNameText, ageText, genderText;
    private CheckBox checkBox;
    private Button submit;
    int PICK_IMAGE = 12;
    public Uri selectedImageUri; // Generated Image url
    FirebaseUtilities firebaseUtilities;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //facebook multi user login sdk
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
        //facebook multi user login sdk


        // This is Array Adapter which will stores the values of spinner.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item); // setting the view resource

        mAuth = FirebaseAuth.getInstance();

        pickImage = findViewById(R.id.imageView);
        spinner = findViewById(R.id.spinner);
        firstNameText = findViewById(R.id.firstName);
        lastNameText = findViewById(R.id.lastName);
        ageText = findViewById(R.id.age);
        genderText = findViewById(R.id.gender);
        checkBox = findViewById(R.id.checkBox);
        submit = findViewById(R.id.submit);

        firebaseDatabase = FirebaseDatabase.getInstance();  // realtime database variable declaration
        firebaseUtilities = new FirebaseUtilities(this); // firebaseUtilities variable

        pickImage.setOnClickListener(new View.OnClickListener() { // If we click pickImage, we'll ask for the storage permission
            @Override
            public void onClick(View view) {
                checkRunTimePermissions();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() { // If we click the submit, we'll upload the image to firebaseStorage

            @Override
            public void onClick(View view) {
                if (selectedImageUri == null) {  // If image is not selected
                    Toast.makeText(ProfileActivity.this, "Please select your profile image!", Toast.LENGTH_SHORT).show();
                }
                post(); // Uploading the image
                sendDataToDatabase();  //  Sends the data to realtime database
            }
        });
    }

    private void post() {
        String firstName = firstNameText.getText().toString().trim();
        String lastName = lastNameText.getText().toString().trim();
        String age = ageText.getText().toString().trim();             // Storing the data in Strings.
        String gender = genderText.getText().toString().trim();
        String email = mAuth.getCurrentUser().getEmail();
        String lang = spinner.getSelectedItem().toString();

        if(selectedImageUri == null){
            Toast.makeText(this, "Please Select the image!!", Toast.LENGTH_SHORT).show();
            return;
        }

        PostModel postModel = new PostModel();
        postModel.setFirstName(firstName);
        postModel.setLastName(lastName);
        postModel.setAge(age);
        postModel.setGender(gender);
        postModel.setLang(lang);

        firebaseUtilities.uploadImage(selectedImageUri,postModel);
    }

    private void sendDataToDatabase() {  // this will send the text data to database.

        String firstName = firstNameText.getText().toString().trim();
        String lastName = lastNameText.getText().toString().trim();
        String age = ageText.getText().toString().trim();             // Storing the data in Strings.
        String gender = genderText.getText().toString().trim();
        String email = mAuth.getCurrentUser().getEmail();
        String lang = spinner.getSelectedItem().toString();


        com.elite.sih_final_1.User user = new User(email,firstName,lastName,age,gender,lang); // Storing all the data into user
//        user.setEmail(email);
        firebaseDatabase.getReference("Users")  // In realtime database: "users" , in "{Uid}" , setting the user data to storage.
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ProfileActivity.this, "data transefered to realtime database", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(ProfileActivity.this, "Data is not transfered!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void checkRunTimePermissions() { // This function is to check runtime permissions, to access images.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_IMAGE);
            } else {
                pickImageFromGallery(); // If permission is given, we'll call the pickImageFromGallery function
            }
        } else {
            pickImageFromGallery();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_IMAGE && grantResults.length > 0) {
            pickImageFromGallery();  // If permission is given, we'll pick image from gallery
        }
    }

    private void pickImageFromGallery() { // This function takes us to gallery, we can pick any photo from there.
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {  // Checks if the image is selected or not.
            selectedImageUri = data.getData();
            pickImage.setImageURI(selectedImageUri);
        }

    }
}