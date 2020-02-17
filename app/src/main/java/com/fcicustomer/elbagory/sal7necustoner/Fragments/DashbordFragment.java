package com.fcicustomer.elbagory.sal7necustoner.Fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.fcicustomer.elbagory.sal7necustoner.Models.Worker;
import com.fcicustomer.elbagory.sal7necustoner.NetworkUtils.NetworkUtils;
import com.fcicustomer.elbagory.sal7necustoner.R;
import com.fcicustomer.elbagory.sal7necustoner.UserValidation;
import com.fcicustomer.elbagory.sal7necustoner.Verification;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashbordFragment extends Fragment   {

    private static final String TAG = DashbordFragment.class.getSimpleName();

    private static UserValidation userValidation;
    DatabaseReference databaseReference;
    private CircleImageView circleImageView;
    private TextView name;
    private TextView phone;
    private ProgressDialog dialog;
    /////
    LocationManager locationManager;
    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 50000000;

    private static final int REQUEST_CHECK_SETTINGS = 100;

    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;


    private String userPhone="";

    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;

    double user_litit =0;
    double user_langitit =0;
    double your_litit =0;
    double your_langtit =0;

    View v;

    public DashbordFragment() {
        // Required empty public constructor
    }


    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         v = inflater.inflate(R.layout.fragment_dashbord, container, false);

        Toolbar toolbar = v.findViewById(R.id.toolbar_dash);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.inflateMenu(R.menu.search);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("الصفحة الرئيسية");


        setHasOptionsMenu(true);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        userValidation = new UserValidation(getActivity());
        dialog = new ProgressDialog(getActivity());
       final TextView name = v.findViewById(R.id.workerprofile_name);
       final  TextView phone = v.findViewById(R.id.worker_sec);
        circleImageView = v.findViewById(R.id.profile_image_worker);





        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mSettingsClient = LocationServices.getSettingsClient(getActivity());

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();


              //  updateLocationUI();
            }
        };
        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        ListView listView = v.findViewById(R.id.pro_list);
        ArrayList<Lists> itme = new ArrayList<>();
        itme.add(new Lists("تحديد مكان العمل", R.drawable.jpsprofile));
        itme.add(new Lists("الدعم", R.drawable.help));
        itme.add(new Lists("الخروج", R.drawable.exit));
        Adapter adapter = new Adapter(itme);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0 :
                        if (NetworkUtils.isNetworkAvailable(getActivity())) {


                            if (userPhone.equals("")) {
                                TastyToast.makeText(getActivity(), "من فضلك تاكد من رقم العميل",
                                        TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            } else {
                                TastyToast.makeText(getActivity(), "جارى تحديد موقع العميل",
                                        TastyToast.LENGTH_SHORT, TastyToast.INFO);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            startLocationUpdates();
                                            Thread.sleep(2000);
                                            // updateLocationUI();

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                            }
                        }
                        else
                        {
                            TastyToast.makeText(getActivity(), "تاكد من اتصالك بالانترنت",
                                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        }


                        break;
                    case 1:

                        makeCall("123456789");

                        break;
                    case 2:

                        startActivity(new Intent(getActivity(),Verification.class));
                        userValidation.writeLoginStatus(false);
                        getActivity().finish();

                        break;

                }
            }
        });
