package com.example.eventreporter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    private List<Event> eventList;
    private DatabaseReference databaseReference;
    private Context context;


    /**
     * Constructor for EventListAdapter
     * @param events events that are showing on screen
     */
    public EventListAdapter(List<Event> events, Context context){
        eventList = events;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        this.context = context;
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

        public ImageView img_view_good;
        public ImageView img_view_comment;

        public TextView good_number;
        public TextView comment_number;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            title = (TextView) v.findViewById(R.id.event_item_title);
            location = (TextView) v.findViewById(R.id.event_item_location);
            description = (TextView) v.findViewById(R.id.event_item_description);
            time = (TextView) v.findViewById(R.id.event_item_time);
            imgView = (ImageView) v.findViewById(R.id.event_item_img);

            img_view_good = (ImageView) v.findViewById(R.id.event_good_img);
            img_view_comment = (ImageView) v.findViewById(R.id.event_comment_img);
            good_number = (TextView) v.findViewById(R.id.event_good_number);
            comment_number = (TextView) v.findViewById(R.id.event_comment_number);
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
        holder.time.setText(Utils.timeTransformer(event.getTime()));

        holder.good_number.setText(String.valueOf(event.getLike()));

        if (event.getImgUrl() != null) {
            final String url = event.getImgUrl();
            holder.imgView.setVisibility(View.VISIBLE);
            new AsyncTask<Void, Void, Bitmap>(){
                @Override
                protected Bitmap doInBackground(Void... params){
                    return Utils.getBitmapFromURL(url);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap){
                    holder.imgView.setImageBitmap(bitmap);
                }
            }.execute();
        } else {
            holder.imgView.setVisibility(View.GONE);
        }

        // when user likes the event, push like number to firebase database
        holder.img_view_good.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               databaseReference.child("events").addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                           //TODO: optimize this method
                           Event recordedevent = snapshot.getValue(Event.class);
                           if (recordedevent.getId().equals(event.getId())) {
                               int number = recordedevent.getLike();
                               holder.good_number.setText(String.valueOf(number + 1));
                               snapshot.getRef().child("like").setValue(number + 1);
                               break;
                           }
                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
            }

        });


        holder.layout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(context, CommentActivity.class);
                String eventId = event.getId();
                intent.putExtra("EventID", eventId);
                context.startActivity(intent);
            }
        });
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
