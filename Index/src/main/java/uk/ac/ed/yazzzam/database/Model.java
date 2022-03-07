package uk.ac.ed.yazzzam.database;

import uk.ac.ed.yazzzam.Indexer.Song;

import java.util.List;
import java.util.UUID;

public interface Model {

    int createSong(Song song, int id);
    List getAllSongs();
    boolean existSong(int songID);

}

