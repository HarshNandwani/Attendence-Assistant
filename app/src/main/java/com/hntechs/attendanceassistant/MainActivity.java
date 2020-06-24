package com.hntechs.attendanceassistant;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hntechs.attendanceassistant.Notifications.AlarmHelper;
import com.hntechs.attendanceassistant.data.AttendanceAssistantContract.AttendanceEntry;
import com.hntechs.attendanceassistant.data.AttendanceAssistantDbHelper;
import java.text.DecimalFormat;
import java.util.StringTokenizer;
public class MainActivity extends AppCompatActivity {
    private CircularProgressBar c;
    private LinearLayout updateAttendance,viewAttendance,updateAsBatch,setLocation;
    private AttendanceAssistantDbHelper mDbHelper;
    private SQLiteDatabase db;
    private int present,total;
    TextView notUpdatedTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AlarmHelper.scheduleAlarms(this);
        mDbHelper = new AttendanceAssistantDbHelper(this);
        db=mDbHelper.getReadableDatabase();

        c = findViewById(R.id.cpb);
        updateAttendance=findViewById(R.id.updateAttendance);
        viewAttendance=findViewById(R.id.viewAttendance);
        updateAsBatch=findViewById(R.id.updateAsBatch);
        setLocation=findViewById(R.id.setLocation);
        notUpdatedTV = findViewById(R.id.notUpdatedTV);
        ImageView settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main2Settings = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(main2Settings);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final float avg; int average; boolean isAvgInt;
        int setThisNumInProgress;
        avg = calculateAttendance();
        CharSequence sizedFloatAverage="";
        if(avg<=100){
            //USER HAS ENTERED ATTENDANCE
            // Converting xx.0 to x using decimal format and storing in num String.
            DecimalFormat myFormat = new DecimalFormat("0.##");
            String num = myFormat.format(avg);

            try{
                //Checking if its integer or still a float (after conversion)
                average=Integer.parseInt(num);
                setThisNumInProgress=average;
                isAvgInt=true;
            }catch (Exception e){
                //Comes to catch if parse exception occurs ie.. its float.
                try{
                    float res = Float.parseFloat(num);

                    StringTokenizer tokenizer=new StringTokenizer(num,".");
                    String first=tokenizer.nextToken();
                    setThisNumInProgress=Integer.parseInt(first);
                    String second = tokenizer.nextToken();
                    second="."+second+"%";
                    int textSize1 = getResources().getDimensionPixelSize(R.dimen.number_text_size);
                    int textSize2 = getResources().getDimensionPixelSize(R.dimen.decimal_text_size);

                    SpannableString span1 = new SpannableString(first);
                    span1.setSpan(new AbsoluteSizeSpan(textSize1), 0, first.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                    SpannableString span2 = new SpannableString(second);
                    span2.setSpan(new AbsoluteSizeSpan(textSize2), 0, second.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    isAvgInt=false;
                    sizedFloatAverage=TextUtils.concat(span1,span2);
                }catch (Exception e1){
                    //TODO: Know About this catch block
                    setThisNumInProgress=100;
                    isAvgInt=false;
                }
            }

            final boolean finalIsAvgInt = isAvgInt;
            final CharSequence finalSizedFloatAverage = sizedFloatAverage;
            c.animateProgressTo(0, setThisNumInProgress, new CircularProgressBar.ProgressAnimationListener() {
                @Override
                public void onAnimationStart() {

                }

                @Override
                public void onAnimationFinish() {
                    if(!finalIsAvgInt) {
                         c.setTitle(finalSizedFloatAverage.toString());
                       // notUpdatedTV.setText(finalSizedFloatAverage);
                      //  notUpdatedTV.setVisibility(View.VISIBLE);
                    }
                  //  c.setTitle(null);
                }

                @Override
                public void onAnimationProgress(int progress) {
                    c.setTitle(progress+"%");
                }
            });


            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mainToAssist = new Intent(MainActivity.this,AttendanceAssister.class);
                    mainToAssist.putExtra("PresentClasses",present);
                    mainToAssist.putExtra("TotalClasses",total);
                    mainToAssist.putExtra("Average",avg);
                    startActivity(mainToAssist);
                }
            });

        }else{
            //USER HAS NOT UPDATED ANY ATTENDANCE
            c.setProgress(100);
            c.setTitle(null);
            notUpdatedTV.setVisibility(View.VISIBLE);
           /* notUpdatedTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent TVtoUpdateAttendance = new Intent(MainActivity.this,UpdateAttendance.class);
                    startActivity(TVtoUpdateAttendance);
                }
            });*/


            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent TVtoUpdateAttendance = new Intent(MainActivity.this,UpdateAttendance.class);
                    startActivity(TVtoUpdateAttendance);
                }
            });

        }

        updateAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainToUpdate = new Intent(MainActivity.this,UpdateAttendance.class);
                startActivity(mainToUpdate);
            }
        });

        updateAsBatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainToBatchUpdate = new Intent(MainActivity.this,BatchUpdate.class);
                startActivity(mainToBatchUpdate);
            }
        });

        viewAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainToCatalog = new Intent(MainActivity.this,AttendanceCatalog.class);
                startActivity(mainToCatalog);
            }
        });

        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainToMaps = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(mainToMaps);
            }
        });



    }

    private float calculateAttendance(){

        present=0;
        total=0;
        boolean d,b;

        Cursor dailyTable = db.query(AttendanceEntry.TABLE_DAILY_ATTENDANCE,
                new String[]{AttendanceEntry.COLUMN_CLASSESPRESENT,AttendanceEntry.COLUMN_TOTAL_CLASSES},
                null,null,null,null,null);

        Cursor batchTable = db.query(AttendanceEntry.TABLE_BATCH_ATTENDANCE,
                new String[]{AttendanceEntry.COLUMN_CLASSESPRESENT,AttendanceEntry.COLUMN_TOTAL_CLASSES},
                null,null,null,null,null);

        if(batchTable!=null && batchTable.getCount()>0){
            int pindex,tindex;
            try{
                pindex = batchTable.getColumnIndex(AttendanceEntry.COLUMN_CLASSESPRESENT);
                tindex = batchTable.getColumnIndex(AttendanceEntry.COLUMN_TOTAL_CLASSES);

                while (batchTable.moveToNext()){
                    present+=batchTable.getInt(pindex);
                    total+=batchTable.getInt(tindex);
                }
            }catch (Exception e){

            }finally {
                batchTable.close();
                b=true;
            }
        }else b=false;

        if(dailyTable!=null && dailyTable.getCount()>0){
            int pindex,tindex;
            try{
                pindex = dailyTable.getColumnIndex(AttendanceEntry.COLUMN_CLASSESPRESENT);
                tindex = dailyTable.getColumnIndex(AttendanceEntry.COLUMN_TOTAL_CLASSES);

                while (dailyTable.moveToNext()){
                    present+=dailyTable.getInt(pindex);
                    total+=dailyTable.getInt(tindex);
                }
            }catch (Exception e){

            }finally {
                dailyTable.close();
                d=true;
            }
        }else d=false;
        float avg;
        if(b || d)
            avg = (((float) present/(float) total)*100);
        else
            avg=101;
        return avg;
    }
}
