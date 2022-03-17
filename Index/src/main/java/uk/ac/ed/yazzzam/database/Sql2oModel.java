package uk.ac.ed.yazzzam.database;

import org.sql2o.Connection;
import org.sql2o.Sql2o;
import uk.ac.ed.yazzzam.GlobalSettings;
import uk.ac.ed.yazzzam.Indexer.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
        var sql = "select * from songs WHERE lyrics is not NULL AND lyrics != \'\'";
//        var sql = "select * from songs WHERE lyrics is not NULL AND lyrics != \'\' LIMIT 100000";
        try(Connection conn = sql2o.open()){
            List<SongData> songs = conn.createQuery(sql)
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
        String sql = "SELECT title FROM songs WHERE title is not NULL";
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql).executeScalarList(String.class);
        }
    }

    public List<SongData> getSameGenre(String genre){
        String sql = "SELECT * FROM songs where genre = :genre LIMIT 25";
        try (Connection conn = sql2o.open()) {
            List<SongData> songs = conn.createQuery(sql)
                    .addParameter("genre", genre)
                    .executeAndFetch(SongData.class);
            Collections.shuffle(songs);
            if (songs.size() > 5) {
                return songs.subList(0, 5);
            }
            else {
                return songs;
            }
        }
    }

    public List<SongData> getSameArtist(String artist){
        String sql = "SELECT * FROM songs where artist = :artist LIMIT 25";
        try (Connection conn = sql2o.open()) {
            List<SongData> songs = conn.createQuery(sql)
                    .addParameter("artist", artist)
                    .executeAndFetch(SongData.class);
            Collections.shuffle(songs);
            if (songs.size() > 5) {
                return songs.subList(0, 5);
            }
            else {
                return songs;
            }
        }
    }

    public List<SongData> getSameYear(String year){
        String sql = "SELECT * FROM songs where year = :year LIMIT 25";
        try (Connection conn = sql2o.open()) {
            List<SongData> songs = conn.createQuery(sql)
                    .addParameter("year", year)
                    .executeAndFetch(SongData.class);
            Collections.shuffle(songs);
            if (songs.size() > 5) {
                return songs.subList(0, 5);
            }
            else {
                return songs;
            }
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
        String sql = "SELECT DISTINCT(genre) FROM songs WHERE genre is not NULL";
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql).executeScalarList(String.class);
        }
    }

    public List<String> getAllArtists() {
        String sql = "SELECT DISTINCT(artist) FROM songs WHERE artist is not NULL";
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql).executeScalarList(String.class);
        }
    }

    public Set<Integer> filterSongs(String genre, String year, String title, String artist) {
        String whereStatement = makeWhereStatement(genre, year, title, artist);
        if (!whereStatement.equals("")) {
            String sql = "SELECT id FROM songs" + whereStatement;
            System.out.println(sql);
            try (Connection conn = sql2o.open()) {
                return conn.createQuery(sql).executeScalarList(String.class).stream().map(id -> Integer.parseInt(id)).collect(Collectors.toSet());
            }
        }
        return null;

    }

    private String makeWhereStatement(String genre, String year, String title, String artist) {
        var toFilter = new ArrayList<String>();
        if (!genre.equals(GlobalSettings.NO_SEARCH)) {
            toFilter.add("genre = \'" + genre + "\'");
        }
        if (!title.equals(GlobalSettings.NO_SEARCH)) {
            toFilter.add("title = \'" + title + "\'");
        }
        if (!year.equals(GlobalSettings.NO_SEARCH)) {
            toFilter.add("year = \'" + year + "\'");
        }
        if (!artist.equals(GlobalSettings.NO_SEARCH)) {
            toFilter.add("artist = \'" + artist + "\'");
        }

        StringBuilder str = new StringBuilder();
        str.append(" WHERE ");
        var conditions = String.join(" AND ", toFilter);
        str.append(conditions);

        return toFilter.size() > 0 ? str.toString() : "";

    }
}
