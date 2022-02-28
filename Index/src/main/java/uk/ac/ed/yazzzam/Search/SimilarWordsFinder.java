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

    /*
    in java doing the equivalent of word[1:] throws NullPointerException,
    this is a safe way to take substring if possible or return empty string otherwise
     */
    private static String substringSafe(String word, int start) {
        return word.length() > 1 ? word.substring(start) : "";
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
