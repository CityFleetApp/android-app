package com.cityfleet.model;

import com.google.gson.annotations.SerializedName;

public class UserInfo {
    private String email;
    @SerializedName("full_name")
    private String fullName;
    private String phone;
    @SerializedName("hack_license")
    private String hackLicense;
    private String username;
    private String bio;
    private String drives;
    @SerializedName("avatar_url")
    private String avatarUrl;
    @SerializedName("documents_up_to_date")
    private boolean documentsUpToDate;
    @SerializedName("jobs_completed")
    private String jobsCompleted;
    private int rating;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHackLicense() {
        return hackLicense;
    }

    public void setHackLicense(String hackLicense) {
        this.hackLicense = hackLicense;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDrives() {
        return drives;
    }

    public void setDrives(String drives) {
        this.drives = drives;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isDocumentsUpToDate() {
        return documentsUpToDate;
    }

    public void setDocumentsUpToDate(boolean documentsUpToDate) {
        this.documentsUpToDate = documentsUpToDate;
    }

    public String getJobsCompleted() {
        return jobsCompleted;
    }

    public void setJobsCompleted(String jobsCompleted) {
        this.jobsCompleted = jobsCompleted;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
