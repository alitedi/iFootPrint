package com.example.alireza.empreints;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WelcomePage extends AppCompatActivity implements View.OnClickListener {

    DataBaseHelper2 helper2=new DataBaseHelper2(this);
    String username="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        username=getIntent().getStringExtra("Username");
        TextView tv=(TextView)findViewById(R.id.TVusername);
        tv.setText(username);



        Button btn=(Button)findViewById(R.id.MapsBtn);
        btn.setOnClickListener(this);
        Button dateBtn0=(Button)findViewById(R.id.dateBtn0);
        dateBtn0.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.MapsBtn){
            Intent i = new Intent(WelcomePage.this, MapsActivity.class);
            startActivity(i);
        }
        else if(v.getId()==R.id.dateBtn0){
            Intent i = new Intent(WelcomePage.this, TodayActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome_page, menu);
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


}
