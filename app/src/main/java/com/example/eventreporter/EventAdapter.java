package com.example.eventreporter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.squareup.picasso.Picasso;

import java.util.List;

public class EventAdapter extends BaseAdapter {
    Context context;
    List<Event> eventData;

    public EventAdapter(Context context){
        this.context = context;
        eventData = DataService.getEventData();
    }

    @Override
    public int getCount() {
        return eventData.size();
    }

    @Override
    public Event getItem(int position){
        return eventData.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.event_item,
                    parent, false);
        }

        // acquire the element and then set
        TextView eventTitle = (TextView) convertView.findViewById(
                R.id.event_title);
        TextView eventAddress = (TextView) convertView.findViewById(
                R.id.event_address);
        TextView eventDescription = (TextView) convertView.findViewById(
                R.id.event_description);
        eventTitle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            }
        });
        ImageView imageView = convertView.findViewById(R.id.event_thumbnail);
        Picasso.get().load("http://www.urbanfarmhub.org/wp-content/uploads/2012/07/apple.png").into(imageView);

        eventTitle.setTextColor(context.getColor(R.color.colorAccent));
        Event r = eventData.get(position);
        eventTitle.setText(r.getTitle());
        eventAddress.setText(r.getAddress());
        eventDescription.setText(r.getDescription());

        return convertView;
    }

}
