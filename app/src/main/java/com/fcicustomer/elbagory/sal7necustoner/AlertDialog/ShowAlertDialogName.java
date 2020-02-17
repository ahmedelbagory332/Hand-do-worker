package com.fcicustomer.elbagory.sal7necustoner.AlertDialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fcicustomer.elbagory.sal7necustoner.R;
import com.fcicustomer.elbagory.sal7necustoner.UserValidation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sdsmdg.tastytoast.TastyToast;


public  class ShowAlertDialogName {

    private static UserValidation userValidation;
     static  Context contex;



    public ShowAlertDialogName(Context contex) {
        this.contex = contex;
        userValidation = new UserValidation(contex);

    }

    public static void showAlertDialog(int layout , final String key , final TextView name_worker) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference();

        AlertDialog.Builder dialogBuilder;
        final AlertDialog alertDialog;

        LayoutInflater inflater = LayoutInflater.from(contex);
        final View layoutView = inflater.inflate(layout, null);
        final EditText newNumber = layoutView.findViewById(R.id._newnumber);
        final Button Ok = layoutView.findViewById(R.id._change);
        final ImageView imageView_intro = layoutView.findViewById(R.id.intro);
        final TextView textView_text_1 = layoutView.findViewById(R.id.textView_text1);
        final TextView textView_text_2 = layoutView.findViewById(R.id.textView_text2);

        imageView_intro.setImageResource(R.drawable.iconfinder_pencil);
        textView_text_1.setText("تغير رقم اسمك سيؤدى الى حذف الاسم القديم  ولن يظهر مرة اخرى");
        textView_text_2.setText("وسيتم حفظ جميع بياناتك السابقه مع الاسم الجديد");
        Ok.setText("تغير الاسم");
        newNumber.setHint("ادخل الاسم الجديد");
        newNumber.setInputType(InputType.TYPE_CLASS_TEXT);

        dialogBuilder = new AlertDialog.Builder(contex);
        dialogBuilder.setView(layoutView);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Phone = newNumber.getText().toString();

                if (Phone.equals("")) {
                    TastyToast.makeText(contex, "تأكد من اسمك",
                            TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    newNumber.setText("");
                } else {

                    databaseReference.child("Workers").child(key).child("name").setValue(newNumber.getText().toString());
                    TastyToast.makeText(contex, "تم تغير اسمك بنجاح",
                            TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                    userValidation.writename(newNumber.getText().toString());
                    name_worker.setText(userValidation.readphone());
                    alertDialog.dismiss();




                }
            }
        });
    }
    }
