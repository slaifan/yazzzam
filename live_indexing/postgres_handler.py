from configparser import ConfigParser
import psycopg2

##  commands ##

CREATE_SONGS_TABLE = """
        CREATE TABLE songs (
            id SERIAL PRIMARY KEY,
            title TEXT NOT NULL,
            artist TEXT NOT NULL,
            genre TEXT,
            year TEXT,
            lyrics TEXT,
            is_indexed BOOLEAN)
        """

CREATE_TITLES_TABLE = """
        CREATE TABLE titles (
                title TEXT NOT NULL
                artist TEXT NOT NULL
                genre TEXT,
                year INTEGER,
                PRIMARY KEY (title, artist))
        """

DELETE_DUBLICATES = """DELETE FROM
                            songs a
                                USING songs b
                        WHERE
                            a.id < b.id
                            AND a.title = b.title
                            AND a.artist = b.artist;"""

INSERT_SONG = """INSERT INTO songs(title, artist, genre, year, lyrics)
             VALUES(%s, %s, %s, %s, %s);"""

INSERT_TITLE = """INSERT INTO songs(title, artist, genre, year, lyrics)
             VALUES(%s, %s, %s, %s, NULL);"""

GET_ALL_SONGS ="SELECT * FROM songs ORDER BY artist;"

SELECT_A_SONG ="""SELECT * FROM songs
                WHERE
                    artist = %s
                AND 
                    title = %s;"""

GET_A_TITLE= """SELECT * FROM songs WHERE lyrics is NULL LIMIT 1;"""

DELETE_A_TITLE = """DELETE FROM titles
                    WHERE title = %s 
                    AND artist = %s;"""

SELECT_NO_METADATA_SONG ="""SELECT id, title, artist, genre, year FROM songs
                            WHERE
                                genre IS NULL
                            OR 
                                year IS NULL LIMIT 20;"""

UPDATE_METADATA = """UPDATE songs
                        SET genre=%s, year=%s
                        WHERE id = %s;"""

NULLS = """UPDATE songs SET genre= NULLIF(genre, ''), year= NULLIF(year, '');"""
                


def config(filename='database.ini', section='postgresql'):
    # create a parser
    parser = ConfigParser()
    # read config file
    parser.read(filename)

    # get section, default to postgresql
    db = {}
    if parser.has_section(section):
        params = parser.items(section)
        for param in params:
            db[param[0]] = param[1]
    else:
        raise Exception('Section {0} not found in the {1} file'.format(section, filename))

    return db


def connect():
    """ Connect to the PostgreSQL database server """
    conn = None
    try:
        # read connection parameters
        params = config()

        # connect to the PostgreSQL server
        print('Connecting to the PostgreSQL database...')
        conn = psycopg2.connect(**params)
		
        # create a cursor
        cur = conn.cursor()
        
        print('PostgreSQL database version:')
        cur.execute('SELECT version()')

        # display the PostgreSQL database server version
        db_version = cur.fetchall()
        print(db_version)
        print("CONNECTED")
       
    except (Exception, psycopg2.DatabaseError) as error:
        print(error)
    else:
        return cur, conn


def execute(cur=None, command=None, values=None):
    """ execute SQL commands"""
    try:
        cur.execute(command, values)
        
    except (Exception, psycopg2.DatabaseError) as error:
        print(error)
    


def get_all_song(cur):
    try:
        cur.execute(GET_ALL_SONGS)
        row = cur.fetchone()

        while row is not None:
            print(row)
            row = cur.fetchone()

        cur.close()
    except (Exception, psycopg2.DatabaseError) as error:
        print(error)
