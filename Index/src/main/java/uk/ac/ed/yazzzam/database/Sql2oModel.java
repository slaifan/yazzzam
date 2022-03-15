package uk.ac.ed.yazzzam.database;

import org.sql2o.Connection;
import org.sql2o.Sql2o;
import uk.ac.ed.yazzzam.Indexer.Song;

import java.util.List;
import java.util.stream.Collectors;

public class Sql2oModel implements Model{

    private Sql2o sql2o;


    public Sql2oModel(Sql2o sql2o){
        this.sql2o = sql2o;
    }

    @Override
    public int createSong(Song song, int id){
        // fix fields

        String title = song.getTitle();
        String artist = song.getArtist();
        String genre = song.getGenre();
        int year = song.getYear();
        String lyrics = song.getLyrics();
        try(Connection conn = sql2o.beginTransaction()){

            conn.createQuery("insert into songs(id, title, artist, genre, year, lyrics) VALUES (:id, :title, :artist, :genre, :year, :lyrics)")
                    .addParameter("id", id)
                    .addParameter("title", title)
                    .addParameter("artist", artist)
                    .addParameter("genre", genre)
                    .addParameter("year", year)
                    .addParameter("lyrics", lyrics)
                    .executeUpdate();
            conn.commit();
            return id;
        }
    }

    @Override
    public List<SongData> getAllSongs(){
        try(Connection conn = sql2o.open()){
            List<SongData> songs = conn.createQuery("select * from songs")
                    .executeAndFetch(SongData.class);
            return songs;
        }
    }

    public List<SongData> getSongs(List<Integer> songIds){
        var songIdsStr = songIds.stream().map(String::valueOf)
                .collect(Collectors.joining(","));
        try(Connection conn = sql2o.open()){
            List<SongData> songs = conn.createQuery("select * from songs WHERE id IN(" + String.join(",", songIdsStr) + ")")
                    .executeAndFetch(SongData.class);
            return songs;
        }
    }

    @Override
    public boolean existSong(int songID){
        try(Connection conn = sql2o.open()){
            List<SongData> songs = conn.createQuery("select * from songs where id = :songID")
                    .addParameter("song", songID)
                    .executeAndFetch(SongData.class);

            return songs.size() > 0;
        }
    }



    public List<String> getAllTitles(){
        String sql = "SELECT title FROM songs";
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql).executeScalarList(String.class);
        }
    }

    public Integer getSongCount(){
        String sql = "SELECT count(title) FROM songs";
        try (Connection conn = sql2o.open()){
            return conn.createQuery(sql).executeScalar(Integer.class);
        }
    }

    public SongData getSong(int songID) {
        String sql = "SELECT * FROM songs WHERE id = :songID";
        try(Connection conn = sql2o.open()){
            SongData song = conn.createQuery(sql)
                    .addParameter("songID", songID)
                    .executeAndFetchFirst(SongData.class);
            return song;
        }
    }


    public List<String> getAllGenres() {
        String sql = "SELECT DISTINCT(genre) FROM songs";
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql).executeScalarList(String.class);
        }
    }

    public List<String> getAllArtists() {
        String sql = "SELECT DISTINCT(artist) FROM songs";
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql).executeScalarList(String.class);
        }
    }
}
