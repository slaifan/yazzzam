from urllib.request import Request, urlopen
from bs4 import BeautifulSoup, Comment
from tqdm import tqdm
import os, sys, csv, pstats, cProfile


def get_csv_size(file):
    with open(file, 'r', encoding='utf-8') as f:
        size = len(f.readlines())
    return size

def csv_reader(file):
    f = open(file, 'r', newline='', encoding='utf-8')
    csv_reader = csv.reader(f, delimiter=',')  
    return csv_reader, f

def csv_writerow(file, row):
    with open (file, 'a', newline='', encoding='utf-8') as f:
        csv_writer = csv.writer(f, delimiter=',')
        csv_writer.writerow(row)

def soup_from_url(url):
    search_req = Request(url, headers={'User-Agent': 'XYZ/3.0'})
    search_page = urlopen(search_req).read()
    return BeautifulSoup(search_page, features="html.parser")


def scrap(titles_reader, downloads_file, downloads, size):
    found=0
    cache = {}
    reformat = lambda str: str.replace(' ', '+').replace(',', '+')
    for line in tqdm(range(size-1)):
        row = next(titles_reader)
        str_row = ",".join(row)
        if str_row not in downloads and str_row not in cache:
            try:
            
                soup = soup_from_url(f"https://search.azlyrics.com/search.php?q={reformat(row[0])}+{reformat(row[1])}")      
                
                lyrics_url = soup.tr.a.get('href')

                soup = soup_from_url(lyrics_url)

                sub_soup = soup.find_all('div')[20]
                comments = sub_soup.find_all(string=lambda text: isinstance(text, Comment))
                for c in comments:
                    if c.startswith(' Usage of azlyrics.com'):
                        try:
                            with open(f'downloads/{row[0]} - {row[1]}.txt', 'w') as f:
                                f.write(sub_soup.get_text().strip())
                            csv_writerow(downloads_file, row)
                            cache.add(str_row)
                            found+=1
                        except:
                            csv_writerow("name_errors.csv", row)
                        

                    else:
                        csv_writerow("parse_errors.csv", row)
            except:
                csv_writerow("notfound.csv", row)
    return found


if __name__ == '__main__':
    with cProfile.Profile() as pr:
        titles_file = sys.argv[1]
        downloads_file = sys.argv[2]
        size = get_csv_size(titles_file)
        titles_reader, f = csv_reader(titles_file)
        headers = next(titles_reader)

        if not os.path.exists(downloads_file):
            csv_writerow(downloads_file, headers)

        if not os.path.exists('downloads'):
            os.makedirs('downloads')

        with open(downloads_file, encoding='utf-8') as df:
            downloads = set(df.read().splitlines())

        found = scrap(titles_reader, downloads_file, downloads, size)

        print(f"{found}/{size-1}")
        f.close()
    stats = pstats.Stats(pr)
    stats.sort_stats(pstats.SortKey.TIME)
    stats.print_stats()