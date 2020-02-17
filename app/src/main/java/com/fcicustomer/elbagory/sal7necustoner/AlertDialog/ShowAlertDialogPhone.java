package com.fcicustomer.elbagory.sal7necustoner.AlertDialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;


public  class ShowAlertDialogPhone {

    private static UserValidation userValidation;
     static  Context contex;



    public ShowAlertDialogPhone(Context contex) {
        this.contex = contex;
        userValidation = new UserValidation(contex);

    }

    public static void showAlertDialog(int layout , final String key , final TextView phone_number) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference();

        AlertDialog.Builder dialogBuilder;
        final AlertDialog alertDialog;




        LayoutInflater inflater = LayoutInflater.from(contex);
        final View layoutView = inflater.inflate(layout, null);
        final EditText newNumber = layoutView.findViewById(R.id._newnumber);
        final Button Ok = layoutView.findViewById(R.id._change);



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

                if (Phone.equals("") || Phone.length() < 11) {
                    TastyToast.makeText(contex, "تأكد من رقم الهاتف",
                            TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    newNumber.setText("");
                }
                else {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Workers");
                    ref.orderByChild("phone").equalTo(Phone).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                TastyToast.makeText(contex, "رقم الهاتف مٌسجل من قبل",
                                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            }

                            else
                            {
                                databaseReference.child("Workers").child(key).child("phone").setValue(newNumber.getText().toString());
                                TastyToast.makeText(contex, "تم تغير رقم الهاتف بنجاح",
                                        TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                userValidation.writephone(newNumber.getText().toString());
                                phone_number.setText(userValidation.readphone());
                                alertDialog.dismiss();


                            }


                            }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }
        });
    }
    }
