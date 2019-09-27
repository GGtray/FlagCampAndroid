package com.example.flagcamp.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flagcamp.R;
import com.example.flagcamp.Utils.Job;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<Job> {
    private List<Job> jobs;

    public ListViewAdapter(Context context, List<Job> jobs) {
        super(context, 0, jobs);
        this.jobs = jobs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView = convertView;

        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item, parent, false);
        }
        Job job = jobs.get(position);
        TextView companyTextView = (TextView) listView.findViewById(R.id.company_text_view);
        companyTextView.setText(job.getCompany());

        TextView locationTextView = (TextView) listView.findViewById(R.id.location_text_view);
        locationTextView.setText(job.getLocation());

        TextView titleTextView = (TextView) listView.findViewById(R.id.title_text_view);
        titleTextView.setText(job.getTitle());

        ImageView jobLogo = (ImageView) listView.findViewById(R.id.job_logo);

        if(job.getCompanyLogoUrl() == null) {
            Picasso.get()
                    .load(R.drawable.notfound)
                    // Image to load when something goes wrong
                    .into(jobLogo);
        } else {
            Picasso.get()
                    .load(job.getCompanyLogoUrl())
                    .into(jobLogo);
        }

        return listView;
    }


}
