package com.example.veled.sharelink;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by veled on 27.02.2018.
 */

public class GroupActivity extends AppCompatActivity {
    private static final String TAG = GroupActivity.class.getSimpleName();

    private GroupAdapter adapter;
    private RecyclerView recyclerView;
    private ApiInterface apiService;

    private String userName;
    private String token;
    private ArrayList<String> groups;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Bundle extras = getIntent().getExtras();
        token = extras.getString("token");
        userName = extras.getString("email");
        groups = extras.getStringArrayList("groups");

        Log.d(TAG, "username: " + userName);
        apiService = ApiClient.getClient().create(ApiInterface.class);

        initSharedPreferences();
        initView();

        adapter = new GroupAdapter(groups);
        recyclerView.setAdapter(adapter);

    }

    private void initView() {
        recyclerView = findViewById(R.id.group_list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if(child != null){
                    int position = rv.getChildLayoutPosition(child);
                    Intent intent = new Intent(getApplicationContext(), LinkActivity.class);
                    intent.putExtra("groupName", groups.get(position));
                    intent.putExtra("email", userName);
                    intent.putExtra("token", token);
                    startActivity(intent);
                }
                return true;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private void initSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.createGroup:
                creatingAGroup();
                break;
            case R.id.logout:
                logOut();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;

    }

    private void logOut() {
        editor.putString("token", null);
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void creatingAGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog, null);
        builder.setView(dialogView);
        builder.setCancelable(true);

        TextView textView = dialogView.findViewById(R.id.alert_text);
        textView.setText(R.string.group_alert_text);

        final EditText editText = dialogView.findViewById(R.id.alert_email);
        editText.setHint(R.string.group_alert_hint);

        builder.setTitle(R.string.group_alert_title);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Call<LinkResponseModel> call = apiService.createGroup(editText.getText().toString(), userName, dateFormat.format(new Date()).toString());
                call.enqueue(new Callback<LinkResponseModel>() {
                    @Override
                    public void onResponse(Call<LinkResponseModel> call, Response<LinkResponseModel> response) {
                        LinkResponseModel receivedResponse = response.body();
                        if (receivedResponse.isAuth()){
                            groups.add(editText.getText().toString());
                            adapter.notifyDataSetChanged();
                        }else {
                            Toast.makeText(getApplicationContext(), receivedResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LinkResponseModel> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Please check internet connection", Toast.LENGTH_SHORT).show();
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

}
