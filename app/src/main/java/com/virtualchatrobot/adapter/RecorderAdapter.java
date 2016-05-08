package com.virtualchatrobot.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.virtualchatrobot.bean.Recorder;
import com.virtualchatrobot.R;
import java.util.List;

/**
 * Created by Dolphix.J Qing on 2016/5/3.
 */
public class RecorderAdapter extends ArrayAdapter<Recorder> {

    private int minItemWidth;
    private int maxItemWidth;
    private LayoutInflater layoutInflater;

    public RecorderAdapter(Context context, List<Recorder> rDatas) {
        super(context,-1,rDatas);

        layoutInflater = LayoutInflater.from(context);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        maxItemWidth = (int)(displayMetrics.widthPixels * 0.7f);
        minItemWidth = (int)(displayMetrics.widthPixels * 0.2f);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (null == convertView){
            convertView = layoutInflater.inflate(R.layout.item_recorder,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.seconds = (TextView) convertView.findViewById(R.id.recorder_time);
            viewHolder.length = (View)convertView.findViewById(R.id.recorder_length);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.seconds.setText(Math.round(getItem(position).getTime())+"\"");
        ViewGroup.LayoutParams layoutParams = viewHolder.length.getLayoutParams();
        layoutParams.width = (int)(minItemWidth + (maxItemWidth / 60f * getItem(position).getTime()));

        return convertView;
    }

    private class ViewHolder{
        TextView seconds;
        View length;
    }
}
