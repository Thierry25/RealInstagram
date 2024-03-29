package com.codepath.instagramclient;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.codepath.instagramclient.CommentActivity;
import com.codepath.instagramclient.InstagramPhoto;
import com.codepath.instagramclient.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {
    //what data we need from activity
    //context, datasource
    Context context ;
    public InstagramPhotosAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        this.context = context;
    }
    //what out item looks like
    //Use template to display each photo

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        //get the data item for this position
        final InstagramPhoto photo = getItem(position);
        //check if we are using a recycled view, if not we need to inflate
        if (convertView == null){
            //create new view from template
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo,parent,false);
        }
        //lookup views for popukating data (image,caption)
        TextView tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
        TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        ImageView ivPhoto = (ImageView) convertView.findViewById(R.id.ivPhoto);
        ImageView ivUserPic = (ImageView) convertView.findViewById(R.id.ivUserPic);
        TextView tvRelativePostingTime = (TextView) convertView.findViewById(R.id.tvRelativePostingTime);
        Button btnLikesCount = (Button) convertView.findViewById(R.id.btnLikesCount);
        TextView tvComments = (TextView) convertView.findViewById(R.id.tvComments);
        Button btnMoreComments = (Button) convertView.findViewById(R.id.btnMoreComments);
        final VideoView vvVideo = (VideoView) convertView.findViewById(R.id.vvVideo);
        //insert the model data into each of the view items
        tvCaption.setText(Html.fromHtml("<font color='#407399'>" + photo.getUsername() + "</font>&nbsp;<font color='#808080'>" + photo.getCaption() + "</font><br>"));
        tvUsername.setText(Html.fromHtml("&nbsp;<font color='#808080'>" + photo.getUsername() + "</font>"));
        if(photo.getImageUrl().endsWith("mp4") || photo.getImageUrl().endsWith("3gp")){
            ivPhoto.setVisibility(View.INVISIBLE);
            vvVideo.setVisibility(View.VISIBLE);
//            // Create a progressbar
//            final ProgressDialog pDialog = new ProgressDialog(getContext());
//            // Set progressbar title
//            pDialog.setTitle("Android Video Streaming Tutorial");
//            // Set progressbar message
//            pDialog.setMessage("Buffering...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            // Show progressbar
//            pDialog.show();

            // vvVideo.setVideoPath(photo.getImageUrl());
            Uri video = Uri.parse(photo.getImageUrl());
            //videoview.setMediaController(mediacontroller);
            vvVideo.setVideoURI(video);
            MediaController mediaController = new MediaController(getContext());
            mediaController.setAnchorView(vvVideo);
            vvVideo.setMediaController(mediaController);
            vvVideo.requestFocus();
            vvVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
                    vvVideo.start();
                    //pDialog.dismiss();
                }
            });
        } else {
            ivPhoto.setVisibility(View.VISIBLE);
            vvVideo.setVisibility(View.INVISIBLE);
            //clear the imageview
            ivPhoto.setImageResource(0);
            //insert image using picasso
            Picasso.with(getContext()).load(photo.getImageUrl()).placeholder(R.drawable.placeholder).into(ivPhoto);
        }
        //Picasso.with(getContext()).load(photo.userProfilePic).into(ivUserPic);
        //if(photo.getUserProfilePic()!="")
        Picasso.with(getContext()).load(photo.getUserProfilePic()).placeholder(R.drawable.placeholder).into(ivUserPic);
        tvRelativePostingTime.setText(photo.getRelativePostingTime());
        if(photo.getLikeCount() > 0) {
            btnLikesCount.setText(photo.getLikeCount() + " likes");
            btnLikesCount.setVisibility(View.VISIBLE);
        } else {
            btnLikesCount.setVisibility(View.INVISIBLE);
        }
        //display comments
        StringBuffer commentText=new StringBuffer();
        if(photo.getComments().size() > 0) {
            tvComments.setVisibility(View.VISIBLE);
            int i=0;
            for(InstagramComment comment:photo.getComments()) {
                if(i==2)
                    break;
                commentText.append("<font color='#407399'>" + comment.getUsername() + "</font>&nbsp;" + comment.getText() + "<br>");
                i++;
            }
            //commentText.append("<br><font color='#407399'>View all " + photo.getComments().size() + " comments</font><br>");
            //tvComments.setTextColor();
            tvComments.setText(Html.fromHtml(commentText.toString()));
            if(photo.getComments().size() > 0) {
                btnMoreComments.setText("view all " + photo.getComments().size() + " comments");
                btnMoreComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, CommentActivity.class);
                        i.putParcelableArrayListExtra("comments_list", (ArrayList<InstagramComment>) photo.getComments());

                        // brings up the second activity
                        context.startActivity(i);
                    }
                });
            }
            // String styledText =""; cache.timeView.setText(Html.fromHtml(styledText) )
        } else {
            tvComments.setVisibility(View.INVISIBLE);
        }
        //return the created item as a view
        return convertView;
    }
}