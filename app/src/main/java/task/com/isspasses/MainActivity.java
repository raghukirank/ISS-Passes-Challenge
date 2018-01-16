package task.com.isspasses;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener ,LocationListener{
    String latitude="0",longitude="0";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Button button;
    private ProgressBar progressBar;
    TextView count;
    LinearLayout bottomlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Passess");
        button= (Button) findViewById(R.id.get_passes);
        bottomlayout= (LinearLayout) findViewById(R.id.bottomlayout);
        count= (TextView) findViewById(R.id.count);
        progressBar= (ProgressBar) findViewById(R.id.progresssbar);
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        creteLocationRequest();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
              /*  requiredPermissions();*/
                if (isConnectingToInternet(MainActivity.this)) {
                    if (checkplayservices()) {
                        progressBar.setVisibility(View.VISIBLE);
                        final Timer t1 = new Timer();
                        TimerTask task;
                        t1.schedule(task = new TimerTask() {
                            @Override
                            public void run() {

                                new Handler(Looper.getMainLooper()).post(
                                        new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!latitude.equals("0")) {
                                            t1.cancel();
                                            OkHttpClient client = new OkHttpClient.Builder().build();
                                            Retrofit retrofit = new Retrofit.Builder()
                                                    .baseUrl("http://api.open-notify.org/")
                                                    .addConverterFactory(GsonConverterFactory.create())
                                                    .client(client)
                                                    .build();
                                            GetPasses getPasses = retrofit.create(GetPasses.class);
                                            Call<Example> respose = getPasses.getjson("iss-pass.json?lat=" + latitude + "&lon=" + longitude);
                                            respose.enqueue(new Callback<Example>() {
                                                @Override
                                                public void onResponse(Call<Example> call, retrofit2.Response<Example> response) {
                                                    progressBar.setVisibility(View.GONE);
                                                    if (response.body() != null) {
                                                        if (response.body().getMessage().equals("success")) {
                                                            bottomlayout.removeAllViews();
                                                            Log.e("req", call.request().toString());
                                                            count.setText(String.valueOf(response.body().getRequest().getPasses()));
                                                            for (int i = 0; i < response.body().getResponse().size(); i++) {
                                                                View view1 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_layout, null, false);
                                                                TextView duration = (TextView) view1.findViewById(R.id.duration);
                                                                TextView raise_time = (TextView) view1.findViewById(R.id.raise_time);
                                                                duration.setText(String.valueOf(response.body().getResponse().get(i).getDuration()));
                                                                raise_time.setText(dateconvertet(String.valueOf(response.body().getResponse().get(i).getRisetime())));
                                                                Log.e("res", String.valueOf(response.body().getResponse().get(i).getRisetime()));
                                                                bottomlayout.addView(view1);
                                                            }
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Example> call, Throwable t) {
                                                    Toast.makeText(MainActivity.this, "" + t, Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        }, 0, 1000);
                    }else {
                        Toast.makeText(MainActivity.this, "Device does't support play services", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this, "No internet access", Toast.LENGTH_SHORT).show();
                }
            }
        });
       }
/*
    private void requiredPermissions() {
        try {
                PermissionUtils.checkPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION, new PermissionUtils.PermissionAskListener() {
                @Override
                public void onNeedPermission() {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
                }
                @Override
                public void onPermissionPreviouslyDenied() {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("Location Permission")
                            .setMessage("permission is required to proceed")
                            .setIcon(R.drawable.location)
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
                                }
                            }).show();
                }

                @Override
                public void onPermissionDisabled() {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("Location Permission")
                            .setMessage("permission is required to proceed enable in settings")
                            .setIcon(R.drawable.location)
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                                }
                            }).show();
                }

                @Override
                public void onPermissionGranted() throws InterruptedException {
                    getLocation();
                }


            });
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
*/
public static boolean isConnectingToInternet(Context context) {
    ConnectivityManager manager= (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
    if (manager!=null){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Network[] network = manager.getAllNetworks();
            NetworkInfo info;
            if (network.length >= 0) {
                for (Network mntework : network) {
                    info = manager.getNetworkInfo(mntework);
                    if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                        return true;
                    }
                }
            }
        }else {
            NetworkInfo[] networkInfos = manager.getAllNetworkInfo();
            if (networkInfos != null) {
                for (NetworkInfo info : networkInfos) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
    }
    return false;
}

    protected void creteLocationRequest(){
        mLocationRequest=new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setSmallestDisplacement(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> resultPendingResult=LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,builder.build());
        resultPendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status=locationSettingsResult.getStatus();
                final LocationSettingsStates state=locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()){
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MainActivity.this,1000);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }


            }
        });
    }
    private boolean checkplayservices(){
        int resultcode= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultcode==ConnectionResult.API_UNAVAILABLE){
            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultcode)){
                GoogleApiAvailability.getInstance().showErrorDialogFragment(this,resultcode,1000);
            }
            return false;
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MainActivity.this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
      /*  Toast.makeText(getApplicationContext(),""+location.getLatitude()+""+location.getLongitude(),Toast.LENGTH_LONG).show();*/
        longitude= String.valueOf(location.getLongitude());
        latitude= String.valueOf(location.getLatitude());
    }

    @Override
    protected void onStop() {
        super.onStop();
        // stop listening for location
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1000:
                switch (resultCode){
                    case Activity.RESULT_OK:
                        Toast.makeText(getApplicationContext(),"Gps turned on",Toast.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // do something here
                        Toast.makeText(getApplicationContext(),"No internet Access",Toast.LENGTH_LONG).show();
                        break;
                }
        }
    }
    public static String dateconvertet(String value){
        long val = Long.parseLong(value);
        Date date=new Date(val);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return df2.format(date);
    }
}
