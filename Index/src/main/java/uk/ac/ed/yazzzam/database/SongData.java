package uk.ac.ed.yazzzam.database;

import lombok.Data;

import java.util.UUID;

@Data
public class SongData {
    private int id;
    private String title;
    private String artist;
    private String genre;
    private int year;
    private String lyrics;
}