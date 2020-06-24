package com.hntechs.attendanceassistant;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.hntechs.attendanceassistant.data.AttendanceAssistantDbHelper;
import com.hntechs.attendanceassistant.data.AttendanceAssistantContract.AttendanceEntry;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AttendanceCatalog extends AppCompatActivity {

    private AttendanceAssistantDbHelper mDbHelper;
    private SQLiteDatabase db;
    private ListView catalogListView;
    private TextView title,noAttendanceUpdated;
    private FloatingActionButton fab;
    private View.OnClickListener daily,batch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_catalog);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        catalogListView=findViewById(R.id.catalogListView);
        noAttendanceUpdated=findViewById(R.id.noAttendanceUpdated);
        title=findViewById(R.id.titleTV);
        mDbHelper=new AttendanceAssistantDbHelper(this);
        db=mDbHelper.getWritableDatabase();
        displayDailyAttendance();

        fab = findViewById(R.id.fab);
        daily=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent catalogToUpdate = new Intent(AttendanceCatalog.this,UpdateAttendance.class);
                startActivity(catalogToUpdate);
            }
        };

        batch=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent catalogToBatch = new Intent(AttendanceCatalog.this,BatchUpdate.class);
                startActivity(catalogToBatch);
            }
        };
        fab.setOnClickListener(daily);
        noAttendanceUpdated.setOnClickListener(daily);
    }

    @Override
    protected void onStart() {
        super.onStart();

        catalogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,final View view, int i, long l) {
                final AlertDialog.Builder options = new AlertDialog.Builder(AttendanceCatalog.this);
                LayoutInflater inflater = LayoutInflater.from(AttendanceCatalog.this);
                View myView = inflater.inflate(R.layout.options_dialog_layout,null);
                options.setView(myView);
                final AlertDialog optionsAlert = options.create();
                optionsAlert.show();
                final TextView update = (TextView) myView.findViewById(R.id.edit);
                final TextView delete = (TextView) myView.findViewById(R.id.delete);
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if((title.getText().toString()).equals("Daily Attendance")){
                            TextView clickedDateTV = (TextView) view.findViewById(R.id.dDateLV);
                            final TextView clickedPresentTV = (TextView) view.findViewById(R.id.dPresentLV);
                            final TextView clickedTotalTV = (TextView) view.findViewById(R.id.dTotalLV);
                            final String date = clickedDateTV.getText().toString();
                            DateFormat inputFormat = new SimpleDateFormat("EEEE, dd MMM, yy");
                            DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date dateVar=null;
                            try {
                                dateVar = inputFormat.parse(date);
                            } catch (ParseException e) {
                                Log.i("MMMM","ParseEX") ;
                            }
                            final String dbDate=outputFormat.format(dateVar);

                            deleteEntry(dbDate);
                            Intent dialogToUpdate = new Intent(AttendanceCatalog.this,UpdateAttendance.class);
                            dialogToUpdate.putExtra("DATE",dbDate);
                            dialogToUpdate.putExtra("PRESENT",Integer.parseInt(clickedPresentTV.getText().toString()));
                            dialogToUpdate.putExtra("TOTAL",Integer.parseInt(clickedTotalTV.getText().toString()));
                            startActivity(dialogToUpdate);
                            optionsAlert.cancel();
                        }else{
                            TextView clickedFrom = (TextView) view.findViewById(R.id.fromDateLV);
                            TextView clickedTo = (TextView) view.findViewById(R.id.toDateLV);
                            final TextView clickedPresent = (TextView) view.findViewById(R.id.bPresentLV);
                            final TextView clickedTotal = (TextView) view.findViewById(R.id.bTotalLV);

                            DateFormat inputFormat = new SimpleDateFormat("dd MMM, yy");
                            final DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

                            Date from=null,to=null;
                            try{
                                from = inputFormat.parse(clickedFrom.getText().toString());
                                to = inputFormat.parse(clickedTo.getText().toString());
                            }catch (ParseException e){

                            }
                            final String fromDate=outputFormat.format(from),
                                    toDate=outputFormat.format(to);
                            deleteBatchEntry(fromDate);
                            Intent dialogToBatch = new Intent(AttendanceCatalog.this,BatchUpdate.class);
                            dialogToBatch.putExtra("FROM_DATE",fromDate);
                            dialogToBatch.putExtra("TO_DATE",toDate);
                            dialogToBatch.putExtra("PRESENT",Integer.parseInt(clickedPresent.getText().toString()));
                            dialogToBatch.putExtra("TOTAL",Integer.parseInt(clickedTotal.getText().toString()));
                            startActivity(dialogToBatch);
                            optionsAlert.cancel();
                        }
                    }
                });


                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if((title.getText().toString()).equals("Daily Attendance")){
                            TextView clickedDateTV = (TextView) view.findViewById(R.id.dDateLV);
                            final TextView clickedPresentTV = (TextView) view.findViewById(R.id.dPresentLV);
                            final TextView clickedTotalTV = (TextView) view.findViewById(R.id.dTotalLV);
                            final String date = clickedDateTV.getText().toString();
                            DateFormat inputFormat = new SimpleDateFormat("EEEE, dd MMM, yy");
                            DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date dateVar=null;
                            try {
                                dateVar = inputFormat.parse(date);
                            } catch (ParseException e) {
                                Log.i("MMMM","ParseEX") ;
                            }
                            final String dbDate=outputFormat.format(dateVar);

                            optionsAlert.cancel();
                            AlertDialog.Builder deleteWarning = new AlertDialog.Builder(AttendanceCatalog.this);
                            deleteWarning.setTitle("Delete this attendance?")
                                    .setMessage("Are you sure, you want to delete attendance of: "+date)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if(deleteEntry(dbDate)==1)
                                                Toast.makeText(AttendanceCatalog.this, "Deleted!", Toast.LENGTH_SHORT).show();
                                            else
                                                Toast.makeText(AttendanceCatalog.this,"Unable to Delete",Toast.LENGTH_LONG).show();

                                            displayDailyAttendance();
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .setNegativeButton("No",null).show();
                        }else {
                            TextView clickedFrom = (TextView) view.findViewById(R.id.fromDateLV);
                            TextView clickedTo = (TextView) view.findViewById(R.id.toDateLV);
                            final TextView clickedPresent = (TextView) view.findViewById(R.id.bPresentLV);
                            final TextView clickedTotal = (TextView) view.findViewById(R.id.bTotalLV);

                            DateFormat inputFormat = new SimpleDateFormat("dd MMM, yy");
                            final DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

                            Date from=null,to=null;
                            try{
                                from = inputFormat.parse(clickedFrom.getText().toString());
                                to = inputFormat.parse(clickedTo.getText().toString());
                            }catch (ParseException e){

                            }
                            final String fromDate=outputFormat.format(from),
                                    toDate=outputFormat.format(to);

                            optionsAlert.cancel();
                            AlertDialog.Builder deleteWarning = new AlertDialog.Builder(AttendanceCatalog.this);
                            deleteWarning.setTitle("Delete this attendance?")
                                    .setMessage("Are you sure, you want to delete batch attendance: From "+fromDate+" to "+toDate+" ?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if(deleteBatchEntry(fromDate)==1)
                                                Toast.makeText(AttendanceCatalog.this,"Deleted!",Toast.LENGTH_SHORT).show();
                                            else
                                                Toast.makeText(AttendanceCatalog.this,"Unable to Delete",Toast.LENGTH_SHORT).show();

                                            displayBatchAttendance();
                                        }
                                    }).setNegativeButton("No",null)
                                    .show();

                        }
                    }
                });
            }
        });

        //refreshAttendanceCatalog();

        /*batchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                TextView clickedFrom = (TextView) view.findViewById(R.id.fromDateLV);
                TextView clickedTo = (TextView) view.findViewById(R.id.toDateLV);
                final TextView clickedPresent = (TextView) view.findViewById(R.id.bPresentLV);
                final TextView clickedTotal = (TextView) view.findViewById(R.id.bTotalLV);

                DateFormat inputFormat = new SimpleDateFormat("dd MMM, yy");
                final DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

                Date from=null,to=null;
                try{
                    from = inputFormat.parse(clickedFrom.getText().toString());
                    to = inputFormat.parse(clickedTo.getText().toString());
                }catch (ParseException e){

                }
                final String fromDate=outputFormat.format(from),
                        toDate=outputFormat.format(to);
                final AlertDialog.Builder options = new AlertDialog.Builder(AttendanceCatalog.this);
                LayoutInflater inflater = LayoutInflater.from(AttendanceCatalog.this);
                View myView = inflater.inflate(R.layout.options_dialog_layout,null);
                options.setView(myView);
                options.setNegativeButton("cancel",null);
                final AlertDialog optionsAlert = options.create();
                final TextView update = (TextView) myView.findViewById(R.id.update);
                final TextView delete = (TextView) myView.findViewById(R.id.delete);
                 update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteBatchEntry(fromDate);
                        Intent dialogToBatch = new Intent(AttendanceCatalog.this,BatchUpdate.class);
                        dialogToBatch.putExtra("FROM_DATE",fromDate);
                        dialogToBatch.putExtra("TO_DATE",toDate);
                        dialogToBatch.putExtra("PRESENT",Integer.parseInt(clickedPresent.getText().toString()));
                        dialogToBatch.putExtra("TOTAL",Integer.parseInt(clickedTotal.getText().toString()));
                        startActivity(dialogToBatch);
                        optionsAlert.cancel();
                    }
                });
                 delete.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         optionsAlert.cancel();
                        AlertDialog.Builder deleteWarning = new AlertDialog.Builder(AttendanceCatalog.this);
                        deleteWarning.setTitle("Delete this attendance?")
                                .setMessage("Are you sure, you want to delete batch attendance: From "+fromDate+" to "+toDate+" ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(deleteBatchEntry(fromDate)==1)
                                            Toast.makeText(AttendanceCatalog.this,"Deleted!",Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(AttendanceCatalog.this,"Unable to Delete",Toast.LENGTH_SHORT).show();

                                        //refreshAttendanceCatalog();
                                    }
                                }).setNegativeButton("No",null)
                                .show();
                     }
                 });
                optionsAlert.show();
            }
        });

        dailyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                TextView clickedDateTV = (TextView) view.findViewById(R.id.dDateLV);
                final TextView clickedPresentTV = (TextView) view.findViewById(R.id.dPresentLV);
                final TextView clickedTotalTV = (TextView) view.findViewById(R.id.dTotalLV);
                final String date = clickedDateTV.getText().toString();
                DateFormat inputFormat = new SimpleDateFormat("EEEE, dd MMM, yy");
                DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date dateVar=null;
                try {
                    dateVar = inputFormat.parse(date);
                } catch (ParseException e) {
                    Log.i("MMMM","ParseEX") ;
                }
                final String dbDate=outputFormat.format(dateVar);

                final AlertDialog.Builder options = new AlertDialog.Builder(AttendanceCatalog.this);
                LayoutInflater inflater = LayoutInflater.from(AttendanceCatalog.this);
                View myView = inflater.inflate(R.layout.options_dialog_layout,null);
                options.setView(myView);
                options.setNegativeButton("Cancel",null);

                final TextView update = (TextView) myView.findViewById(R.id.update);
                final TextView delete = (TextView) myView.findViewById(R.id.delete);

                final AlertDialog optionsAlert = options.create();

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteEntry(dbDate);
                        Intent dialogToUpdate = new Intent(AttendanceCatalog.this,UpdateAttendance.class);
                        dialogToUpdate.putExtra("DATE",dbDate);
                        dialogToUpdate.putExtra("PRESENT",Integer.parseInt(clickedPresentTV.getText().toString()));
                        dialogToUpdate.putExtra("TOTAL",Integer.parseInt(clickedTotalTV.getText().toString()));
                        startActivity(dialogToUpdate);
                        optionsAlert.cancel();
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        optionsAlert.cancel();
                        AlertDialog.Builder deleteWarning = new AlertDialog.Builder(AttendanceCatalog.this);
                        deleteWarning.setTitle("Delete this attendance?")
                                .setMessage("Are you sure, you want to delete attendance of: "+date)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(deleteEntry(dbDate)==1)
                                            Toast.makeText(AttendanceCatalog.this, "Deleted!", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(AttendanceCatalog.this,"Unable to Delete",Toast.LENGTH_LONG).show();

                                        //refreshAttendanceCatalog();
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton("No",null).show();
                    }
                });
                optionsAlert.show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_attendance_catalog,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_viewDailyAttendance:
                title.setText(R.string.daily_attendance);
                noAttendanceUpdated.setVisibility(View.INVISIBLE);
                displayDailyAttendance();
                fab.setOnClickListener(daily);
                return true;
            case R.id.menu_viewBatchAttendance:
                title.setText(R.string.batch_attendance);
                noAttendanceUpdated.setVisibility(View.INVISIBLE);
                displayBatchAttendance();
                fab.setOnClickListener(batch);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayDailyAttendance(){

        String orderBy = AttendanceEntry.COLUMN_DATE+" DESC";
        Cursor dailyAttendanceCursor = db.query(AttendanceEntry.TABLE_DAILY_ATTENDANCE,
                null,null,null,null,null,orderBy);

        if(dailyAttendanceCursor.getCount()==0){
            catalogListView.setAdapter(null);
            noAttendanceUpdated.setVisibility(View.VISIBLE);
            noAttendanceUpdated.setOnClickListener(daily);
        }
        else {
            noAttendanceUpdated.setVisibility(View.INVISIBLE);
            DailyAttendanceCursorAdapter dailyAttendanceCursorAdapter = new DailyAttendanceCursorAdapter(this, dailyAttendanceCursor);
            catalogListView.setAdapter(dailyAttendanceCursorAdapter);
        }
    }

    private void displayBatchAttendance(){
        Cursor batchAttendanceCursor = db.query(AttendanceEntry.TABLE_BATCH_ATTENDANCE,
                null,null,null,null,null,null);

        if(batchAttendanceCursor.getCount()==0){
            catalogListView.setAdapter(null);
            noAttendanceUpdated.setVisibility(View.VISIBLE);
            noAttendanceUpdated.setOnClickListener(batch);
        }
        else {

            BatchAttendanceCursorAdapter batchAttendanceCursorAdapter = new BatchAttendanceCursorAdapter(this, batchAttendanceCursor);
            catalogListView.setAdapter(batchAttendanceCursorAdapter);
        }
    }

    private long deleteEntry(String date){
        String whereClause = AttendanceEntry.COLUMN_DATE+"=?";
        String[] whereArgs = new String[]{date};
        return db.delete(AttendanceEntry.TABLE_DAILY_ATTENDANCE,whereClause,whereArgs);
    }

    private long deleteBatchEntry(String date){
        String whereClause = AttendanceEntry.COLUMN_FROM_DATE+"=?";
        String[] whereArgs = new String[]{date};
        return db.delete(AttendanceEntry.TABLE_BATCH_ATTENDANCE,whereClause,whereArgs);
    }
}
