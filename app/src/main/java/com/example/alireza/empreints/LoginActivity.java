package com.example.alireza.empreints;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    public String nameA;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";
    EditText etUsername,etPassword,forgetPassEmail;
    Button loginBtn, signUp ;
    ImageButton forgetBtn;
    Dialog dialog;
    DataBaseHelper helper=new DataBaseHelper(this);
    private TextView info;
    CallbackManager callbackManager;
    LoginButton loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //FragmentActivity    faActivity  = (FragmentActivity)super.LoginActivity;
        super.onCreate(savedInstanceState);


        //fb
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        loginButton = (LoginButton)findViewById(R.id.fb_login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("email");
        loginButton.setReadPermissions("user_birthday");
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                //String name = "";
                                try{
                                    //name = object.getString("name");
                                    nameA=object.getString("name");
                                    //Toast.makeText(LoginActivity.this, "onSuccess"+name, Toast.LENGTH_LONG).show();
                                    //Toast.makeText(LoginActivity.this, "onSuccess"+nameA, Toast.LENGTH_LONG).show();
                                    Contacts c=new Contacts();
                                    c.setEmail(nameA);
                                    c.setName(nameA);
                                    c.setPassword(nameA);
                                    c.setUsername(nameA);
                                    helper.insertContact(c);
                                    Intent i = new Intent(LoginActivity.this, TodayActivity.class);
                                    i.putExtra("Username",nameA);
                                    startActivity(i);
                                }
                                catch (JSONException exe){ }
                                //Toast.makeText(LoginActivity.this, "onSuccess"+name, Toast.LENGTH_LONG).show();
                                Log.v("LoginActivity", response.toString());
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
                //parameters.toString();




            }

            @Override
            public void onCancel() {
               // Toast.makeText(LoginActivity.this, "onCancel", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
              //  Toast.makeText(LoginActivity.this, "onError" + e.toString(), Toast.LENGTH_LONG).show();
            }
        });
        //end of fb






        setContentView(R.layout.activity_login);
        etUsername = (EditText)findViewById(R.id.UsenameTxt);
        etPassword = (EditText)findViewById(R.id.PasswordTxt);
        loginBtn=(Button)findViewById(R.id.LoginBtn);
        loginBtn.setOnClickListener(this);
        signUp=(Button)findViewById(R.id.HaveAcountBtn);
        signUp.setOnClickListener(this);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        forgetBtn=(ImageButton)findViewById(R.id.ForgetBtn);
        forgetBtn.setOnClickListener(this);
        SignInButton googleSignInBtn = (SignInButton) findViewById(R.id.sign_in_button);
        //SIGN IN BY GOOGLE ACCOUNT
        googleSignInBtn.setStyle(SignInButton.SIZE_STANDARD, SignInButton.COLOR_AUTO);
        setGooglePlusButtonText(googleSignInBtn, "Sign in with Google");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this/* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    protected void setGooglePlusButtonText(SignInButton signInButton,
                                           String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTextSize(15);
                tv.setTypeface(null, Typeface.NORMAL);
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.LoginBtn:
                final String str=etUsername.getText().toString();
                //check password username
                String pass=etPassword.getText().toString();
                String password=helper.searchPass(str);
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
                if(pass.equals(password)) {
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    // On complete call either onLoginSuccess or onLoginFailed
                                    Intent i = new Intent(LoginActivity.this, TodayActivity.class);
                                    i.putExtra("Username", str);
                                    startActivity(i);
                                    // onLoginFailed();
                                    progressDialog.dismiss();
                                }
                            }, 2000);
                }
                else {
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    Toast tst = Toast.makeText(LoginActivity.this, "Make sure that you entered your username and password correctly!", Toast.LENGTH_LONG);
                                    tst.show();
                                    progressDialog.dismiss();
                                }
                            }, 1000);
                }
                break;

            case R.id.HaveAcountBtn:
                Intent ii = new Intent(LoginActivity.this, SignUp.class);
                startActivity(ii);
                break;

            case R.id.sign_in_button:
                signIn();
                break;

            case R.id.ForgetBtn:
                dialog = new Dialog(this);
                dialog.setContentView(R.layout.forget_pass_layout);
                dialog.setTitle("Enter your dialog for sending password ");
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
                String password2="Hello, here is your password for your iFootPrint application: "+ helper.passForget(str2);
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                new SendMail().execute(str2, password2);
                dialog.cancel();
                Toast.makeText(LoginActivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                break;

            case R.id.cancel:
                dialog.cancel();
                break;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //verify existance of contacts
            if(helper.existanceOfContacts(acct.getEmail())==0) {
                //add the contact
                Contacts c=new Contacts();
                c.setEmail(acct.getEmail());
                c.setName(acct.getDisplayName());
                c.setPassword(acct.hashCode() + "");
                c.setUsername(acct.getEmail().replace("@gmail.com", ""));
                helper.insertContact(c);
            }
            //add the contact to DB
            //go to connecxion page
            Intent i = new Intent(LoginActivity.this, TodayActivity.class);
            i.putExtra("Username",acct.getEmail().replace("@gmail.com", ""));
            startActivity(i);


        } else {
            //Toast tst=Toast.makeText(LoginActivity.this, "handleSignInResult  cancel", Toast.LENGTH_LONG);
            //tst.show();
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
    // [END signIn]

    int w=0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        //finish();
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if(w==1)
            {
                //finish();
                Toast tst=Toast.makeText(LoginActivity.this, "2nd press", Toast.LENGTH_LONG);
                tst.show();
                System.exit(0);
                return true;
            }
            else{
                w++;
                Toast tst=Toast.makeText(LoginActivity.this, "press another back to exit", Toast.LENGTH_LONG);
                tst.show();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }


}

