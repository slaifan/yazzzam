package uk.ac.ed.yazzzam.Indexer;

import com.opencsv.bean.CsvBindByName;

import java.util.ArrayList;
import java.util.List;

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
    private List<String> preprocessedLyrics;

    public Song() {
        title = null;
        artist=null;
        genre=null;
        year= 0;
        lyrics=null;
        preprocessedLyrics= new ArrayList<>();
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

    public List<String> getPreprocessedLyrics() {
        return preprocessedLyrics;
    }

    public void setPreprocessedLyrics(List<String> preprocessedLyrics){
        this.preprocessedLyrics = preprocessedLyrics;
    }

    public void deleteLyrics(){
        this.lyrics = null;
    }

    public String toString(){
        return String.format("title: %s, artist: %s, genre: %s, year: %d, lyrics: %s \npreprocessed lyrics: %s", title, artist, genre, year, lyrics, preprocessedLyrics);
    }



}
