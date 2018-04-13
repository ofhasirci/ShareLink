package com.example.veled.sharelink;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by veled on 15.03.2018.
 */

public class GroupInfoActivity extends AppCompatActivity {

    private String TAG = "TAG";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_info);

        ListView memberList = findViewById(R.id.membersList);
        final TextView title = findViewById(R.id.memberCountTitle);
        TextView exit = findViewById(R.id.exit);

        Bundle extras = getIntent().getExtras();
        ArrayList<String> membersNames = extras.getStringArrayList("members");
        Log.d(TAG, membersNames.get(0));
        CustomAdapter adapter = new CustomAdapter(getApplicationContext(), membersNames);
        memberList.setAdapter(adapter);

        title.setText(membersNames.size() + " Members");
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "This is not exit.");
            }
        });
    }

    class CustomAdapter extends BaseAdapter{

        ArrayList<String> members = new ArrayList<>();
        Context context;


        public CustomAdapter(Context context, ArrayList<String> members){
            super();
            this.members = members;
            this.context = context;
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (view == null){
                view = inflater.inflate(R.layout.group_info_card, viewGroup, false);
                holder = new ViewHolder();
                holder.textView =  view.findViewById(R.id.userName);
                holder.imageView = view.findViewById(R.id.userImage);
                view.setTag(holder);
            }else {
                holder = (ViewHolder) view.getTag();
            }
            Log.d("TAGGG", members.get(i));
            holder.textView.setText(members.get(i));

            return view;
        }

        class ViewHolder{
            TextView textView;
            ImageView imageView;
        }
    }


}
