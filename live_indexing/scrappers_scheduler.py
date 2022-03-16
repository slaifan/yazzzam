#TODO: TESTING/ SCRAPPING INPUT from DB 
from spotify_api import Client
import schedule, time
import postgres_handler as pgh
import lyrics_scraper as lscraper

def restore_nulls(conn):
    cur = conn.cursor()
    pgh.execute(cur, pgh.NULLS)
    cur.close()
    conn.commit()

def scrap_new_titles(c, conn):
    print("scraping new titles")
    cur = conn.cursor()
    titles = c.scrap_new_titles()
    for artist, track, genre, year in titles:
        pgh.execute(cur, pgh.INSERT_TITLE, (artist, track, genre, year,))
    cur.close()
    conn.commit()

def scrap_lyrics(conn):
    cur = conn.cursor()
    pgh.execute(cur, pgh.GET_A_TITLE)
    song = cur.fetchone()
    lyrics = lscraper.scrap_song(f"{song[0]} {song[1]}")
    cur2 = conn.cursor()
    if lyrics is not None:
        print(song[0], song[1], song[2], "found")
        pgh.execute(cur, pgh.INSERT_SONG, (song[0], song[1], song[2], song[3], lyrics))
    else:
        print(song[0], song[1], song[2], "not found")
        pgh.execute(cur, pgh.DELETE_A_TITLE, (song[1], song[2],))
    cur.close()
    cur2.close()
    conn.commit

def scrap_metadata(c, conn):
    cur = conn.cursor()
    pgh.execute(cur, pgh.SELECT_NO_METADATA_SONG)
    songs = cur.fetchall()
    if len(songs) != 0:
        for song in songs:
            id = song[0]
            metadata = c.search_metadata(f"{song[1]} {song[2]}")
            if metadata != None:
                print(metadata[0], metadata[1], metadata[2])
                genre = metadata[1] if metadata[1] is not None else ""
                year = metadata[2] if metadata[2] is not None else ""
            else:
                print("notfound")
                genre = ""
                year = ""
            cur2 = conn.cursor()
            pgh.execute(cur2, pgh.UPDATE_METADATA, (genre,year, id))
            cur.close()
            cur2.close()
            conn.commit()
    else:
        print("Done!")
        cur.close()
        conn.commit()
        restore_nulls(conn)
        return schedule.CancelJob

    

if __name__ == "__main__":

    _, conn = pgh.connect()
    c = Client()
    schedule.every().day.at("04:00").do(scrap_new_titles, c, conn)
    schedule.every(1).minutes.do(scrap_lyrics, conn)
    #schedule.every(1).seconds.do(scrap_metadata, c, conn)

    while True:
        schedule.run_pending()
        time.sleep(1)

