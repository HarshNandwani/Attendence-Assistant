package com.hntechs.attendanceassistant;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.hntechs.attendanceassistant.data.AttendanceAssistantContract.AttendanceEntry;
import com.hntechs.attendanceassistant.data.AttendanceAssistantDbHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UpdateAttendance extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private int year,month,day;
    private EditText dateTV,presentTV,totalTV,extraInfoTV;
    private String dateSet,dateTvText;
    private Vibrator v;
    private AttendanceAssistantDbHelper mDbHelper;
    private SQLiteDatabase db;
    private Menu m;
    private String updateIntentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_attendance);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateTV = (EditText) findViewById(R.id.dateTV);
        presentTV = (EditText) findViewById(R.id.presentTV);
        totalTV = (EditText) findViewById(R.id.totalTV);
        extraInfoTV=(EditText) findViewById(R.id.extraInfoTV);
        /*DatePickerDialog needs to start at date. So, getting todays date
            from calender and passing it to DatePickerDialog's constructor
        */
        calendar=Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH);
        day=calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog= new DatePickerDialog(this,this,year,month,day);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mDbHelper = new AttendanceAssistantDbHelper(this);
        db = mDbHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            dateSet=extras.getString("DATE");
            DateFormat inputformat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat outputformat = new SimpleDateFormat("EEEE, MMM dd, yyyy");
            Date dateVar=null;
            try{
                dateVar=inputformat.parse(dateSet);
            }catch (ParseException e){

            }
            dateTV.setText(outputformat.format(dateVar));
            presentTV.setText(Integer.toString(extras.getInt("PRESENT")));
            totalTV.setText(Integer.toString(extras.getInt("TOTAL")));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_update_attendance,menu);
        m=menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done:
                int presentClasses, totalClasses;
                long rowID;
                if (TextUtils.isEmpty(dateSet)) {
                    Toast.makeText(UpdateAttendance.this, "Date is missing!", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        v.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                    else
                        v.vibrate(150);
                }

                try {
                    presentClasses = Integer.parseInt(presentTV.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(UpdateAttendance.this, "Classes Present is missing!", Toast.LENGTH_SHORT).show();
                    presentClasses = 9999;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        v.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                    else
                        v.vibrate(150);
                }

                try {
                    totalClasses = Integer.parseInt(totalTV.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(UpdateAttendance.this, "Total Classes is missing!", Toast.LENGTH_SHORT).show();
                    totalClasses = 9999;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        v.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                    else
                        v.vibrate(150);
                }
                String extraInfo = extraInfoTV.getText().toString();
                /*
                    Now we got all values checking if they're correct.
                 */
                //Show dialog TODO: add icon to AlertDialog
                if (totalClasses < presentClasses) {
                    AlertDialog.Builder incorrectAlert = new AlertDialog.Builder(UpdateAttendance.this);
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
                else if (totalClasses != 9999 && presentClasses != 9999 && !TextUtils.isEmpty(dateSet)) {

                    rowID = insertIntoDailyAttendance(dateSet,presentClasses,totalClasses,extraInfo);

                    if(rowID<0){

                        AlertDialog.Builder overWriteAlert = new AlertDialog.Builder(UpdateAttendance.this);
                        overWriteAlert.setTitle("Already Exsists!")
                                .setMessage("You've already updated attendence for "+dateTvText+". Do you want to overwrite it?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String whereClause = AttendanceEntry.COLUMN_DATE+"=?";
                                        String[] whereArgs = new String[]{dateSet};
                                        db.delete(AttendanceEntry.TABLE_DAILY_ATTENDANCE,whereClause,whereArgs);
                                        //db.delete(AttendanceEntry.TABLE_ATTENDED_DATES,whereClause,whereArgs);
                                        //tick.performClick();
                                        onOptionsItemSelected(m.findItem(R.id.done));
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dateTV.setText(null);
                                        presentTV.setText(null);
                                        totalTV.setText(null);
                                    }
                                }).show();
                    }
                    else {
                        Toast.makeText(UpdateAttendance.this, "Attendance Updated!", Toast.LENGTH_SHORT).show();
                        NavUtils.navigateUpFromSameTask(UpdateAttendance.this);
                    }
                }
                //Toast.makeText(this, "Attendance Updated!", Toast.LENGTH_SHORT).show();
                //NavUtils.navigateUpFromSameTask(UpdateAttendance.this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
        String dateTVText;
        if (selectedDay == day && selectedMonth == month && selectedYear == year) {
            dateTVText = "Today";
            dateTV.setText(dateTVText);
        }else{
            //Converting in form of Monday, Feb 7, 2019
            calendar.set(selectedYear, selectedMonth, selectedDay);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
            String dayOfWeek = simpleDateFormat.format(calendar.getTime());
            simpleDateFormat = new SimpleDateFormat("MMM");
            String monthName = simpleDateFormat.format(calendar.getTime());
            dateTVText = monthName+" "+Integer.toString(selectedDay)+", "+Integer.toString(selectedYear);
            /* Not adding dayOfWeek into dateTVText coz, it will be used in
               dialog box (shown when user tries to enter attendance for already
               existing day in db)*/
            dateTV.setText(dayOfWeek+", "+dateTVText);
        }
        /*Converting it in a form which fits in database.
            i.e .. in form of yyyy-MM-dd, appending month with 0 if its <10 for
            sorting purpose.
        */
        selectedMonth+=1;
        String month=Integer.toString(selectedMonth),day=Integer.toString(selectedDay);
        if(selectedMonth<10)
            month="0"+Integer.toString(selectedMonth);
        if(selectedDay<10)
            day="0"+Integer.toString(selectedDay);
        // dateSet is string which is inserted into db.
        dateSet= Integer.toString(selectedYear)+"-"+month+"-"+day;
        //Global var dateTvText is used in dialog. dateTVText is local.
        dateTvText=dateTVText;
    }

    private long insertIntoDailyAttendance(String date,int present,int total,String extraInfo){
        long newRowId,rowIdDate;
        //First Inserting date into AttendedDates table.
        ContentValues values = new ContentValues();
        values.put(AttendanceEntry.COLUMN_DATE, date);
        /*rowIdDate = db.insert(AttendanceEntry.TABLE_ATTENDED_DATES,null,values);
        if(rowIdDate<0) {
            //Then date already exists
            return rowIdDate;
        }*/
        values.put(AttendanceEntry.COLUMN_CLASSESPRESENT, present);
        values.put(AttendanceEntry.COLUMN_TOTAL_CLASSES, total);
        if(!TextUtils.isEmpty(extraInfo)) {
            values.put(AttendanceEntry.COLUMN_EXTRAINFO, extraInfo);
            newRowId = db.insert(AttendanceEntry.TABLE_DAILY_ATTENDANCE, null, values);
        }else {
            newRowId = db.insert(AttendanceEntry.TABLE_DAILY_ATTENDANCE, AttendanceEntry.COLUMN_EXTRAINFO, values);
        }
        return newRowId;
    }
}
