package uk.ac.ed.yazzzam.Search;

import uk.ac.ed.yazzzam.GlobalSettings;
import uk.ac.ed.yazzzam.WebServer.SearchResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PhraseSearch {
    // input: preprocessed query and inverted index
    public HashSet<Integer> search(List<String> words) {
        var res = new HashSet<Integer>();
        Set<Integer> docs = GlobalSettings.getIndex().getInvertedIndex().get(words.get(0)).getPostingsList().keySet();
        for (int i = 0; i < words.size(); i++) {
            var ds = GlobalSettings.getIndex().getInvertedIndex().get(words.get(i)).getPostingsList().keySet();
            docs.retainAll(ds);
        }
        for (int i : docs) {
            var wordPositions = new ArrayList<HashSet<Integer>>();
            for (int wordIdx = 0; wordIdx < words.size(); wordIdx++) {
                 var pos = GlobalSettings
                         .getIndex()
                         .getInvertedIndex()
                         .get(words.get(wordIdx))
                         .getPostingsList()
                         .get(i)
                         .getPositions()
                         .stream()
                         .collect(Collectors.toSet());
                 wordPositions.add((HashSet<Integer>) pos);
            }
            for (int expectedPosition : wordPositions.get(0)) {
                for (int j = 1; j < words.size(); j++) {
                    if (!wordPositions.get(j).contains(expectedPosition + j)) {
                        break;
                    } else if (j == words.size() - 1) {
                        res.add(i);
                    }
                }
            }
        }
        return res;
    }

    public static HashSet<Integer> search(List<String> words, Set<Integer> filteredIds) {
        var res = new HashSet<Integer>();
        Set<Integer> docs = GlobalSettings.getIndex().getInvertedIndex().get(words.get(0)).getPostingsList().keySet();
        docs.retainAll(filteredIds);
        for (int i = 0; i < words.size(); i++) {
            var ds = GlobalSettings.getIndex().getInvertedIndex().get(words.get(i)).getPostingsList().keySet();
            docs.retainAll(ds);
        }
        for (int i : docs) {
            var wordPositions = new ArrayList<HashSet<Integer>>();
            for (int wordIdx = 0; wordIdx < words.size(); wordIdx++) {
                var pos = GlobalSettings
                        .getIndex()
                        .getInvertedIndex()
                        .get(words.get(wordIdx))
                        .getPostingsList()
                        .get(i)
                        .getPositions()
                        .stream()
                        .collect(Collectors.toSet());
                wordPositions.add((HashSet<Integer>) pos);
            }
            for (int expectedPosition : wordPositions.get(0)) {
                for (int j = 1; j < words.size(); j++) {
                    if (!wordPositions.get(j).contains(expectedPosition + j)) {
                        break;
                    } else if (j == words.size() - 1) {
                        res.add(i);
                    }
                }
            }
        }
        return res;
    }

    public ArrayList<SearchResult> getResults(String query) {
        var q = GlobalSettings.getPreprocessor().preprocess(query);
        var docs = search(q);
        return (ArrayList<SearchResult>) docs.stream().map(d -> new SearchResult(d, query)).collect(Collectors.toList());
    }

    public ArrayList<SearchResult> getResults(String lyrics, Set<Integer> filteredIds) {
        var q = GlobalSettings.getPreprocessor().preprocess(lyrics);
        var docs = search(q, filteredIds);
        System.out.println(docs.stream().collect(Collectors.toList()));
        return (ArrayList<SearchResult>) docs.stream().map(d -> new SearchResult(d, lyrics)).collect(Collectors.toList());
    }
}
