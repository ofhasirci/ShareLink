package com.example.veled.sharelink;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by veled on 11.03.2018.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private ArrayList<String> groups;

    public GroupAdapter(ArrayList<String> groups) {this.groups = groups;}

    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.groupcardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupAdapter.ViewHolder holder, int position) {
        holder.groupName.setText(groups.get(position).split("_")[0]);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView groupName;
        private ImageView groupImage;

        public ViewHolder(View itemView) {
            super(itemView);

            groupName = itemView.findViewById(R.id.group_text);
            groupImage = itemView.findViewById(R.id.group_image);
        }
    }
}
