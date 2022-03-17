package uk.ac.ed.yazzzam.database;

import lombok.Data;

@Data
public class SongData {
    private int id;
    private String title;
    private String artist;
    private String genre;
    private int year;
    public String lyrics;
}