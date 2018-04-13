package com.example.veled.sharelink;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by veled on 9.02.2018.
 */

public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.ViewHolder> {

    private ArrayList<LinkObjectModel> linkObjects;

    public LinkAdapter(ArrayList<LinkObjectModel> linkObjects){
        this.linkObjects = linkObjects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            holder.userName.setText(linkObjects.get(position).getUsername().split("@")[0]);
            holder.date.setText(linkObjects.get(position).getDate());
            holder.link.setText(linkObjects.get(position).getLink());
            holder.description.setText(linkObjects.get(position).getDescription());
        } catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return linkObjects.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;
        private TextView date;
        private TextView link;
        private TextView description;

        public ViewHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_text);
            date = itemView.findViewById(R.id.date_text);
            link = itemView.findViewById(R.id.link_text);
            description = itemView.findViewById(R.id.desc_text);
        }
    }

}
