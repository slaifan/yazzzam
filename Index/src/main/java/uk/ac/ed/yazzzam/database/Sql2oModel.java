package uk.ac.ed.yazzzam.database;

import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import uk.ac.ed.yazzzam.Indexer.Song;

import java.util.List;

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

    public String getLyrics (int songID) {
        String sql = "SELECT lyrics FROM songs WHERE id = :songID";
        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql)
                    .addParameter("songID", songID)
                    .executeScalar(String.class);
        }
    }

    public String getArtist (int songID){
        String sql = "SELECT artist FROM songs WHERE id = :songID";
        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql)
                    .addParameter("songID", songID)
                    .executeScalar(String.class);
        }
    }

    public String getGenre(int songID){
        String sql = "SELECT genre FROM songs WHERE id = :songID";
        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql)
                    .addParameter("songID", songID)
                    .executeScalar(String.class);
        }
    }

    public Integer getYear(int songID){
        String sql = "SELECT year FROM songs WHERE id = :songID";
        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql)
                    .addParameter("songID", songID)
                    .executeScalar(Integer.class);
        }
    }

    public String getTitle(int songID){
        String sql = "SELECT title FROM songs WHERE id = :songID";
        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql)
                    .addParameter("songID", songID)
                    .executeScalar(String.class);
        }
    }

    // Insert multiple songs into table at the same time
    public String insertBunchOfSongs(List<Song> songs){
        final String sql = "insert into songs(id, title, artist, genre, year, lyrics) VALUES (:id, :title, :artist, :genre, :year, :lyrics)";

        try (Connection conn = sql2o.beginTransaction()){
            Query query = conn.createQuery(sql);

            for (int i = 0; i < songs.size(); i++){
                query.addParameter("id", i)
                        .addParameter("title", songs.get(i).getTitle())
                        .addParameter("artist", songs.get(i).getArtist())
                        .addParameter("genre", songs.get(i).getGenre())
                        .addParameter("year", songs.get(i).getYear())
                        .addParameter("lyrics", songs.get(i).getLyrics())
                        .addToBatch();
            }
            query.executeBatch();
            conn.commit();
        }
        return "Success!";
    }




    // song title from og id
    // song lyrics from title
    // whole row from id
    // Takes Song object, loops over metadata except lyrics, if not null create SQL query


}
