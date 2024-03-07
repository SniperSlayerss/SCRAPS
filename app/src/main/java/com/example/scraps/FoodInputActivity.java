package com.example.scraps;

import android.app.Activity;
import android.app.Dialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import com.example.scraps.R;
import com.example.scraps.DBModels.FoodItem;
import com.example.scraps.DBModels.Users;
import com.google.android.material.navigation.NavigationView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import android.Manifest;

public class FoodInputActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    private MyEditTextDatePicker expiryDatePicker, purchaseDatePicker;
    private EditText foodNameEditText, expiryDateEditText, purchaseDateEditText, priceEditText;
    private Button submitButton, cameraButton;
    private FirebaseAuth mAuth;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private String currentPhotoPath;
    private ActivityResultLauncher<Intent> mTakePicture;
    private PreviewView mPreviewView;
    private ImageCapture imageCapture = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_input);

        ImageView leftIcon = findViewById(R.id.left_icon);
        ImageView rightIcon = findViewById(R.id.right_icon);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), HomeActivity.class); //v.context() lets you access current class
                startActivity(intent);
            }
        });

        rightIcon.setOnClickListener(new View.OnClickListener() {
            //button to open menu
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "MENU TOGGLE CLICKED", Toast.LENGTH_SHORT).show();
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        foodNameEditText = findViewById(R.id.food_name_editText);
        expiryDatePicker = new MyEditTextDatePicker(this, R.id.expiry_date_editText);
        purchaseDatePicker = new MyEditTextDatePicker(this, R.id.purchase_date_editText);
        priceEditText = findViewById(R.id.price_editText);
        submitButton = findViewById(R.id.submit_button);
        cameraButton = findViewById(R.id.camera);
        mPreviewView = findViewById(R.id.camera_preview_view);

        mAuth = FirebaseAuth.getInstance();

        mTakePicture = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.e("CameraActivity", "Image capture was successfully");
                    }
                }
        );

        if (checkCameraPermission()) {
            startCamera();
        } else {
            requestCameraPermission();
        }


        cameraButton.setOnClickListener(v -> {
            if (imageCapture != null) {
                takePhoto(imageCapture);
            } else {
                Toast.makeText(FoodInputActivity.this, "Unable to capture photo", Toast.LENGTH_SHORT).show();
            }
        });



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values
                String foodName = foodNameEditText.getText().toString().trim();
                String expiryDate = expiryDatePicker._editText.getText().toString().trim();
                String purchaseDate = purchaseDatePicker._editText.getText().toString().trim();
                try {
                    double price = Double.parseDouble(priceEditText.getText().toString().trim());
                    uploadImageAndSaveData(foodName, expiryDate, purchaseDate, price);
                } catch (NumberFormatException e) {
                    Toast.makeText(FoodInputActivity.this, "Invalid price", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImageAndSaveData(String foodName, String expiryDate, String purchaseDate, double price) {
        if (currentPhotoPath == null) {
            Toast.makeText(FoodInputActivity.this, "No image captured!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Uri fileUri = Uri.fromFile(new File(currentPhotoPath));
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/" + fileUri.getLastPathSegment());

        imageRef.putFile(fileUri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
            String imageUrl = downloadUri.toString();
            // Now save all data including the image URL to Firebase Database
            saveDataToDatabase(foodName, expiryDate, purchaseDate, price, imageUrl);
        })).addOnFailureListener(e -> {
            Log.e("FirebaseStorage", "Upload failed: " + e.getLocalizedMessage());
            Toast.makeText(FoodInputActivity.this, "Upload failed: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });
        } catch (Exception e){
            Log.d("RAWR",  currentPhotoPath);
        }
    }


    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private void saveDataToDatabase(String foodName, String expiryDate, String purchaseDate, double price, String imageUrl) {
        String firebaseId = mAuth.getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Adjust this path as needed for your application's data structure
        DatabaseReference usersRef = databaseRef.child("Users").child(firebaseId);

        // Create unique key for each food item
        String foodItemId = usersRef.child("foodItems").push().getKey();
        FoodItem foodItem = new FoodItem(foodName, expiryDate, purchaseDate, firebaseId, "", price, imageUrl, false);

        // Assuming "foodItems" is a node under each user where their food items are stored
        usersRef.child("foodItems").child(foodItemId).setValue(foodItem).addOnSuccessListener(aVoid -> {
            Toast.makeText(FoodInputActivity.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Log.e("FirebaseDatabase", "Data saving failed: " + e.getLocalizedMessage());
            Toast.makeText(FoodInputActivity.this, "Data saving failed!", Toast.LENGTH_SHORT).show();
        });
    }

    private void requestCameraPermission() {
        if (!checkCameraPermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to use the camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error starting camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // Use the global imageCapture variable
        imageCapture = new ImageCapture.Builder()
                .build();

        // Make sure that the camera preview is assigned to the surface provider.
        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

        // Bind the lifecycle of the camera to the activity
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

        // Set the OnClickListener for the camera button to trigger the takePhoto method
        cameraButton.setOnClickListener(v -> {
            if (imageCapture != null) {
                takePhoto(imageCapture);
            } else {
                Toast.makeText(FoodInputActivity.this, "Camera not ready. Please wait.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void takePhoto(ImageCapture imageCapture) {
        File photoFile = createImageFile();

        if (photoFile == null) {
            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            return;
        }

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        // Uri contentUri = outputFileResults.getSavedUri(); // If you want to use the saved Uri directly.
                        runOnUiThread(() -> Toast.makeText(FoodInputActivity.this, "Photo capture succeeded: " + photoFile.getAbsolutePath(), Toast.LENGTH_SHORT).show());
                        currentPhotoPath = photoFile.getAbsolutePath();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e("CameraXApp", "Photo capture failed: " + exception.getMessage(), exception);
                    }
                }
        );
    }


    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            // Save a file path for use with ACTION_VIEW intents
            currentPhotoPath = image.getAbsolutePath();
        } catch (IOException e) {
            Log.e("createImageFile", "Error while creating file: " + e.getMessage());
        }

        return image;
    }


    public static class MyEditTextDatePicker  implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
        EditText _editText;
        private int _day;
        private int _month;
        private int _birthYear;
        private Context _context;

        public MyEditTextDatePicker(Context context, int editTextViewID)
        {
            Activity act = (Activity)context;
            this._editText = (EditText)act.findViewById(editTextViewID);
            this._editText.setOnClickListener(this);
            this._context = context;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            _birthYear = year;
            _month = monthOfYear;
            _day = dayOfMonth;
            updateDisplay();
        }
        @Override
        public void onClick(View v) {
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

            DatePickerDialog dialog = new DatePickerDialog(_context, this,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();

        }

        // updates the date in the birth date EditText
        private void updateDisplay() {

            _editText.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(_day).append("-").append(_month + 1).append("-").append(_birthYear).append(" "));
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.menu_food_item) {
            Intent intent = new Intent(this, FoodDatabaseScreenActivity.class); //v.context() lets you access current class
            startActivity(intent);
        }
        else if (itemID == R.id.menu_home) {
            Intent intent = new Intent(this, HomeActivity.class); //v.context() lets you access current class
            startActivity(intent);
        }
        else if (itemID == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class); //v.context() lets you access current class
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }
}
