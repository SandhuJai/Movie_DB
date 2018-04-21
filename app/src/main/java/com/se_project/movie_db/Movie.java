package com.se_project.movie_db;

import java.util.ArrayList;

public class Movie {
    private String ID;
    private String title;   // title of the Movie
    private String releaseDate;
    private String rating;
    private String imagePoster;
    private String imageWallpaper;
    // TODO : Add poster, trailer,reviews, comments

    public Movie(String ID, String title, String releaseDate, String rating, String imagePoster, String imageWallpaper) {
        this.ID = ID;
        this.title = title;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.imagePoster = imagePoster;
        this.imageWallpaper = imageWallpaper;
    }

    public Movie(String title, String url, String ID) {
        this.title = title;
        this.imagePoster = url;
        this.ID = ID;
    }

    public Movie(String title, String url, String rating, String ID) {
        this.title = title;
        this.imagePoster = url;
        this.rating = rating;
        this.ID = ID;
    }

    public String getImagePoster() {
        return imagePoster;
    }

    public void setImagePoster(String imagePoster) {
        this.imagePoster = imagePoster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getImageWallpaper() {
        return imageWallpaper;
    }

    public void setImageWallpaper(String imageWallpaper) {
        this.imageWallpaper = imageWallpaper;
    }
}
