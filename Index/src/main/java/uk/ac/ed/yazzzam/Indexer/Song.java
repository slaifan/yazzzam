package uk.ac.ed.yazzzam.Indexer;

import com.opencsv.bean.CsvBindByName;

public class Song {

    @CsvBindByName(column = "track_name", required = true)
    private String title;
    @CsvBindByName(column = "artist_name", required = true)
    private String artist;
    @CsvBindByName(column = "genre")
    private String genre;
    @CsvBindByName(column = "release_date")
    private short year;
    @CsvBindByName(column = "lyrics", required = true)
    private String lyrics;

    public Song() {
        title = "";
        artist="";
        genre="";
        year=0;
        lyrics="";
    }

    public Song(String title, String artist, String genre, short year, String lyrics){

        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.year = year;
        this.lyrics = lyrics;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public short getYear() {
        return year;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setYear(short year) {
        this.year = year;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String toString(){
        return String.format("title: %s, artist: %s, genre: %s, year: %d, lyrics: %s", title, artist, genre, year, lyrics);
    }



}
