package com.example.appintercambio.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.appintercambio.Models.Exchange;
import com.example.appintercambio.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ExchangeAdapter extends BaseAdapter
{
    Context context;
    List<Exchange> list = new ArrayList<>();
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    public ExchangeAdapter(Context context, List<Exchange> exchangeList){
        this.context = context;
        this.list = exchangeList;
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
        if (convertView ==  null){
            convertView = LayoutInflater.from(context).inflate(R.layout.demo_layout, null);
        }

        Exchange exchange = (Exchange) getItem(position);
        TextView textView = convertView.findViewById((R.id.txtDemo));
        textView.setText(exchange.toString());

        return convertView;
    }
}
