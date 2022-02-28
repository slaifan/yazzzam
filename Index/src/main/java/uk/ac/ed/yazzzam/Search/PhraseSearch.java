package uk.ac.ed.yazzzam.Search;

import uk.ac.ed.yazzzam.index.InvertedIndex;
import uk.ac.ed.yazzzam.index.postinglists.ProximityPostingListIterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PhraseSearch {
    // input: preprocessed query and inverted index
    public static HashSet<Integer> Search(List<String> words, InvertedIndex index) {
        var postingLists = new ArrayList<ProximityPostingListIterator>();
        for (String word: words) {
            postingLists.add(index.getPostingList(word));
        }
        return getRelevantDocuments(postingLists);
    }

    private static HashSet<Integer> getRelevantDocuments(List<ProximityPostingListIterator> postingLists) {
        var res = new HashSet<Integer>();
        for (int i=1; i < postingLists.size(); i++) {
            postingLists.get(i).moveToNextDocument(); // initialise
        }
        while (postingLists.get(0).moveToNextDocument()) {
            if (postingLists.size() == 1) {
                res.add(postingLists.get(0).getCurrentDocument());
            }
            else {
                var goal = postingLists.get(0).getCurrentDocument();
                for (int i = 1; i < postingLists.size(); i++) {
                    var lst = postingLists.get(i);
                    while (lst.getCurrentDocument() < goal) {
                        var hasNext = lst.moveToNextTermPosition();
                        if (!hasNext) {
                            break;
                        }
                    }
                    if (lst.getCurrentDocument() > goal) {
                        break;
                    }
                    if (lst.getCurrentDocument() == goal && i == postingLists.size() - 1) {
                        if (phraseExists(postingLists)) {
                            res.add(goal);
                        }
                    }
                }
            }
        }
        return res;
    }

    private static boolean phraseExists(List<ProximityPostingListIterator> postingLists) {
        for (int i=1; i < postingLists.size(); i++) {
            postingLists.get(i).moveToNextTermPosition(); // initialise
        }
        while (postingLists.get(0).moveToNextTermPosition()) {
            for (int i=1; i < postingLists.size(); i++) {
                var lst = postingLists.get(i);
                var goal = postingLists.get(0).getCurrentTermPosition() + i;
                while (lst.getCurrentTermPosition() < goal) {
                    var hasNext = lst.moveToNextTermPosition();
                    if (!hasNext) {
                        return false;
                    }
                }
                if (lst.getCurrentTermPosition() > goal) {
                    break;
                }
                if (lst.getCurrentTermPosition() == goal && i == postingLists.size() - 1) {
                    return true;
                }
            }
        }
        return false;
    }
}
