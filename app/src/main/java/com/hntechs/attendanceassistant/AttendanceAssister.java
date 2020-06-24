package com.hntechs.attendanceassistant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class AttendanceAssister extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_assister);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView attendedTV = findViewById(R.id.classesAttended),
                totalTV = findViewById(R.id.totalClasses);
        CircularProgressBar circularProgressBar = findViewById(R.id.circularProgressBar);
        Intent i =getIntent();
        float avg=i.getFloatExtra("Average",0);
        int present = i.getIntExtra("PresentClasses",0);
        int total = i.getIntExtra("TotalClasses",0);
        circularProgressBar.setProgress((int)avg);
        circularProgressBar.setTitle(avg+"%");

        attendedTV.setText(present+"");
        totalTV.setText(total+"");
        Toast.makeText(this,i.getIntExtra("PresentClasses",0)+" "+i.getIntExtra("TotalClasses",0),Toast.LENGTH_SHORT).show();
    }
}
