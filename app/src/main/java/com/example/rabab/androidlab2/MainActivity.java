package com.example.rabab.androidlab2;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private RemindersSimpleCursorAdapter mAdapter;
    private List<Reminder> notesList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;

    private RemindersDbAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //logo
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        coordinatorLayout =(CoordinatorLayout) findViewById(R.id.coordinator_layout);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        db = new RemindersDbAdapter(this);
        notesList.addAll(db.fetchAllReminders());

        mAdapter = new RemindersSimpleCursorAdapter(this, notesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);



        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                showActionsDialog(position);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }


    //insert new note
    private void createNote(String note, int imp) {
               int id = (int) db.createReminder(note, imp);
        Reminder n = db.fetchReminderById((int) id);
        if (n != null) {
            notesList.add(0, n);
            mAdapter.notifyDataSetChanged();
         }
    }

    // update note text
    private void updateNote(String note, int imp, int position) {
        Reminder n = notesList.get(position);
        n.setContent(note);
        n.setImportant(imp);
        db.updateReminder(n);
        notesList.set(position, n);
        mAdapter.notifyItemChanged(position);

     }

    // delete note
    private void deleteNote(int position) {
        db.deleteNote(notesList.get(position));
        notesList.remove(position);
        mAdapter.notifyItemRemoved(position);

     }

     //open options to edit or delete
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

         builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, notesList.get(position), position);
                } else {
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }

    //pop up
    private void showNoteDialog(final boolean shouldUpdate, final Reminder note, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkBox);
//        checkBox.setChecked(note.getImp()!=0);


        final EditText inputNote = (EditText) view.findViewById(R.id.note);
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? "New Reminder" :  "Edit Reminder");
        if (shouldUpdate && note != null) {
            inputNote.setText(note.getContent());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "Commit" : "Commit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if user updating note
                if (shouldUpdate && note != null) {
                    // update note by it's id
//                    updateNote(inputNote.getText().toString(), checkBox.isChecked()?1:0, position);
                    updateNote(inputNote.getText().toString(),1, position);
                    alertDialog.cancel();
                 } else {
                    // create new note
//                    createNote(inputNote.getText().toString(),  checkBox.isChecked()?1:0);
                    createNote(inputNote.getText().toString(), 1);
                    alertDialog.cancel();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main , menu);
        return true;
    }
    @Override
    //new reminder
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.newReminder) {
             showNoteDialog(false, null, -1);

        }
        //exit app
        else if (item.getItemId() == R.id.exit) {
            finish();
            System.exit(0);
        }
        return true;
    }
}