package com.hntechs.attendanceassistant;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.hntechs.attendanceassistant.data.AttendanceAssistantContract.AttendanceEntry;
import com.hntechs.attendanceassistant.data.AttendanceAssistantDbHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class BatchUpdate extends AppCompatActivity {

    private EditText fromDateTV, toDateTV, presenttTV, totallTV, extraInfooTV;
    private Calendar calendar;
    private int year,month,day;
    private DatePickerDialog fromDatePickerDialog,toDatePickerDialog;
    private Vibrator v;
    private AttendanceAssistantDbHelper mDbHelper;
    private SQLiteDatabase db;
    private String fromDateSet, toDateSet, fromDateTvText, toDateTvText,temp;
    private Menu m;
    private DateFormat inputformat,outputformat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_update);
        fromDateTV = (EditText) findViewById(R.id.fromDateTV);
        toDateTV = (EditText) findViewById(R.id.toDateTV);
        presenttTV = (EditText) findViewById(R.id.presenttTV);
        totallTV = (EditText) findViewById(R.id.totallTV);
        extraInfooTV = (EditText) findViewById(R.id.extraInfooTV);

        calendar=Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        day=calendar.get(Calendar.DAY_OF_MONTH);
        createDatePickerDialogs();
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mDbHelper = new AttendanceAssistantDbHelper(this);
        db = mDbHelper.getWritableDatabase();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("MMMM","o");
        inputformat = new SimpleDateFormat("yyyy-MM-dd");
        outputformat = new SimpleDateFormat("EEEE, MMM dd, yyyy");


        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            fromDateSet=extras.getString("FROM_DATE");
            toDateSet = extras.getString("TO_DATE");

            Date from=null,to=null;
            try{
                from=inputformat.parse(fromDateSet);
                to=inputformat.parse(toDateSet);
            }catch (ParseException e){

            }
            fromDateTV.setText(outputformat.format(from));
            toDateTV.setText(outputformat.format(to));
            presenttTV.setText(Integer.toString(extras.getInt("PRESENT")));
            totallTV.setText(Integer.toString(extras.getInt("TOTAL")));
        }
        fromDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDatePickerDialog.show();
            }
        });

        toDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDatePickerDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_update_attendance, menu);
        m=menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                int presentClasses,totalClasses,cmp = fromDateSet.compareTo(toDateSet);
                long rowId;
                if (TextUtils.isEmpty(fromDateSet) || TextUtils.isEmpty(toDateSet)) {
                    Toast.makeText(BatchUpdate.this, "Date is missing!", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        v.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                    else
                        v.vibrate(150);
                }
                try {
                    presentClasses = Integer.parseInt(presenttTV.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(BatchUpdate.this, "Classes Present is missing!", Toast.LENGTH_SHORT).show();
                    presentClasses = 9999;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        v.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                    else
                        v.vibrate(150);
                }

                try {
                    totalClasses = Integer.parseInt(totallTV.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(BatchUpdate.this, "Total Classes is missing!", Toast.LENGTH_SHORT).show();
                    totalClasses = 9999;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        v.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                    else
                        v.vibrate(150);
                }
                String extraInfo = extraInfooTV.getText().toString();

                //Show dialog TODO: add icon to AlertDialog
                if (totalClasses < presentClasses) {
                    AlertDialog.Builder incorrectAlert = new AlertDialog.Builder(BatchUpdate.this);
                    incorrectAlert.setMessage("Present Classes cannot be more than Total Classes.")
                            .setTitle("Incorrect Details!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // TODO:Do SOMETHING!
                                }
                            }).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    else
                        v.vibrate(500);
                }
                else if(cmp>=0){
                    String message = "From Date is ahed of To Date. Please enter respective dates correctly.";
                    if(cmp==0)
                        message="From and To dates are same, To update in batch mode dates should be different.";

                    AlertDialog.Builder fromDateAhedAlert = new AlertDialog.Builder(BatchUpdate.this);
                    fromDateAhedAlert.setMessage(message)
                            .setTitle("Incorrect Dates!")
                            .setPositiveButton("Ok",null).show();
                }
                else if (totalClasses != 9999 && presentClasses != 9999 && !TextUtils.isEmpty(fromDateSet) && !TextUtils.isEmpty(toDateSet) && cmp<0){
                    Log.i("MMMM","All set");
                    rowId = insertIntoBatchAttendance(fromDateSet,toDateSet,presentClasses,totalClasses,extraInfo);
                    if(rowId<0){
                       String title,msg,date="bb";

                        /*if(rowId==-999) {
                            //If a single or many dates between entered batch already has an attendance updated.
                            try {
                                date = outputformat.format(inputformat.parse(temp));
                            } catch (Exception e) {
                                Log.i("MMMMM","-999");
                                e.printStackTrace();
                            }
                            title = "Attendance Duplication!";
                            msg = "You cannot add this as batch attendance."+"Attendance for "+date+" already exists!";
                        }
                        else {*/
                            title = "Batch Already Exists!";
                            msg = "You've already added batch attendance from dates: " + fromDateTvText + " to " + toDateTvText + ". Do you want to overwrite it?";
                        //}
                        AlertDialog.Builder overWriteAlert = new AlertDialog.Builder(this);

                        overWriteAlert.setTitle(title)
                                .setMessage(msg)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String whereClause = AttendanceEntry.COLUMN_FROM_DATE+"=?";
                                        String[] whereArgs = new String[]{fromDateSet};
                                        db.delete(AttendanceEntry.TABLE_BATCH_ATTENDANCE,whereClause,whereArgs);

                                        onOptionsItemSelected(m.findItem(R.id.done));
                                    }
                                });
                    }
                    Toast.makeText(BatchUpdate.this, "Batch Attendance Updated!", Toast.LENGTH_SHORT).show();
                    NavUtils.navigateUpFromSameTask(BatchUpdate.this);
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createDatePickerDialogs() {

        DatePickerDialog.OnDateSetListener from_dateSetListener, to_dateSetListener;

        from_dateSetListener = new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                String dateTVText;
                if (selectedDay == day && selectedMonth == month && selectedYear == year) {
                    dateTVText = "Today";
                    fromDateTV.setText(dateTVText);
                } else {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
                    String dayOfWeek = simpleDateFormat.format(calendar.getTime());
                    simpleDateFormat = new SimpleDateFormat("MMM");
                    String monthName = simpleDateFormat.format(calendar.getTime());
                    dateTVText = monthName + " " + Integer.toString(selectedDay) + ", " + Integer.toString(selectedYear);
                    fromDateTV.setText(dayOfWeek + ", " + dateTVText);
                }
                selectedMonth += 1;
                String month = Integer.toString(selectedMonth), day = Integer.toString(selectedDay);
                if (selectedMonth < 10)
                    month = "0" + Integer.toString(selectedMonth);
                if (selectedDay < 10)
                    day = "0" + Integer.toString(selectedDay);
                fromDateSet = Integer.toString(selectedYear) + "-" + month + "-" + day;
                fromDateTvText = dateTVText;
            }
        };

        to_dateSetListener=new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                String dateTVText;
                if (selectedDay == day && selectedMonth == month && selectedYear == year) {
                    dateTVText = "Today";
                    toDateTV.setText(dateTVText);
                } else {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
                    String dayOfWeek = simpleDateFormat.format(calendar.getTime());
                    simpleDateFormat = new SimpleDateFormat("MMM");
                    String monthName = simpleDateFormat.format(calendar.getTime());
                    dateTVText = monthName + " " + Integer.toString(selectedDay) + ", " + Integer.toString(selectedYear);
                    toDateTV.setText(dayOfWeek + ", " + dateTVText);
                }
                selectedMonth += 1;
                String month = Integer.toString(selectedMonth), day = Integer.toString(selectedDay);
                if (selectedMonth < 10)
                    month = "0" + Integer.toString(selectedMonth);
                if (selectedDay < 10)
                    day = "0" + Integer.toString(selectedDay);
                toDateSet = Integer.toString(selectedYear) + "-" + month + "-" + day;
                toDateTvText = dateTVText;
            }
        };

        fromDatePickerDialog = new DatePickerDialog(this,from_dateSetListener,year,month,day);
        toDatePickerDialog = new DatePickerDialog(this,to_dateSetListener,year,month,day);

        fromDatePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        toDatePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
    }

    private long insertIntoBatchAttendance(String fromDate, String toDate, int present, int total, String extraInfo){
        ContentValues cv = new ContentValues();
        cv.put(AttendanceEntry.COLUMN_FROM_DATE,fromDate);
        cv.put(AttendanceEntry.COLUMN_TO_DATE,toDate);
        cv.put(AttendanceEntry.COLUMN_CLASSESPRESENT,present);
        cv.put(AttendanceEntry.COLUMN_TOTAL_CLASSES,total);
        if(!TextUtils.isEmpty(extraInfo)){
            cv.put(AttendanceEntry.COLUMN_EXTRAINFO,extraInfo);
            return db.insert(AttendanceEntry.TABLE_BATCH_ATTENDANCE,null,cv);
        }
        else
            return db.insert(AttendanceEntry.TABLE_BATCH_ATTENDANCE,AttendanceEntry.COLUMN_EXTRAINFO,cv);
    }
}