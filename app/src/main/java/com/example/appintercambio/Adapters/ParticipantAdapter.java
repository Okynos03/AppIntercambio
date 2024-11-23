package com.example.appintercambio.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.appintercambio.Models.Participant;
import com.example.appintercambio.R;
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
        ViewHolder holder;

        // Si la vista no existe, inicializamos el ViewHolder y sus vistas
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_participant, parent, false);
            holder = new ViewHolder();
            holder.textViewNombre = convertView.findViewById(R.id.textViewNombre);
            holder.textViewCorreo = convertView.findViewById(R.id.textViewCorreo);
            convertView.setTag(holder);
        } else {
            // Reutilizamos el ViewHolder si la vista ya existe
            holder = (ViewHolder) convertView.getTag();
        }

        // Asignamos los datos del participante a las vistas
        Participant participant = list.get(position);
        holder.textViewNombre.setText(participant.getName());
        holder.textViewCorreo.setText(participant.getEmail());

        return convertView;
    }

    // ViewHolder para optimizar el rendimiento evitando llamadas repetidas a findViewById
    private static class ViewHolder {
        TextView textViewNombre;
        TextView textViewCorreo;
    }

    // Actualiza la lista de participantes y notifica los cambios, no se sabe si funcione
    public void updateList(List<Participant> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }
}
