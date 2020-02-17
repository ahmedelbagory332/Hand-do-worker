package com.fcicustomer.elbagory.sal7necustoner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.fcicustomer.elbagory.sal7necustoner.Models.ChooseSection_Model;
import com.fcicustomer.elbagory.sal7necustoner.Models.IDs;
import com.fcicustomer.elbagory.sal7necustoner.Models.Worker;
import com.fcicustomer.elbagory.sal7necustoner.NetworkUtils.NetworkUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class Verification extends AppCompatActivity {

    DatabaseReference databaseReference;
    private CircleImageView ImageView_profile;
    private Uri mImageUri = null;
    private Uri mImageUriid1 = null;
    private Uri mImageUriid2 = null;
    private boolean mImageid1_open = false;
    private boolean mImageid2_open = false;
    private boolean mImageprofile_open = false;
    private StorageReference mStorageRef;
    private StorageReference mStorageRef_ids;
    private StorageTask mUploadTask;
    byte[] final_img;
    byte[] final_imgid1;
    byte[] final_imgid2;

    private ProgressDialog dialog;
    private EditText name  , phone , id;
    TextView section;
    private static Animation shakeAnimation;
    LinearLayout linearLayout , id_layout;
    private  ImageView ImageViewid1 , ImageViewid2;
    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;
    AlertDialog alertDialog_newSection;
    private UserValidation userValidation;
    TextView login ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        dialog = new ProgressDialog(this);
        mStorageRef = FirebaseStorage.getInstance().getReference("Workers_ProfileImages");
        mStorageRef_ids = FirebaseStorage.getInstance().getReference("Workers_IDs");
        userValidation = new UserValidation(this);

        name = findViewById(R.id._name);
        section = findViewById(R.id._section);
        phone = findViewById(R.id._phone);
        id = findViewById(R.id._id);
        login = findViewById(R.id._login);

        ImageViewid1 = findViewById(R.id.imageView_id1);
        ImageViewid2 = findViewById(R.id.imageView_id2);

        shakeAnimation = AnimationUtils.loadAnimation(this,
                R.anim.shake);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        linearLayout = findViewById(R.id.user_info);
        id_layout = findViewById(R.id.idlayout);
        ImageView_profile = findViewById(R.id.image_profile);
        ImageView_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageprofile_open = true;
                mImageid1_open = false;
                mImageid2_open = false;
                openFileChooser();
            }
        });

        ImageViewid1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageprofile_open = false;
                mImageid1_open = true;
                mImageid2_open = false;
                openFileChooser();
            }
        });

        ImageViewid2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageprofile_open = false;
                mImageid1_open = false;
                mImageid2_open = true;
                openFileChooser();
            }
        });

        section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                    showAlertDialog(R.layout.choose_sections);

                }
                else
                {
                    TastyToast.makeText(getApplicationContext(), "تاكد من اتصالك بالانترنت",
                            TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }
        });

    }


    //FOR ACTIVITY RESULT PERMISSION
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 555 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openFileChooser();
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 555);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChooser() {
        CropImage.startPickImageActivity(this);
    }

    private void croprequest(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //RESULT FROM SELECTED IMAGE
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            croprequest(imageUri);
        }
        //RESULT FROM CROPING ACTIVITY
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && mImageprofile_open==true) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mImageUri = result.getUri();
                Glide.with(this).load(mImageUri).into(ImageView_profile);
                String realPath = GetFilePathFromDevice.getPath(this, mImageUri);

                File actualImage = new File(realPath);
                try {
                    Bitmap compressedImage = new Compressor(this)
                            .compressToBitmap(actualImage);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    final_img = baos.toByteArray();

                } catch (IOException e) {
                    e.printStackTrace();
                }



            }


        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && mImageid1_open==true)
        {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mImageUriid1 = result.getUri();
                Glide.with(this).load(mImageUriid1).into(ImageViewid1);
                String realPath = GetFilePathFromDevice.getPath(this, mImageUriid1);

                File actualImage = new File(realPath);
                try {
                    Bitmap compressedImage = new Compressor(this)
                            .compressToBitmap(actualImage);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    final_imgid1 = baos.toByteArray();

                } catch (IOException e) {
                    e.printStackTrace();
                }



            }

        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && mImageid2_open==true)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mImageUriid2 = result.getUri();
                Glide.with(this).load(mImageUriid2).into(ImageViewid2);
                String realPath = GetFilePathFromDevice.getPath(this, mImageUriid2);

                File actualImage = new File(realPath);
                try {
                    Bitmap compressedImage = new Compressor(this)
                            .compressToBitmap(actualImage);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    final_imgid2 = baos.toByteArray();

                } catch (IOException e) {
                    e.printStackTrace();
                }



            }
        }
    }

    private void uploadFile() {


        dialog.setMessage("من فضلك انتظر....");
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        if (mImageUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));



            UploadTask uploadTask = fileReference.putBytes(final_img);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {




                            Worker workers_models = new Worker();


                            workers_models.setName(name.getText().toString());
                            workers_models.setPhone(phone.getText().toString());
                            workers_models.setImg(uri.toString());
                            workers_models.setSection(section.getText().toString());
                            workers_models.setId(id.getText().toString());
                            workers_models.setRate(0);
                            workers_models.setIScheked(false);


                            databaseReference.child("Workers").push().setValue(workers_models);


                        }
                    });


                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Verification.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                                TastyToast.makeText(getApplicationContext(), "حدث خطا ما",
                                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            }
                        }
                    });

        } else {
            TastyToast.makeText(getApplicationContext(), "لم يتم اختيار صورة",
                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            dialog.dismiss();

        }
    }
    private void uploadIDs() {

        if (mImageUriid1 != null) {
            final StorageReference fileReference = mStorageRef_ids.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUriid1));



            UploadTask uploadTask = fileReference.putBytes(final_imgid1);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri1) {



                            // upload part two of id image
                            if (mImageUriid2 != null) {
                                final StorageReference fileReference = mStorageRef_ids.child(System.currentTimeMillis()
                                        + "." + getFileExtension(mImageUriid2));



                                UploadTask uploadTask = fileReference.putBytes(final_imgid2);

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {

                        IDs workers_ids= new IDs();
                        workers_ids.setId_img1(uri1.toString());
                        workers_ids.setId_img2(uri2.toString());
                        // upload both images of id image
                        databaseReference.child("IDs").child(phone.getText().toString()).push().setValue(workers_ids);

                        startActivity(new Intent(getApplicationContext(), Home.class));
                        userValidation.writeLoginStatus(true);
                        userValidation.writephone(phone.getText().toString());
                        overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                        finish();
                        TastyToast.makeText(getApplicationContext(), "تم التسجيل بنجاح",
                                TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }


                    }
                });


                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Verification.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                if (dialog.isShowing()) {
                                                    dialog.dismiss();
                                                    TastyToast.makeText(getApplicationContext(), "حدث خطا ما",
                                                            TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                                                }
                                            }
                                        });

                            } else {
                                TastyToast.makeText(getApplicationContext(), "لم يتم اختيار صورة",
                                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            }


                        }
                    });


                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Verification.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                                TastyToast.makeText(getApplicationContext(), "حدث خطا ما",
                                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            }
                        }
                    });

        } else {
            TastyToast.makeText(getApplicationContext(), "لم يتم اختيار صورة",
                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            dialog.dismiss();

        }
    }


    public void signup(View view) {
        if (name.getText().toString().equals("") || phone.getText().toString().equals("")||
                section.getText().toString().equals("") || id.getText().toString().equals("")||
                id.getText().length()<14 || phone.getText().length()<11 || mImageUri==null){

            linearLayout.startAnimation(shakeAnimation);
            ImageView_profile.startAnimation(shakeAnimation);
            TastyToast.makeText(getApplicationContext(), "تاكد من جميع المعلومات",
                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);
        }
        else if (mImageUriid1 == null || mImageUriid2 == null)
        {
            id_layout.startAnimation(shakeAnimation);
            TastyToast.makeText(getApplicationContext(), "تاكد من رفع صورة البطاقه من الخلف و الامام",
                    TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }
        else {
            if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Workers");
                ref.orderByChild("phone").equalTo(phone.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            TastyToast.makeText(getApplicationContext(), "رقم الهاتف مٌسجل من قبل",
                                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        } else {
                            uploadIDs();
                            uploadFile();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else {

                TastyToast.makeText(getApplicationContext(), "تاكد من اتصالك بالانترنت",
                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);

            }
        }


    }


    private void showAlertDialog(int layout){

        dialogBuilder = new AlertDialog.Builder(this);
        final View layoutView = LayoutInflater.from(this).inflate(layout, null);
        final List<String> Sections = new ArrayList<String>();
        final ArrayAdapter<String> adapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, Sections);
        ListView list = layoutView.findViewById(R.id.list_dynamic);
        Button newSection = layoutView.findViewById(R.id._new);
        list.setAdapter(adapter);



        dialogBuilder.setView(layoutView);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();


        dialog.setMessage("جارى التحميل....");
        dialog.show();
        databaseReference.child("ChooseSection").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Sections.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String model =dataSnapshot1.child("section").getValue(String.class);
                    Sections.add(model);
                    adapter.notifyDataSetChanged();
                }

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

            }
        });


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String sec = Sections.get(position);
                section.setText(sec);
                alertDialog.dismiss();

            }
        });

        newSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogNewSection(R.layout.new_section);
                alertDialog.dismiss();

            }
        });




    }

    private void showAlertDialogNewSection(int layout) {
        dialogBuilder = new AlertDialog.Builder(this);
        final View layoutView = LayoutInflater.from(this).inflate(layout, null);

        Button newSection = layoutView.findViewById(R.id._savenew);
       final EditText Section = layoutView.findViewById(R.id._newsection);


        dialogBuilder.setView(layoutView);
        alertDialog_newSection = dialogBuilder.create();
        alertDialog_newSection.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog_newSection.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog_newSection.show();




        newSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (Section.getText().toString().equals(""))
               {
                   TastyToast.makeText(getApplicationContext(), "تاكد من القسم",
                           TastyToast.LENGTH_SHORT, TastyToast.ERROR);
               }
               else {
                   ChooseSection_Model chooseSection_model = new ChooseSection_Model(Section.getText().toString());
                   databaseReference.child("ChooseSection").push().setValue(chooseSection_model);
                   section.setText(Section.getText().toString());
                   alertDialog_newSection.dismiss();
               }

            }
        });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_enter, R.anim.right_out);
    }
}
