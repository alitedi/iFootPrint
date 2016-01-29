package com.example.alireza.empreints;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements LocationProvider.LocationCallback, View.OnClickListener {

    public static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private LocationProvider mLocationProvider;

    Dialog dialog;

    public EditText editText, editText2, editText3,forgetPassEmail;

    public Records record;

    public Button save_btn, cancel_btn,btn_share;

    DataBaseHelper2 helper2 = new DataBaseHelper2(this);

    String longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        record = (Records) getIntent().getSerializableExtra("Record");
        longitude = record.getLat();
        latitude = record.getLng();
        setUpMapIfNeeded();
        mLocationProvider = new LocationProvider(this, this);

        editText = (EditText) findViewById(R.id.loc_name);
        editText.setText(record.getRecordsName(), TextView.BufferType.EDITABLE);

        editText2 = (EditText) findViewById(R.id.loc_comment);
        editText2.setText(record.getCommnets(), TextView.BufferType.EDITABLE);

        editText3 = (EditText) findViewById(R.id.loc_ranking);
        editText3.setText(record.getRanking(), TextView.BufferType.EDITABLE);

        save_btn = (Button) findViewById(R.id.save_btn);
        save_btn.setOnClickListener(this);

        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(this);

        btn_share=(Button)findViewById(R.id.btn_share);
        btn_share.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mLocationProvider.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude2 = Double.parseDouble(latitude);
        double currentLongitude2 = Double.parseDouble(longitude);

        LatLng latLng = new LatLng(currentLongitude2, currentLatitude2);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("You were here!");
        mMap.addMarker(options);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_btn:
                //update the value on the table
                String str = editText.getText().toString();

                Records newRecord = new Records();
                newRecord.setContactUsername(record.getContactUsername());
                newRecord.setDate(record.getDate());
                newRecord.setLat(record.getLat());
                newRecord.setLng(record.getLng());


                newRecord.setRecordsName(editText.getText().toString());
                newRecord.setCommnets(editText2.getText().toString());
                newRecord.setRanking(editText3.getText().toString());

                //Toast tst4 = Toast.makeText(MapsActivity.this, editText.getText().toString(), Toast.LENGTH_SHORT);
                //tst4.show();

                int rs1 = helper2.update_record(record, newRecord, str);
                if (rs1 == 1) {
                    Toast tst = Toast.makeText(MapsActivity.this, "Saved changes! ", Toast.LENGTH_SHORT);
                    tst.show();
                    Intent it1 = new Intent(MapsActivity.this, TodayActivity.class);
                    it1.putExtra("Username", record.getContactUsername());
                    startActivity(it1);
                }else {
                    Toast tst2 = Toast.makeText(MapsActivity.this, "Saved Failed! ", Toast.LENGTH_SHORT);
                    tst2.show();
                }
                break;
            case R.id.cancel_btn:
                newRecord = record;
                newRecord.setRecordsName(editText.getText().toString());
                newRecord.setCommnets(editText2.getText().toString());
                newRecord.setRanking(editText3.getText().toString());
                newRecord.setContactUsername(record.getContactUsername());
                newRecord.setDate(record.getDate());
                int rs = helper2.delete_record(newRecord);
                if (rs == 1) {
                    Toast tst2 = Toast.makeText(MapsActivity.this, "Deleted correctly! ", Toast.LENGTH_SHORT);
                    tst2.show();
                    Intent it = new Intent(MapsActivity.this, TodayActivity.class);
                    it.putExtra("Username", record.getContactUsername());
                    startActivity(it);
                } else {
                    Toast tst2 = Toast.makeText(MapsActivity.this, "Deleted Failed! ", Toast.LENGTH_SHORT);
                    tst2.show();
                }
                break;
            case R.id.btn_share:
                //send mail
                dialog = new Dialog(this);
                dialog.setContentView(R.layout.forget_pass_layout);
                dialog.setTitle("Enter mail destination for sending your record");
                forgetPassEmail=(EditText)dialog.findViewById(R.id.editText3);
                Button save=(Button)dialog.findViewById(R.id.save);
                Button btnCancel=(Button)dialog.findViewById(R.id.cancel);
                dialog.show();
                save.setOnClickListener(this);
                btnCancel.setOnClickListener(this);
                break;
            case R.id.save:
                //sending back the password by sending mail
                String str2 =forgetPassEmail.getText().toString();
                String password2="Hey, I was "+record.getRecordsName()+ " at "+ record.getDate()
                        + "with this Longitude "+record.getLng()+" and latitude "+record.getLat();
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                new SendMail().execute(str2, password2);
                dialog.cancel();
                Toast.makeText(MapsActivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                break;
            case R.id.cancel:
                dialog.cancel();
                break;
            default:
                break;
        }
    }
}