package com.example.flagcamp.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flagcamp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.MyViewHolder> {

    private List<Job> jobs;
    private OnJobClickListener onJobClickListener;

    public JobAdapter(List<Job> jobs, OnJobClickListener onJobClickListener) {
        this.jobs = jobs;
        this.onJobClickListener = onJobClickListener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView companyLogoImageView;
        TextView companyNameTextView;
        TextView locationTextView;
        TextView jobTypeTextView;
        TextView jobTitleTextView;
        TextView viewDetailsTextView;
        TextView postDateTextView;
        TextView descriptionTextView;
        OnJobClickListener onJobClickListener;

        public MyViewHolder(View layout, OnJobClickListener onJobClickListener) {
            super(layout);
            this.companyLogoImageView = layout.findViewById(R.id.company_logo_image_view);
            this.companyNameTextView = layout.findViewById(R.id.company_name_text_view);
            this.locationTextView = layout.findViewById(R.id.location_text_view);
            this.jobTypeTextView = layout.findViewById(R.id.job_type_text_view);
            this.jobTitleTextView = layout.findViewById(R.id.job_title_text_view);
            this.viewDetailsTextView = layout.findViewById(R.id.view_details_text_view);
            this.postDateTextView = layout.findViewById(R.id.post_date_text_view);
            this.descriptionTextView = layout.findViewById(R.id.description_text_view);
            this.onJobClickListener = onJobClickListener;
            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            this.onJobClickListener.onJobClick(getAdapterPosition());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Job job = jobs.get(position);
//        if (job.getCompanyLogoUrl() != null) {
//            Picasso.get().load(job.getCompanyLogoUrl()).into(holder.companyLogoImageView);
//        }
        if (job.getCompanyLogoUrl() == null) {
            holder.companyLogoImageView.setImageResource(R.mipmap.ic_default_logo);
        } else {
            Picasso.get().load(job.getCompanyLogoUrl()).into(holder.companyLogoImageView);
        }
        holder.companyNameTextView.setText(job.getCompany());
        holder.locationTextView.setText(job.getLocation());
        holder.jobTypeTextView.setText(job.getJobType());
        holder.jobTitleTextView.setText(job.getTitle());
        holder.postDateTextView.setText(job.getPostDate());
        holder.descriptionTextView.setText(job.getDescription());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(layout, onJobClickListener);
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public void clear() {
        jobs.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Job> jobs) {
        this.jobs.addAll(jobs);
        notifyDataSetChanged();
    }

    public List<Job> getJobs() {
        return this.jobs;
    }

    public interface OnJobClickListener {
        void onJobClick(int position);
    }
}