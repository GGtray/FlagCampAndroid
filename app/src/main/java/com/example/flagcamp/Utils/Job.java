package com.example.flagcamp.Utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

public class Job implements Parcelable {


    private String id;
    private String company;
    private String location;
    private String title;
    private String description;
    private String companyLogoUrl;
    private String jobType;
    private String detailUrl;
    private String postDate;
    private String applyUrl;

    public Job(){

    }

    public Job(String id, String company, String location, String title, String description, String companyLogoUrl, String jobType, String detailUrl, String postDate, String applyUrl) {
        this.id = id;
        this.company = company;
        this.location = location;
        this.title = title;
        description = Jsoup.clean(description, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
        this.description = description;
        this.companyLogoUrl = companyLogoUrl;
        this.jobType = jobType;
        this.detailUrl = detailUrl;
        this.postDate = postDate;
        Log.d("Constructor", applyUrl);
        Document doc = Jsoup.parse(applyUrl);
        Element link = doc.select("a").first();
        if (link != null) {
            this.applyUrl = link.attr("href");
        } else {
            this.applyUrl = detailUrl;
        }
    }

    protected Job(Parcel in) {
        id = in.readString();
        company = in.readString();
        location = in.readString();
        title = in.readString();
        description = in.readString();
        companyLogoUrl = in.readString();
        jobType = in.readString();
        detailUrl = in.readString();
        postDate = in.readString();
        applyUrl = in.readString();
    }

    public static final Creator<Job> CREATOR = new Creator<Job>() {
        @Override
        public Job createFromParcel(Parcel in) {
            return new Job(in);
        }

        @Override
        public Job[] newArray(int size) {
            return new Job[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getCompany() {
        return company;
    }

    public String getLocation() {
        return location;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCompanyLogoUrl() {
        return companyLogoUrl;
    }

    public String getJobType() {
        return jobType;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public String getPostDate() {
        return postDate;
    }

    public String getApplyUrl() {
        return applyUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(company);
        parcel.writeString(location);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(companyLogoUrl);
        parcel.writeString(jobType);
        parcel.writeString(detailUrl);
        parcel.writeString(postDate);
        parcel.writeString(applyUrl);
    }
}
