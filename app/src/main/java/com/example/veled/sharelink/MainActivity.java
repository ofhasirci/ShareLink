package com.example.veled.sharelink;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText email;
    private EditText password;
    private Button button;
    private RadioGroup radioGroup;

    private SharedPreferences sharedPreferences;
    private String token;
    private ApiInterface apiService;

    private final static int MY_PERMISSIONS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        initSharedPreferences();

        initViews();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, MY_PERMISSIONS_REQUEST_CODE);
            }
        }

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        //Log.e(TAG, "permission: " + permissionCheck);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED){
            //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, MY_PERMISSIONS_REQUEST_CODE);
        }


    }

    private void initSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = sharedPreferences.getString("token", null);
        Log.d(TAG, "token: " + token);
        if (token != null){
            Log.d(TAG, "token: " + token);
            Call<LinkResponseModel> call = apiService.getProfile(token);
            call.enqueue(new Callback<LinkResponseModel>() {
                @Override
                public void onResponse(Call<LinkResponseModel> call, Response<LinkResponseModel> response) {
                    LinkResponseModel receivedResponse = response.body();
                    Log.d(TAG, "token: " + token);
                    Log.d(TAG, "auth: " + response);
                    Log.d(TAG, "username: " + receivedResponse.getEmail());
                    if (receivedResponse.isAuth()){
                        Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
                        intent.putExtra("email", receivedResponse.getEmail());
                        intent.putExtra("token", token);
                        intent.putExtra("groups", receivedResponse.getGroups());
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(), receivedResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LinkResponseModel> call, Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "Please log/sign in the system.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        button = findViewById(R.id.login);
        radioGroup = findViewById(R.id.selection);
    }

    public void loginSystem(View view){
        Log.d(TAG, "Infos: " + email.getText().toString() + ", " + password.getText().toString());

        int selectedId = radioGroup.getCheckedRadioButtonId();
        Call<LinkResponseModel> call;
        if (selectedId == R.id.radioLogin){
            call = apiService.login(email.getText().toString(), password.getText().toString());
        }else {
            call = apiService.signin(email.getText().toString(), password.getText().toString());
        }

        call.enqueue(new Callback<LinkResponseModel>() {
            @Override
            public void onResponse(Call<LinkResponseModel> call, Response<LinkResponseModel> response) {
                try {
                    LinkResponseModel receivedResponse = response.body();
                    //Log.d(TAG, "response: "+ response.errorBody().string());
                    if (receivedResponse.isAuth()){
                        Intent intent = new Intent(getApplicationContext(), GroupActivity.class);
                        intent.putExtra("token", receivedResponse.getToken());
                        intent.putExtra("email", receivedResponse.getEmail());
                        intent.putExtra("groups", receivedResponse.getGroups());
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), receivedResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (NullPointerException e){
                    Log.e(TAG, "NullPointer" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<LinkResponseModel> call, Throwable t) {
                Log.e(TAG, t.toString());
                Toast.makeText(getApplicationContext(), "Request has failed. Check the connection.", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void resetPassword(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog, null);
        builder.setView(dialogView);
        builder.setCancelable(true);

        final EditText editText = dialogView.findViewById(R.id.alert_email);

        builder.setTitle("Reset Password");
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Call<LinkResponseModel> call = apiService.resetpass(editText.getText().toString());
                call.enqueue(new Callback<LinkResponseModel>() {
                    @Override
                    public void onResponse(Call<LinkResponseModel> call, Response<LinkResponseModel> response) {
                        LinkResponseModel receivedResponse = response.body();
                        Toast.makeText(getApplicationContext(), receivedResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<LinkResponseModel> call, Throwable t) {
                        Log.e(TAG, t.toString());
                        Toast.makeText(getApplicationContext(), "Request has failed. Check the connection.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Permission denied");
                }
                return;
            }
        }
    }
}
