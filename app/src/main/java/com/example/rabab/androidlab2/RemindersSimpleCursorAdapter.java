package com.example.rabab.androidlab2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RemindersSimpleCursorAdapter extends RecyclerView.Adapter<RemindersSimpleCursorAdapter.MyViewHolder> {

    private Context context;
    private List<Reminder> notesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView note;

        public MyViewHolder(View view) {
            super(view);
            note = (TextView) view.findViewById(R.id.note);
         }
    }


    public RemindersSimpleCursorAdapter(Context context, List<Reminder> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Reminder note = notesList.get(position);

        holder.note.setText(note.getContent());

    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

}
