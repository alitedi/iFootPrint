package com.example.alireza.empreints;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TodayActivity extends AppCompatActivity implements
        View.OnClickListener, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    static boolean active = true;

    private RecyclerView mRecyclerView;
    public static CustomAdapter mAdapter;
    private List<Records> currentRecords;
    protected LocationManager locationManager;
    protected Context context;

    TextView txtLat, edt, mp, tvAddress;
    String username, reportDate, currentAddress = null;
    String today;
    DataBaseHelper2 helper2 = new DataBaseHelper2(this);
    double currentLat = 0, currentLng = 0;

    Date selctedDate;
    private GoogleApiClient mGoogleApiClient;
    Location globalLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today2);

        //logic code --start
        active = true;
        ImageButton dateBtn = (ImageButton) findViewById(R.id.dateBTN);
        dateBtn.setOnClickListener(this);

        edt = (TextView) findViewById(R.id.dateText);
        mp = (TextView) findViewById(R.id.mapLocation);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        username = getIntent().getStringExtra("Username");

        setTitle("Date Analyser");

        Button sign_out_button = (Button) findViewById(R.id.sign_out_button);
        sign_out_button.setOnClickListener(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider, (long) currentLat, (float) currentLng, this);

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        today = df.format(Calendar.getInstance().getTime());
        selctedDate = Calendar.getInstance().getTime();
        reportDate = df.format(selctedDate);
        edt.setText(reportDate);

        //Google Settings
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //RecyclerView -- start
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // use a linear layout manager
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setVerticalScrollbarPosition(0);
        // specify an adapter (see also next example)
        if (currentRecords != null) currentRecords.clear();
        currentRecords = selectRecords();
        mAdapter = new CustomAdapter(currentRecords);
        mRecyclerView.setAdapter(mAdapter);
    }

    public List<Records> selectRecords() {
        //put to array records by username
        //List<Records> rc =new ArrayList<Records>(helper2.searchContact(username));
        List<Records> rc = new ArrayList<Records>(helper2.searchContactByDate(this.username, this.reportDate));
        return rc;
    }

    public void onLocationChanged(Location location) {
        globalLocation = location;
        //define a zone
        if (currentLat != location.getLatitude() && currentLng != location.getLongitude() && active) {
            currentLat = location.getLatitude();
            currentLng = location.getLongitude();
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(location.getLatitude(), location.getLongitude(),
                    getApplicationContext(), new GeocoderHandler());
        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dateBTN:
                //DatePickerDialog dialog = new DatePickerDialog()
                //dialog.show();
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                edt.setText(dayOfMonth + "-"
                                        + (monthOfYear + 1) + "-" + year);
                                selctedDate.setDate(dayOfMonth);
                                selctedDate.setMonth(monthOfYear);
                                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                                reportDate = df.format(selctedDate);
                                //swap data
                                currentRecords.clear();
                                currentRecords.addAll(selectRecords());
                                mAdapter.notifyDataSetChanged();
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
                break;

            case R.id.sign_out_button:
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                active = false;
                                Intent it = new Intent(TodayActivity.this, LoginActivity.class);
                                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(it);
                                finish();

                            }
                        });

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_today, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            tvAddress.setText(locationAddress);
            currentAddress = locationAddress;
            int w = helper2.returnCount(username);
            //maps location
            txtLat = (TextView) findViewById(R.id.mapLocation);
            txtLat.setText("Latitude:" + globalLocation.getLatitude() + ", Longitude:" + globalLocation.getLongitude());


            //check
            boolean addVerifier = true;
            if (currentAddress.contains("Unable"))
                addVerifier = false;

            locationAddress = locationAddress.replaceAll("'", "");
            //add new place
            if (helper2.existanceOfAnAddresse(username, today, locationAddress) == 0 && currentAddress != null && addVerifier && username!=null) {
                //add current location to records :
                Records r = new Records();
                r.setCommnets("no comment");
                r.setLat(String.valueOf(globalLocation.getLatitude()));
                r.setLng(String.valueOf(globalLocation.getLongitude()));
                r.setContactUsername(username);
                r.setRanking("5");
                currentAddress = currentAddress.replaceAll("'", "");
                r.setRecordsName(currentAddress);
                r.setDate(today);
                helper2.insertRecords(r);
                //refresh lis view
                mAdapter.add(r, mAdapter.getItemCount());
                mAdapter.notifyDataSetChanged();
               // Toast tst2=Toast.makeText(TodayActivity.this, "Add new place! ",Toast.LENGTH_SHORT);
               // tst2.show();
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //finish();
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            active = false;
            finish();
            super.onStop();
            Intent it = new Intent(TodayActivity.this, LoginActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(it);

        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        username = getIntent().getStringExtra("Username");
        super.onResume();
        //swap data
        currentRecords.clear();
        currentRecords.addAll(selectRecords());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        username = getIntent().getStringExtra("Username");
        super.onStart();
        //swap data
        currentRecords.clear();
        currentRecords.addAll(selectRecords());
        mAdapter.notifyDataSetChanged();
    }
}
