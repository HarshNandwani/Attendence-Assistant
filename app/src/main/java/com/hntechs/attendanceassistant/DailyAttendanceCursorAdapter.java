package com.hntechs.attendanceassistant;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.hntechs.attendanceassistant.data.AttendanceAssistantContract.AttendanceEntry;

public class DailyAttendanceCursorAdapter extends CursorAdapter {

    public DailyAttendanceCursorAdapter(Context context, Cursor c){
        super(context,c,0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_daily_attendance,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView date = (TextView) view.findViewById(R.id.dDateLV);
        TextView total = (TextView) view.findViewById(R.id.dTotalLV);
        TextView present = (TextView) view.findViewById(R.id.dPresentLV);

        int dateColumnIndex = cursor.getColumnIndex(AttendanceEntry.COLUMN_DATE);
        int presentColumnIndex = cursor.getColumnIndex(AttendanceEntry.COLUMN_CLASSESPRESENT);
        int totalColumnIndex = cursor.getColumnIndex(AttendanceEntry.COLUMN_TOTAL_CLASSES);

        String datee = cursor.getString(dateColumnIndex);
        int presentt = cursor.getInt(presentColumnIndex);
        int totall = cursor.getInt(totalColumnIndex);

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("EEEE, dd MMM, yy");
        Date dateVar = null;
        try {
            dateVar = inputFormat.parse(datee);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        datee=outputFormat.format(dateVar);
        date.setText(datee);
        total.setText(String.valueOf(totall));
        present.setText(String.valueOf(presentt));
    }
}
