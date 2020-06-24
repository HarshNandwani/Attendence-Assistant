package com.hntechs.attendanceassistant;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.hntechs.attendanceassistant.data.AttendanceAssistantContract.AttendanceEntry;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BatchAttendanceCursorAdapter extends CursorAdapter {

    public BatchAttendanceCursorAdapter(Context context, Cursor c){
        super(context,c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_batch_attendance,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView fromDateLV = (TextView) view.findViewById(R.id.fromDateLV);
        TextView toDateLV = (TextView) view.findViewById(R.id.toDateLV);
        TextView bPresentLV = (TextView) view.findViewById(R.id.bPresentLV);
        TextView bTotalLV = (TextView) view.findViewById(R.id.bTotalLV);

        int fromColumnIndex = cursor.getColumnIndex(AttendanceEntry.COLUMN_FROM_DATE);
        int toColumnIndex = cursor.getColumnIndex(AttendanceEntry.COLUMN_TO_DATE);
        int presentColumnIndex = cursor.getColumnIndex(AttendanceEntry.COLUMN_CLASSESPRESENT);
        int totalColumnIndex = cursor.getColumnIndex(AttendanceEntry.COLUMN_TOTAL_CLASSES);

        String fromDate = cursor.getString(fromColumnIndex);
        String toDate = cursor.getString(toColumnIndex);
        int present = cursor.getInt(presentColumnIndex);
        int total = cursor.getInt(totalColumnIndex);

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM, yy");
        Date fromDateVar = null,toDateVar=null;
        try{
            fromDateVar = inputFormat.parse(fromDate);
            toDateVar = inputFormat.parse(toDate);
        }catch (ParseException e){

        }

        fromDate = outputFormat.format(fromDateVar);
        toDate = outputFormat.format(toDateVar);

        fromDateLV.setText(fromDate);
        toDateLV.setText(toDate);
        bPresentLV.setText(Integer.toString(present));
        bTotalLV.setText(Integer.toString(total));

    }
}
