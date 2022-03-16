from spotipy.oauth2 import SpotifyClientCredentials
import spotipy

GENERIC_GENRES = {"classical", "opera", "folk", "dance", "emo",
 "pop", "rock", "metal", "funk", "blues", "hip hop", "rap", "jazz", "country", "punk", "techno"}

class Client:
    def __init__(self):
        self.sp=spotipy.Spotify(client_credentials_manager=SpotifyClientCredentials())

    def get_genre(self, artist):
        try:
            genre = artist["genres"][0].lower()
            for g in artist["genres"]:
                g = g.lower()
                if g in GENERIC_GENRES:
                    genre = g
                    break
            return genre
        except:
            None

    def get_year(self, album):
        year = int(album["release_date"][:4])
        return year if year > 1400 and year < 3000 else None
    
    def search_metadata(self, query):
        try:
            results = self.sp.search(q=query, type="track", limit=20, offset=0, market="GB")
            track = results['tracks']['items'][0]
            return self.scrap_metadata(track)
        except:
            return None

    def scrap_metadata(self,track):
        try:
            artist = self.sp.artist(track["artists"][0]["external_urls"]["spotify"])
            artist_name = artist["name"]
            genre = self.get_genre(artist)
            album = self.sp.album(track["album"]["external_urls"]["spotify"])
            year = self.get_year(album)
            return artist_name, genre, year
        except:
            return None

    def scrap_new_titles(self):
        titles=[]
        releases = self.sp.new_releases(country=None, limit=50, offset=0)
        for r in releases["albums"]["items"]:
            artist = self.sp.artist(r["artists"][0]["id"])
            artist_name = artist["name"].lower()
            genre = self.get_genre(artist)
            year = self.get_year(r)
            if r["album_type"] == "album":
                alb = self.sp.album_tracks(r["external_urls"]["spotify"])                
                for track in alb["items"]:
                    track_name = track["name"].lower()
                    titles.append((artist_name, track_name, genre, year))
            elif r["album_type"] == "single":
                track_name = r["name"]
                titles.append((artist_name, track_name, genre, year))
        return titles

if __name__ == "__main__":
    c = Client()
    x = c.scrap_new_titles()
    print(x)
    print(len(x))