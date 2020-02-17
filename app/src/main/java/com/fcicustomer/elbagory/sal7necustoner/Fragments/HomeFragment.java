package com.fcicustomer.elbagory.sal7necustoner.Fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fcicustomer.elbagory.sal7necustoner.Models.Worker;
import com.fcicustomer.elbagory.sal7necustoner.NetworkUtils.NetworkUtils;
import com.fcicustomer.elbagory.sal7necustoner.R;
import com.fcicustomer.elbagory.sal7necustoner.UserValidation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    DatabaseReference databaseReference ;
    UserValidation userValidation;
    private ProgressDialog dialog;
    private static long NumberOfRecomendations = 0;
    private static String KEY = "";
    TextView info;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
            userValidation = new  UserValidation(getActivity());
        dialog = new ProgressDialog(getActivity());

        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            TastyToast.makeText(getActivity(), "تاكد من اتصالك بالانترنت",
                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            dialog.dismiss();

        }

        Toolbar toolbar = v.findViewById(R.id.toolbar_home);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.inflateMenu(R.menu.search);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("الصفحة الشخصيه");


        Button reservation = v.findViewById(R.id._reservation);
        Button follow = v.findViewById(R.id._follow);
        final CircleImageView circleImageView = v.findViewById(R.id.workser_image);
          info = v.findViewById(R.id.information);
        reservation.setEnabled(false);
        follow.setEnabled(false);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        Query query = databaseReference.child("Workers").orderByChild("phone").equalTo(userValidation.readphone());
        dialog.setMessage("جارى تحميل بياناتك....");
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                    if (getActivity() != null)
                    {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            KEY = dataSnapshot1.getKey();
                            NumberOfRecomendations = dataSnapshot1.child("rate").getValue(Long.class);
                            Worker workers_models = dataSnapshot1.getValue(Worker.class);

                            if (workers_models != null) {

                                if (workers_models.getRate() == 0) {
                                    info.setText(workers_models.getName() + "\n" + "من قسم " + workers_models.getSection() +
                                            "\n" + "لم يتم التوصيه من قبل أحد");
                                } else if (workers_models.getRate() == 1) {
                                    info.setText(workers_models.getName() + "\n" + "من قسم " + workers_models.getSection() +
                                            "\n" + "تم التوصيه من قبل شخص");
                                } else if (workers_models.getRate() == 2) {
                                    info.setText(workers_models.getName() + "\n" + "من قسم " + workers_models.getSection() +
                                            "\n" + "تم التوصيه من قبل شخصان");
                                } else if (workers_models.getRate() == 3 || workers_models.getRate() == 4 || workers_models.getRate() == 5
                                        || workers_models.getRate() == 6 || workers_models.getRate() == 7 || workers_models.getRate() == 8
                                        || workers_models.getRate() == 9 || workers_models.getRate() == 10) {
                                    info.setText(workers_models.getName() + "\n" + "من قسم " + workers_models.getSection() +
                                            "\n" +
                                            "تم التوصيه من قبل " + workers_models.getRate() + " أشخاص");
                                } else {
                                    info.setText(workers_models.getName() + "\n" + "من قسم " + workers_models.getSection() +
                                            "\n" +
                                            "تم التوصيه من قبل " + workers_models.getRate() + " شخص");
                                }

                                Glide.with(getActivity())
                                        .asBitmap()
                                        .load(workers_models.getImg())
                                        .fitCenter()
                                        .placeholder(R.drawable.account)
                                        .into(circleImageView);

                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }

                            } else
                                TastyToast.makeText(getActivity(), "تأكد من اتصالك بالانترنت",
                                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                TastyToast.makeText(getActivity(), "تأكد من اتصالك بالانترنت",
                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });


        return v;
    }

}
