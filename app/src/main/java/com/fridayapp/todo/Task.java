package com.fridayapp.todo;

public class Task {
    private String checkin_location, checkout_location;
    private String checkin_date, checkout_date;
    private String status;
    private String id, image_url;

    public Task(String image_url,String checkin_location, String checkin_date, String checkout_location, String checkout_date, String status, String id) {
        this.image_url = image_url;
        this.checkin_location = checkin_location;
        this.checkin_date = checkin_date;
        this.checkout_location = checkout_location;
        this.checkout_date = checkout_date;
        this.status = status;
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getCheckout_location() {
        return checkout_location;
    }

    public void setCheckout_location(String checkout_location) {
        this.checkout_location = checkout_location;
    }

    public String getCheckout_date() {
        return checkout_date;
    }

    public void setCheckout_date(String checkout_date) {
        this.checkout_date = checkout_date;
    }

    public String getCheckin_location() {
        return checkin_location;
    }

    public void setCheckin_location(String checkin_location) {
        this.checkin_location = checkin_location;
    }

    public String getCheckin_date() {
        return checkin_date;
    }

    public void setCheckin_date(String checkin_date) {
        this.checkin_date = checkin_date;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }
}

