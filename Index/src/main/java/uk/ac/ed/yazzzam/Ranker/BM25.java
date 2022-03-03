package uk.ac.ed.yazzzam.Ranker;

import java.util.*;
public class BM25 {

    private double k1 = 1.5;
    private double epsilon = 0.25;
    private double b = 0.75;
    private List<String> query = new ArrayList<String>();

    private int idfSum = 0;
    private Map<String, Integer> idfVals = new HashMap<String, Integer>();
    private List<String> negativeIDFs = new ArrayList<Integer>();
    private int avgIDF = 0;

    public BM25(double k1, double epsilon, double b, List<String> query) {
        this.k1 = k1;
        this.epsilon = epsilon;
        this.b = b;
        this.query = query;
    }

    public Map<String, Integer> calcIDF(Integer numDocs, Map<String, Integer> docFreqs) {
        for (String term : docFreqs.keySet()) {
            for (Integer freq : docFreqs.get(term)) {
                var idf = Math.log(numDocs - freq + 0.5) - Math.log(freq + 0.5);
                idfVals.put(term, idf);
                idfSum += idf;

                if (idf < 0){
                    negativeIDFs.add(term);
                }
            }
        }
        avgIDF = idfSum / idfVals.size();
        var eps = epsilon * avgIDF;
        for(String term:negativeIDFs){
            idfVals.replace(term, idfVals.get(term), eps);
        }
        return idfVals;
    }

    public int[] getScore(Map<Integer, Map<String, Integer>> docTermFreqs, Integer numDocs, int numWords, int[] numWordsPerDoc){
        var scores = 0;
        int [] q_freq = new int[docTermFreqs.size()];
        int [] score = new int[numDocs];
        var avgdl = numWords/numDocs ;

        for(String q : this.query){
            for (Integer doc: docTermFreqs.keySet()){
                if(docTermFreqs.get(doc).containsKey(q)){
                    q_freq[docTermFreqs.indexOf(q)] = docTermFreqs.get(doc).get(q);
                }
            }
            for(int i = 0; i < q_freq.length; i++){
                score[i] = this.idfVals.get(q_freq[i]) * (q_freq[i] * (this.k1 + 1)) / (q_freq[i] + (this.k1) * (1 - this.b + this.b * numWordsPerDoc[i] / avgdl));
            }
        }
        return score;
    }


    
}
