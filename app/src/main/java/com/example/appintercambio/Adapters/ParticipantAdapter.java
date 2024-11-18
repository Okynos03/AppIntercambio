package com.example.appintercambio.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.appintercambio.Models.Participant;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ParticipantAdapter extends BaseAdapter
{
    Context context;
    List<Participant> list = new ArrayList<>();
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    public ParticipantAdapter(Context context, List<Participant> participantList){
        this.context = context;
        this.list = participantList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
