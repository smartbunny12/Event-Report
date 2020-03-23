package com.example.eventreporter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    private List<Event> eventList;


    /**
     * Constructor for EventListAdapter
     * @param events events that are showing on screen
     */
    public EventListAdapter(List<Event> events){
        eventList = events;
    }

    /**
     * Use ViewHolder to hold view widget, view holder is required to be used in recycler view
     * http://developer.android.com/training/improving-layouts/smooth-scrolling.html
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView location;
        public TextView description;
        public TextView time;
        public ImageView imgView;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            title = (TextView) v.findViewById(R.id.event_item_title);
            location = (TextView) v.findViewById(R.id.event_item_location);
            description = (TextView) v.findViewById(R.id.event_item_description);
            time = (TextView) v.findViewById(R.id.event_item_time);
            imgView = (ImageView) v.findViewById(R.id.event_item_img);
        }

    }

    /**
     * OnBlindViewHolder will render created view holder on screen
     * @param holder View Holder created for each position
     * @param position position needs to show
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        final Event event = eventList.get(position);
        holder.title.setText(event.getTitle());
        String[] locations = event.getAddress().split(",");
        holder.location.setText(locations[1] + "," + locations[2]);
        holder.description.setText(event.getDescription());
        holder.time.setText(String.valueOf(event.getTime()));

    }

    /**
     * By calling this method, each ViewHolder will be initiated and passed to onBindViewHolder
     * @param parent parent view
     * @param viewType we might have multiple view types
     * @return ViewHolder created
     */
    @Override
    public EventListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.event_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    /**
     * return the size of your dataset(invoked by the layout manager)
     */
    @Override
    public int getItemCount(){
        return eventList.size();
    }

}
