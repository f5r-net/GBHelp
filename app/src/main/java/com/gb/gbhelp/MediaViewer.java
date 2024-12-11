package com.gb.gbhelp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MediaViewer extends BaseActivity {

    StorageReference storageReference;
    ProgressDialog progressDialog;
    Uri imageUri;
    String uploadedMediaUrl = "null";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_viewer);
        ImageView imageView = findViewById(R.id.pager);
        FloatingActionButton floatingActionButton = findViewById(R.id.sendMedia);
        Intent intent = getIntent();
        String intentUri = intent.getStringExtra("imageUri");
        if (intent.hasExtra("viewImage")) {
            floatingActionButton.setVisibility(View.GONE);

        }
        if (intentUri.startsWith("GBHelpMedia/")){
            Bitmap bitmap = BitmapFactory.decodeFile(intentUri.replace("GBHelpMedia/",""));
            imageView.setImageBitmap(bitmap);
            GB.printLog("MediaViewer/path="+intentUri);
            floatingActionButton.setVisibility(View.GONE);
        }else {
            imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));
            GB.printLog("MediaViewer/uri="+imageUri);
            imageView.setImageURI(imageUri);
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();

            }
        });
    }

    private void uploadImage() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File....");
        progressDialog.show();


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("images/"+fileName);


        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(MediaViewer.this,"Successfully Uploaded/",Toast.LENGTH_SHORT).show();
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Got the download URL for 'users/me/profile.png'
                                GB.printLog("GBLink/"+uri.toString());
                                uploadedMediaUrl = uri.toString();

                                Intent intent = new Intent();
                                intent.putExtra("mediaUrl",uploadedMediaUrl);
                                setResult(GB.MEDIA_UPLOADED,intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                finish();
                            }
                        });
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(MediaViewer.this,"Failed to Upload/"+e.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });

    }

}
