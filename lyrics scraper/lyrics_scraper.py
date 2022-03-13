#
# run as python lyrics_scraper.py sourcefile
#
from dataclasses import Field
from operator import contains
from urllib.request import Request, urlopen
from xml.dom import NOT_FOUND_ERR
from bs4 import BeautifulSoup, Comment
from tqdm import tqdm
import os, sys, csv, pstats, cProfile, time, yaml

CONFIGS_FILE = 'config.yml'
DOWNLOADS_FOLDER = 'downloads'
NAME_ERRORS_FILE = 'name_errors.tsv'
PARSE_ERRORS_FILE = 'parse_errors.tsv'
NOT_FOUND_FILE = 'not_found.tsv'
DOWNLOADED_FILE = 'downloaded.txt'
FIELD_NAMES = ['artist_name', 'track_name', 'release_date', 'genre', 'lyrics']

def init_configs(titles_file):
    cfg = {'total': 
                    {'current_batch_no': 1,
                    'current_batch_row_no': 0, 
                    'songs_downloaded': 0,
                    'parsing_errors': 0,
                    'songs_not_found': 0}, 
            'current_file':
                    {'file_name': titles_file,
                    'songs_downloaded': 0, 
                    'parsing_errors': 0,
                    'songs_not_found': 0}}
            
    with open(CONFIGS_FILE, "w+") as ymlfile:
        yaml.dump(cfg, ymlfile)
    return cfg

def load_configs(titles_file):
    with open(CONFIGS_FILE, 'r') as ymlfile:
        cfg = yaml.safe_load(ymlfile)
        if cfg['current_file']['file_name'] != titles_file:
            cfg['current_file'] = {'file_name': titles_file,
                    'songs_downloaded': 0, 
                    'parsing_errors': 0,
                    'songs_not_found': 0}
    return cfg

def save_configs(cfg):
    with open(CONFIGS_FILE, "w") as ymlfile:
        yaml.dump(cfg, ymlfile)

def get_csv_size(file):
    with open(file, 'r', encoding='utf-8') as f:
        size = len(f.readlines())-1
    return size

def create_csv_reader(file):
    f = open(file, 'r', newline='', encoding='utf-8')
    csv_reader = csv.DictReader(f, delimiter=',')  
    return csv_reader, f

def create_csv_writer(file):
    f = open (file, 'a', newline='', encoding='utf-8')
    csv_writer = csv.DictWriter(f, delimiter='\t', fieldnames = FIELD_NAMES)
    return csv_writer, f

def soup_from_url(url):
    search_req = Request(url, headers={'User-Agent': 'XYZ/3.0'})
    search_page = urlopen(search_req).read()
    return BeautifulSoup(search_page, features="html.parser")


def scrap(titles_reader, titles_size, downloaded, cfg, ne_writer,pe_writer, nf_writer):
    found=0
    cache = set()
    reformat = lambda str: str.replace(' ', '+').replace(',', '+')
    curr_batch = f"batch_{cfg['total']['current_batch_no']}.tsv"
    writer, f = create_csv_writer(curr_batch)
    if cfg['total']['current_batch_row_no'] == 0 and os.stat(curr_batch).st_size == 0:
        writer.writeheader()
 
    for line in tqdm(range(titles_size)):
        row = next(titles_reader)
        id = "-".join([row['artist_name'], row['track_name']])
        if id not in downloaded and id not in cache:
            try:
            
                soup = soup_from_url(f"https://search.azlyrics.com/search.php?q={reformat(row['artist_name'])}+{reformat(row['track_name'])}")      
                if 'Access denied' in str(soup):
                    print('Scraper got blocked')
                    quit()
                lyrics_url = soup.tr.a.get('href')


                time.sleep(60)
                soup = soup_from_url(lyrics_url)
                if 'Access denied' in str(soup):
                    print('Scraper got blocked')
                    quit()

                sub_soup = soup.find_all('div')[20]
                comments = sub_soup.find_all(string=lambda text: isinstance(text, Comment))
                for c in comments:
                    if c.startswith(' Usage of azlyrics.com'):
                        try:
                            lyrics = " ".join(sub_soup.get_text().splitlines())
                            writer.writerow({'artist_name':row['artist_name'],
                            'track_name':row['track_name'],
                            'release_date':row['release_date'],
                            'genre':row['genre'],
                            'lyrics':lyrics})
                            with open(DOWNLOADED_FILE, 'a') as f:
                                f.write(f'{id}\n')
                            cache.add(id)
                            found+=1
                        except:
                            ne_writer.writerow({'artist_name':row['artist_name'],
                            'track_name':row['track_name'],
                            'release_date':row['release_date'],
                            'genre':row['genre'],
                            'lyrics':row['lyrics']})
                        

                    else:
                        pe_writer.writerow({'artist_name':row['artist_name'],
                            'track_name':row['track_name'],
                            'release_date':row['release_date'],
                            'genre':row['genre'],
                            'lyrics':row['lyrics']})
            except:
                nf_writer.writerow({'artist_name':row['artist_name'],
                            'track_name':row['track_name'],
                            'release_date':row['release_date'],
                            'genre':row['genre'],
                            'lyrics':row['lyrics']})
        time.sleep(60)
    return found


if __name__ == '__main__':
    with cProfile.Profile() as pr:
        titles_file = sys.argv[1] ## titles/metadata folder
        titles_size = get_csv_size(titles_file)
        titles_reader, f = create_csv_reader(titles_file)
        headers = next(titles_reader)

        if not os.path.exists(DOWNLOADS_FOLDER):
            os.makedirs(DOWNLOADS_FOLDER)

        if not os.path.exists(CONFIGS_FILE):
            init_configs(titles_file)
            
        else:
            cfg = load_configs(titles_file)

        ne_writer, ne = create_csv_writer(NAME_ERRORS_FILE)
        pe_writer, pe = create_csv_writer(PARSE_ERRORS_FILE)
        nf_writer, nf = create_csv_writer(NOT_FOUND_FILE)

        if os.stat(NAME_ERRORS_FILE).st_size == 0:
            ne_writer.writeheader()
        if os.stat(PARSE_ERRORS_FILE).st_size == 0:
            pe_writer.writeheader()
        if os.stat(NOT_FOUND_FILE).st_size == 0:
            nf_writer.writeheader()

        with open(DOWNLOADED_FILE, 'w+', encoding='utf-8') as df:
            downloaded = set(df.read().splitlines())
        
        found = scrap(titles_reader, titles_size, downloaded, cfg, ne_writer,pe_writer, nf_writer)

        print(f"{found}/{titles_size-1}")
        f.close(), ne.close(), pe.close(), nf.close

    stats = pstats.Stats(pr)
    stats.sort_stats(pstats.SortKey.TIME)
    stats.print_stats()