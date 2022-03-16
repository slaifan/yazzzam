import postgres_handler as pgh
import csv, sys

def create_table(cur):
    pgh.execute(cur, pgh.CREATE_SONGS_TABLE)

def insert_database(cur, csvfile):
    with open(csvfile, 'r') as f:
        reader = csv.DictReader(f, delimiter="\t", quotechar='"', quoting=csv.QUOTE_MINIMAL)
        next(reader)
        for row in reader:
            artist = row["artist_name"]
            title = row["track_name"]
            genre = row["genre"] if row["genre"] !="" else None
            year = row["release_date"] if row["release_date"] !="" else None
            lyrics = row["lyrics"]
            pgh.execute(cur, pgh.INSERT_SONG, (title, artist, genre, year, lyrics,))

def delete_dublicates(cur):
    pgh.execute(cur, pgh.DELETE_DUBLICATES)

if __name__ == "__main__":

    cur, conn = pgh.connect()

    #create_table(cur)
    #insert_database(cur, sys.argv[1])
    delete_dublicates(cur)
    
    cur.close()
    conn.commit()
    conn.close()

