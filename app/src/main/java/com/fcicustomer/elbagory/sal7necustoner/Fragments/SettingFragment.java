package com.fcicustomer.elbagory.sal7necustoner.Fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fcicustomer.elbagory.sal7necustoner.AlertDialog.ShowAlertDialogName;
import com.fcicustomer.elbagory.sal7necustoner.Models.Worker;
import com.fcicustomer.elbagory.sal7necustoner.NetworkUtils.NetworkUtils;
import com.fcicustomer.elbagory.sal7necustoner.R;
import com.fcicustomer.elbagory.sal7necustoner.AlertDialog.ShowAlertDialogPhone;
import com.fcicustomer.elbagory.sal7necustoner.UserValidation;
import com.fcicustomer.elbagory.sal7necustoner.Verification;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
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
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    DatabaseReference databaseReference;
    DatabaseReference ref;

    private static UserValidation userValidation;
    private CircleImageView circleImageView;
    private TextView name_worker;
    private TextView phone_number;
    private ProgressDialog dialog;
  //  AlertDialog.Builder dialogBuilder;
   // AlertDialog alertDialog;
    private String KEY ="";



    public SettingFragment() {
        // Required empty public constructor
    }


    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_setting, container, false);

        Toolbar toolbar = v.findViewById(R.id.toolbar_setting);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.inflateMenu(R.menu.search);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("الاعدادات");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        userValidation = new UserValidation(getActivity());
        dialog = new ProgressDialog(getActivity());
        name_worker = v.findViewById(R.id.name_settiong);
         phone_number = v.findViewById(R.id.worker_sec_setting);
        circleImageView = v.findViewById(R.id.profile_image_worker_setting);

        ref= FirebaseDatabase.getInstance().getReference().child("Workers");







        ListView listView = v.findViewById(R.id.pro_list_setting);
        ArrayList<Lists> itme = new ArrayList<>();
        itme.add(new  Lists("حدث التطبيق", R.drawable.update));
        itme.add(new  Lists("تغير اسمك", R.drawable.iconfinder_pencil));
        itme.add(new  Lists("تغير رقم الهاتف", R.drawable.ic_iconfinder_mobile));
        itme.add(new Lists("حذف الحساب", R.drawable.delete));
        Adapter adapter = new Adapter(itme);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0:
                        if (NetworkUtils.isNetworkAvailable(getActivity())) {

                            try {
                                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(i);
                            } catch (ActivityNotFoundException e) {
                                Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
                                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(i);
                            }
                        }
                        else
                        {
                            TastyToast.makeText(getActivity(), "تاكد من اتصالك بالانترنت",
                                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        }
                        break;
                    case 1:
                        if (NetworkUtils.isNetworkAvailable(getActivity())) {

                            ref.orderByChild("phone").equalTo(userValidation.readphone())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {

                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                                    KEY = dataSnapshot1.getKey();

                                                }

                                                new ShowAlertDialogName(getActivity()).showAlertDialog(R.layout.change_number, KEY, name_worker);

                                                //  showAlertDialog(R.layout.change_number,KEY,1);
                                                GetInfo();



                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                        else
                        {
                            TastyToast.makeText(getActivity(), "تاكد من اتصالك بالانترنت",
                                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        }
                        break;
                    case 2:
                        if (NetworkUtils.isNetworkAvailable(getActivity())) {

                            ref.orderByChild("phone").equalTo(userValidation.readphone())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {

                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                                    KEY = dataSnapshot1.getKey();

                                                }
                                                new ShowAlertDialogPhone(getActivity()).showAlertDialog(R.layout.change_number, KEY, phone_number);


                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                        else
                        {
                            TastyToast.makeText(getActivity(), "تاكد من اتصالك بالانترنت",
                                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        }
                        break;
                    case 3:
                        if (NetworkUtils.isNetworkAvailable(getActivity())) {

                            ref.orderByChild("phone").equalTo(userValidation.readphone()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {

                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                            KEY = dataSnapshot1.getKey();

                                        }

                                        databaseReference.child("Workers").child(KEY).child("id").removeValue();
                                        databaseReference.child("Workers").child(KEY).child("ischeked").removeValue();
                                        databaseReference.child("Workers").child(KEY).child("name").removeValue();
                                        databaseReference.child("Workers").child(KEY).child("phone").removeValue();
                                        databaseReference.child("Workers").child(KEY).child("rate").removeValue();
                                        databaseReference.child("Workers").child(KEY).child("section").removeValue();

                                        userValidation.writephone("");
                                        userValidation.writeLoginStatus(false);
                                        TastyToast.makeText(getActivity(), "تم حذف الحساب بنجاح",
                                                TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                        getActivity().finish();


                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else
                        {
                            TastyToast.makeText(getActivity(), "تاكد من اتصالك بالانترنت",
                                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        }
                        break;
                }
            }
        });

               GetInfo();

        return v;
    }



    class Lists{

        String option ;
        int img;

        public Lists(String option, int img) {
            this.option = option;
            this.img = img;
        }
    }

    class Adapter extends BaseAdapter {

        ArrayList<Lists> itme = new ArrayList<>();

        public Adapter(ArrayList<Lists> itme) {
            this.itme = itme;
        }

        @Override
        public int getCount() {
            return itme.size();
        }

        @Override
        public Object getItem(int position) {
            return itme.get(position).option;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {

            LayoutInflater layoutInflater = getLayoutInflater();
            View view1 = layoutInflater.inflate(R.layout.row_item_worker_profile,null);

            TextView textView = view1.findViewById(R.id.option);
            ImageView imageView = view1.findViewById(R.id.profile_image_option);


            textView.setText(itme.get(i).option);
            imageView.setImageResource(itme.get(i).img);


            return view1;

        }
    }


    private void GetInfo(){
        Query query = databaseReference.child("Workers").orderByChild("phone").equalTo(userValidation.readphone());
        dialog.setMessage("جارى تحميل بياناتك....");
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (isAdded()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        Worker workers_models = dataSnapshot1.getValue(Worker.class);

                        if (workers_models != null) {


                            name_worker.setText(workers_models.getName());
                            phone_number.setText(workers_models.getPhone());


                            Glide.with(getActivity())
                                    .asBitmap()
                                    .load(workers_models.getImg())
                                    .fitCenter()
                                    .placeholder(R.drawable.account)
                                    .into(circleImageView);
                            name_worker.setText(userValidation.readname());

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

    }


    }