//to load worker info
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


                            name.setText(workers_models.getName());
                            phone.setText(workers_models.getPhone());


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


        if (userValidation.ReadTapTargetSequence()) {


            TapTargetView.showFor(getActivity(),
                    TapTarget.forToolbarMenuItem(toolbar, R.id.user_search, "ايقونة تحديد الموقع", "لمعرفه مكان العميل عن طريق رقم الهاتف")
                            .outerCircleColor(R.color.colorPrimaryDark)      // Specify a color for the outer circle
                            .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                            .targetCircleColor(R.color.colorwhite)   // Specify a color for the target circle
                            .titleTextSize(30)                  // Specify the size (in sp) of the title text
                            .titleTextColor(R.color.colorPrimaryDark)      // Specify the color of the title text
                            .descriptionTextSize(20)            // Specify the size (in sp) of the description text
                            .descriptionTextColor(R.color.colorwhite)  // Specify the color of the description text
                            .textColor(R.color.colorwhite)            // Specify a color for both the title and description text
                            .textTypeface(Typeface.MONOSPACE)  // Specify a typeface for the text
                            .drawShadow(true)
                            .cancelable(false),
                    new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);      // This call is optional
                            view.dismiss(true);
                        }
                    });
            userValidation.writeTapTargetSequence(false);

        }

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

    private void makeCall(String number){

        Intent call = new Intent(Intent.ACTION_CALL);
        call.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(call);

    }
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        // Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(2000);
                                        updateLocationUI();

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();


                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        TastyToast.makeText(getActivity(), "تأكد من اعدادات تحديد الموقع",
                                TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }
    private void updateLocationUI() {

        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            if (mCurrentLocation != null) {


                getActivity().runOnUiThread(new Runnable() {
                    public void run() {

//                        Toast.makeText(getActivity(), "Lat: " + mCurrentLocation.getLatitude() + ", " +
//                                "Lng: " + mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                        your_litit = mCurrentLocation.getLatitude();
                        your_langtit = mCurrentLocation.getLongitude();


                        Query query = databaseReference.child("Users").orderByChild("phone").equalTo(userPhone);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                    user_litit =dataSnapshot1.child("litit").getValue(double.class);
                                    user_langitit =dataSnapshot1.child("langtit").getValue(double.class);

                                    String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + your_litit+
                                            "," + your_langtit + "&daddr="+ user_litit +","+ user_langitit;

                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    startActivity(intent.createChooser(intent, "اختر تطبيق"));
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });





                    }
                });







                // giving a blink animation on TextView


            }
            else
            {
                String uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + your_litit+
                        "," + your_langtit + "&daddr="+ user_litit +","+ user_langitit;

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent.createChooser(intent, "اختر تطبيق"));
            }


        }
        else {
            TastyToast.makeText(getActivity(), "تاكد من اتصالك بالانترنت",
                    TastyToast.LENGTH_SHORT, TastyToast.ERROR);


        }
    }


    public void showLastKnownLocation() {
        if (mCurrentLocation != null) {
            // Toast.makeText(getApplicationContext(), "Lat: " + mCurrentLocation.getLatitude()
            //     + ", Lng: " + mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();


            //  databaseReference.child("Users").child(KEY).child("langtit").setValue(mCurrentLocation.getLongitude());
            //  databaseReference.child("Users").child(KEY).child("litit").setValue(mCurrentLocation.getLatitude());

            //  TastyToast.makeText(getApplicationContext(), "تم تحديد موقعك بنجاح يمكنك الان ان تغلق GPS",
            //          TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);



        } else {
            //  TastyToast.makeText(getApplicationContext(), "حدث خطأ ما",
            //   TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            // Toast.makeText(getApplicationContext(), "Last known location is not available!", Toast.LENGTH_SHORT).show();

        }
    }
    private void showAlertDialog(int layout){

        dialogBuilder = new AlertDialog.Builder(getActivity());
        final View layoutView = LayoutInflater.from(getActivity()).inflate(layout, null);
        Button newSection = layoutView.findViewById(R.id._savenew);
        final EditText Section = layoutView.findViewById(R.id._newsection);



        dialogBuilder.setView(layoutView);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();


        newSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userPhone = Section.getText().toString();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                ref.orderByChild("phone").equalTo(userPhone).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            TastyToast.makeText(getActivity(), "تم تحديد موقع العميل \n الرجاء الضغط على تحديد مكان العمل لفتح الخريطه",
                                    TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                            alertDialog.dismiss();
                        }
                        else
                        {
                            TastyToast.makeText(getActivity(), "تاكد من رقم العميل",
                                    TastyToast.LENGTH_LONG, TastyToast.ERROR);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });






            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {



                case R.id.user_search:

                    if (NetworkUtils.isNetworkAvailable(getActivity())) {
                    // Do Fragment menu item stuff here
                    showAlertDialog(R.layout.userphone);
                        return true;
                    }
                    else
                    {
                        TastyToast.makeText(getActivity(), "تاكد من اتصالك بالانترنت",
                                TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                    }





            default:
                break;
        }

        return false;
    }

}
