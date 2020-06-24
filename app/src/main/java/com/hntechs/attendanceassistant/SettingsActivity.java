package com.hntechs.attendanceassistant;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hntechs.attendanceassistant.Fragments.CollegeDetailsFragment;
import com.hntechs.attendanceassistant.Fragments.ProfileFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final SharedPreferences userPref = getSharedPreferences("UserPref",MODE_PRIVATE);

        final Switch notiSwitch = findViewById(R.id.notiSwitch);
        notiSwitch.setChecked(userPref.getBoolean("NotificationsEnabled",true));

        notiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    SharedPreferences.Editor editor=userPref.edit();
                    editor.putBoolean("NotificationsEnabled",true);
                    editor.commit();
                //    Toast.makeText(SettingsActivity.this,"Notifications are enabled",Toast.LENGTH_SHORT).show();
                }else {
                    SharedPreferences.Editor editor=userPref.edit();
                    editor.putBoolean("NotificationsEnabled",false);
                    editor.commit();
                //    Toast.makeText(SettingsActivity.this,"Notifications are disabled",Toast.LENGTH_SHORT).show();
                }
            }
        });



        LinearLayout profile = findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment profile = new ProfileFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.settingsFrame, profile);
                transaction.addToBackStack("Profile");
                transaction.commit();
            }
        });

        LinearLayout collegeDetails = findViewById(R.id.collegeDetails);
        collegeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment collegeDetails = new CollegeDetailsFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.settingsFrame, collegeDetails);
                transaction.addToBackStack("CollegeDetails");
                transaction.commit();

            }
        });


        LinearLayout setAttendance = findViewById(R.id.setAttendance);
        setAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder attendancePicker = new AlertDialog.Builder(SettingsActivity.this);
                LayoutInflater inflater = LayoutInflater.from(SettingsActivity.this);
                View myDialog = inflater.inflate(R.layout.dialog_attendance_picker,null);
                SeekBar seekBar = myDialog.findViewById(R.id.seekBar);
                final TextView attendanceTV = myDialog.findViewById(R.id.textViewAttendance);
                int minAtt = userPref.getInt("MinimumAttendance",65);
                seekBar.setProgress(minAtt);
                attendanceTV.setText(minAtt+" %");
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        attendanceTV.setText(i+" %");
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                attendancePicker.setView(myDialog)
                        .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String attendance = (String) attendanceTV.getText();
                                attendance=attendance.substring(0,attendance.length()-2);
                                int minAttendance = Integer.parseInt(attendance);

                                SharedPreferences.Editor editor = userPref.edit();
                                editor.putInt("MinimumAttendance",minAttendance);
                                editor.commit();
                                Toast.makeText(SettingsActivity.this,"Minimum attendance: "+attendance+" %",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .setCancelable(false).show();
            }
        });

        LinearLayout assistantNoti = findViewById(R.id.assistantNoti);
        assistantNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notiSwitch.setChecked(!notiSwitch.isChecked());
            }
        });

    }
}
