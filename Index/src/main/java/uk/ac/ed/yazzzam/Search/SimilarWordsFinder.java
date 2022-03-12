package uk.ac.ed.yazzzam.Search;

import java.util.*;
import java.util.stream.Collectors;

public class SimilarWordsFinder {
    private TrieNode root;
    private HashMap<Integer, String> translation;

    public SimilarWordsFinder(Set<String> keys) {
        root = new TrieNode();
        translation = new HashMap<>();
        var val = 0;
        for (String key: keys) {
            insert(root, key, val);
            translation.put(val, key);
            val++;
        }
    }

    public Set<String> findSimilarWords(String word, int threshold) {
        var res = new HashSet<Integer>();
        fuzzySearch(root, word, threshold, res);
        return res.stream().map(e -> translation.get(e)).collect(Collectors.toSet());
    }

    private static void insert(TrieNode trie, String word, int value) {
        if (word.length() == 0) {
            trie.val = value;
            trie.isEndOfWord = true;
            return;
        }
        if (!trie.children.containsKey(word.charAt(0))) {
            trie.children.put(word.charAt(0), new TrieNode());
        }
        insert(trie.children.get(word.charAt(0)), substringSafe(word, 1), value);
    }

    private static int search(TrieNode trie, String word) {
        if (trie.isEndOfWord && word.length() == 0) {
            return trie.val;
        }
        else if (!trie.isEndOfWord && word.length() == 0) {
            return -1;
        }
        else if (!trie.children.containsKey(word.charAt(0))) {
            return -1;
        }
        return search(trie.children.get(word.charAt(0)), substringSafe(word, 1));
    }

    private static HashSet<Integer> fuzzySearch(TrieNode trie, String word, int threshold, HashSet<Integer> resultSet) {
        if (threshold == 0) {
            var res = search(trie, word);
            if (res != -1) {
                resultSet.add(res);
            }
            return resultSet;
        }

        if (trie.isEndOfWord && word.length() <= threshold) {
            resultSet.add(trie.val);
        }

        for (Map.Entry<Character, TrieNode> entry : trie.children.entrySet()) {
            var k = entry.getKey();
            var child = entry.getValue();
            if (word.length() == 0) {
                fuzzySearch(child, word, threshold - 1, resultSet);
            }
            else if (k == word.charAt(0)) {
                fuzzySearch(child, substringSafe(word, 1), threshold, resultSet);
            }
            else {
                fuzzySearch(child, word, threshold - 1, resultSet);
                fuzzySearch(child, substringSafe(word, 1), threshold - 1, resultSet);
            }
        }
        return resultSet;
    }


    public int getRelevantWords(List<String> query, Song song, int windowSize){
        var docLen = song.lyrics.length();
        var idxScores = new HashMap<Integer,Integer>(); // For mapping first index of window to the max score of window
        var idxBestWindow = 0; // First index of window with the highest score
        for(int left = 0; left <= docLen - windowSize; left++){
            for(int right = left; right < left + windowSize; right++) {
                if (right = left + windowSize - 1) { //When window size is reached, we can check the occurrence of the words in the query in the given window
                    var windowString = substringSafeStartEnd(song.lyrics,left,right); //Creates the substring of the window 
                    var score = 0; //Unique score for each window
                    for (String word : query){ //Loops through every word in query
                        if (windowString.contains(word)) { // If word in query is contained in the window string, we count the number of occurrences of the word in the window
                            var lastIndex = 0;
                            var count = 0;
                            while (lastIndex != -1) {
                                lastIndex = windowString.indexOf(word, lastIndex); //Updates the value of the last index until all occurrences of the word are found, after the final occurrence is counted, indexOf returns -1 after which we stop counting
                                if (lastIndex != -1) {
                                    count++; //Unique count for each word in query for each window
                                    lastIndex += word.length();
                                }
                            }
                            score += count; // Count of each word in query is added to the score of each window
                        }
                    }
                    idxScores.put(left,score); // Mapping starting index of window to score of window
                }
            }
        }
        var maxWindowScore = (Collections.max(idxScores.values())); //Calculating the maximum score
        for(Entry<Integer,Integer> entry:idxScores) {
            if (entry.getValue() == maxWindowScore) {
                idxBestWindow = entry.getKey(); // Starting index of window with the highest score
            }
        }
        return idxBestWindow;
    }

    /*
    in java doing the equivalent of word[1:] throws NullPointerException,
    this is a safe way to take substring if possible or return empty string otherwise
     */
    private static String substringSafe(String word, int start) {
        return word.length() > 1 ? word.substring(start) : "";
    }

    private static String substringSafeStartEnd(String word, int start, int end){
        return word.length() > 1 ? word.substring(start,end) : "";
    }

    private static class TrieNode {
        private int val;
        private HashMap<Character, TrieNode> children;
        private boolean isEndOfWord;

        public TrieNode() {
            val = -1;
            children = new HashMap<>();
            isEndOfWord = false;
        }
    }
}
