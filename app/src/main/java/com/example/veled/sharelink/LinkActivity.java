package com.example.veled.sharelink;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by veled on 8.02.2018.
 */

public class LinkActivity extends AppCompatActivity {

    private static final String TAG = LinkActivity.class.getSimpleName();

    private final static int LIMIT = 10;
    private final static int SKIP = 0;

    private Socket socketClient;

    private RecyclerView recyclerView;
    private LinkAdapter adapter;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton fab;
    private ApiInterface apiService;

    private GroupObjectModel groupObjectModel;
    private ArrayList<LinkObjectModel> links;
    private String userName;
    private String token;
    private String group;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);

        Bundle extras = getIntent().getExtras();
        token = extras.getString("token");
        userName = extras.getString("email");
        group = extras.getString("groupName");

        initSocket();
        initView();

        apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<GroupObjectModel> call = apiService.getLinks(token, "0", "0", group);
        call.enqueue(new Callback<GroupObjectModel>() {
            @Override
            public void onResponse(Call<GroupObjectModel> call, Response<GroupObjectModel> response) {
                groupObjectModel = response.body();
                //Log.d(TAG, "Number of links: " + links.size());
                //Log.d(TAG, "link1: " + links.get(0).getUsername() + " " + links.get(0).getLink());
                if (groupObjectModel != null){
                    links = groupObjectModel.getLinks();
                    Collections.reverse(links);

                    adapter = new LinkAdapter(links);
                    recyclerView.setAdapter(adapter);
                }else {
                    Toast.makeText(getApplicationContext(), "Internal Server Error!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<GroupObjectModel> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });

    }

    private void initView() {
        setTitle(group.split("_")[0]);

        coordinatorLayout = findViewById(R.id.coor);
        fab = findViewById(R.id.fab);

        //RecyclerView adjustment
        recyclerView = findViewById(R.id.card_list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Snackbar snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_INDEFINITE);
                Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View snackView = inflater.inflate(R.layout.snackbar, null);

                Button sendButton = snackView.findViewById(R.id.send);
                final TextView snackLinkTv = snackView.findViewById(R.id.link_to_send);
                final TextView snackDescTv = snackView.findViewById(R.id.desc_to_send);

                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String link = snackLinkTv.getText().toString();
                        Log.d(TAG, "Link text: " + link);
                        //---------!!!!!!!!!!!!!!!!!!!!!!!!!!!1
                        if(link == "" || link == null){
                            Toast.makeText(getApplicationContext(), "Please, do not leave empty the link space", Toast.LENGTH_SHORT)
                                    .setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
                            return;
                        }

                        JSONObject object = new JSONObject();
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = new Date();
                        try {
                            object.put("groupName", group);
                            object.put("userName", userName);
                            object.put("link", snackLinkTv.getText().toString());
                            object.put("description", snackDescTv.getText().toString());
                            object.put("date", dateFormat.format(date).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        socketClient.emit("shareLink", object);

                        snackbar.dismiss();
                    }
                });

                layout.addView(snackView, 0);
                snackbar.show();

            }
        });

    }

    private void initSocket(){
        try{
            socketClient = IO.socket(getResources().getString(R.string.host_url));
            socketClient.on("shareLink", onNewMessage);
            socketClient.connect();
            socketClient.emit("joinGroup", group);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = (JSONObject) args[0];
                    try {
                        LinkObjectModel newEntry = new LinkObjectModel(
                                jsonObject.getString("user"),
                                jsonObject.getString("link"),
                                jsonObject.getString("description"),
                                jsonObject.getString("date")
                        );
                        links.add(0, newEntry);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, args[0].toString());
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketClient.emit("leaveGroup", group);
        socketClient.disconnect();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.link_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.addContact:
                addContactToGroup();
                break;
            case R.id.groupInfo:
                transactionToGroupInfo();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void transactionToGroupInfo() {
        Intent intent = new Intent(getApplicationContext(), GroupInfoActivity.class);
        Log.d(TAG, groupObjectModel.getMembers().get(0));
        intent.putStringArrayListExtra("members", groupObjectModel.getMembers());
        startActivity(intent);

    }

    private void addContactToGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog, null);
        builder.setView(dialogView);
        builder.setCancelable(true);

        TextView textView = dialogView.findViewById(R.id.alert_text);
        textView.setText(R.string.add_contact_text);

        final EditText editText = dialogView.findViewById(R.id.alert_email);
        editText.setHint(R.string.add_contact_hint);

        builder.setTitle(R.string.add_contact_title);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Call<LinkResponseModel> call = apiService.addContact(editText.getText().toString(), group);
                call.enqueue(new Callback<LinkResponseModel>() {
                    @Override
                    public void onResponse(Call<LinkResponseModel> call, Response<LinkResponseModel> response) {
                        LinkResponseModel receivedResponse = response.body();
                        if (receivedResponse.isAuth()){
                            Toast.makeText(getApplicationContext(), "The contact added succesfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), receivedResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LinkResponseModel> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Please check internet connection.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
    }
}
