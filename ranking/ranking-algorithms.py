import math
import numpy as np

"""
All algorithms in the paper Trotmam et al, Improvements to BM25 and Language Models Examined.
BM25 algorithms taken from https://github.com/dorianbrown/rank_bm25/blob/master/rank_bm25.py

"""

class Rank:
    def __init__(self, index):
        self.index_size = len(index)
        self.avgdl = 0
        self.doc_freqs = []
        self.idf = {}
        self.doc_len = []

        nd = self._initialize(index)
        self._calc_idf(nd)

    def _initialize(self, index):
        nd = {}  # word -> number of documents with word
        num_doc = 0
        for document in index:
            self.doc_len.append(len(document))
            num_doc += len(document)

            frequencies = {}
            for word in document:
                if word not in frequencies:
                    frequencies[word] = 0
                frequencies[word] += 1
            self.doc_freqs.append(frequencies)

            for word, freq in frequencies.items():
                try:
                    nd[word]+=1
                except KeyError:
                    nd[word] = 1

        self.avgdl = num_doc / self.index_size
        return nd

    def _calc_idf(self, nd):
        raise NotImplementedError()

    def get_scores(self, query):
        raise NotImplementedError()

    def get_batch_scores(self, query, doc_ids):
        raise NotImplementedError()

    def get_top_n(self, query, documents, n=5):

        assert self.index_size == len(documents), "The documents given don't match the index!"

        scores = self.get_scores(query)
        top_n = np.argsort(scores)[::-1][:n]
        return [documents[i] for i in top_n]


class BM25Okapi(Rank):
    def __init__(self, index, k1=1.5, b=0.75, epsilon=0.25):
        self.k1 = k1
        self.b = b
        self.epsilon = epsilon
        super().__init__(index)

    def _calc_idf(self, nd):
        #In order to avoid negative idf values a floor is set with the help of epsilon * average_idf
        # collect idf sum to calculate an average idf for epsilon value
        idf_sum = 0
        # collect words with negative idf to set them a special epsilon value.
        # idf can be negative if word is contained in more than half of documents
        negative_idfs = []
        for word, freq in nd.items():
            idf = math.log(self.index_size - freq + 0.5) - math.log(freq + 0.5) # idf = log ((N - df_t + 0.5)/(df_t + 0.5)) in the paper Trotmam et al, Improvements to BM25 and Language Models Examined.
            self.idf[word] = idf
            idf_sum += idf
            if idf < 0:
                negative_idfs.append(word)
        self.average_idf = idf_sum / len(self.idf)

        eps = self.epsilon * self.average_idf
        for word in negative_idfs:
            self.idf[word] = eps

    def get_scores(self, query):
        """
        The ATIRE BM25 variant uses a derivation of idf that allows for negative values, to prevent these negative idf scores, 
        the algorithm adds a floor to the idf value of epsilon.

        """
        score = np.zeros(self.index_size)
        doc_len = np.array(self.doc_len)
        for q in query:
            q_freq = np.array([(doc.get(q) or 0) for doc in self.doc_freqs])  # tf_td in the paper # tf_td in the paper Trotmam et al, Improvements to BM25 and Language Models Examined.
            score += (self.idf.get(q) or 0) * (q_freq * (self.k1 + 1) /
                                               (q_freq + self.k1 * (1 - self.b + self.b * doc_len / self.avgdl)))
        return score

    def get_batch_scores(self, query, doc_ids):
        assert all(di < len(self.doc_freqs) for di in doc_ids)
        score = np.zeros(len(doc_ids))
        doc_len = np.array(self.doc_len)[doc_ids]
        for q in query:
            q_freq = np.array([(self.doc_freqs[di].get(q) or 0) for di in doc_ids]) 
            score += (self.idf.get(q) or 0) * (q_freq * (self.k1 + 1) /
                                               (q_freq + self.k1 * (1 - self.b + self.b * doc_len / self.avgdl)))
        return score.tolist()


class BM25L(Rank):
    def __init__(self, index, k1=1.5, b=0.75, delta=0.5):
        # A new value of IDF was derived in BM25L which disallows negative values
        self.k1 = k1
        self.b = b
        self.delta = delta
        super().__init__(index)

    def _calc_idf(self, nd):
        for word, freq in nd.items():
            idf = math.log(self.index_size + 1) - math.log(freq + 0.5) # idf = log ((N+1)/(df_t + 0.5)) in the paper Trotmam et al, Improvements to BM25 and Language Models Examined. This disallows negative values when compared to BM25
            self.idf[word] = idf

    def get_scores(self, query):
        score = np.zeros(self.index_size)
        doc_len = np.array(self.doc_len)
        for q in query:
            q_freq = np.array([(doc.get(q) or 0) for doc in self.doc_freqs])  # tf_td in the paper Trotmam et al, Improvements to BM25 and Language Models Examined.
            ctd = q_freq / (1 - self.b + self.b * doc_len / self.avgdl)  # c_td = tf_td/(1-b+b*(L_d/L_avg)) given in the paper Trotmam et al, Improvements to BM25 and Language Models Examined.
            score += (self.idf.get(q) or 0) * q_freq * (self.k1 + 1) * (ctd + self.delta) / \
                     (self.k1 + ctd + self.delta)
        return score

    def get_batch_scores(self, query, doc_ids):
        assert all(di < len(self.doc_freqs) for di in doc_ids)
        score = np.zeros(len(doc_ids))
        doc_len = np.array(self.doc_len)[doc_ids]
        for q in query:
            q_freq = np.array([(self.doc_freqs[di].get(q) or 0) for di in doc_ids]) # tf_td in the paper Trotmam et al, Improvements to BM25 and Language Models Examined.
            ctd = q_freq / (1 - self.b + self.b * doc_len / self.avgdl) # c_td = tf_td/(1-b+b*(L_d/L_avg)) given in the paper Trotmam et al, Improvements to BM25 and Language Models Examined.
            score += (self.idf.get(q) or 0) * q_freq * (self.k1 + 1) * (ctd + self.delta) / \
                     (self.k1 + ctd + self.delta)
        return score.tolist()


class BM25Plus(Rank):
    def __init__(self, index, k1=1.5, b=0.75, delta=1):
        #In this algorithm, Lv and Zhai lower bound the contribution of a single term occurence with the help of delta.
        self.k1 = k1
        self.b = b
        self.delta = delta
        super().__init__(index)

    def _calc_idf(self, nd):
        for word, freq in nd.items():
            idf = math.log((self.index_size + 1) / freq) # idf = log ((N+1)/(df_t)) in the paper Trotmam et al, Improvements to BM25 and Language Models Examined. IDF calculated differently from BM25 and BM25L
            self.idf[word] = idf

    def get_scores(self, query):
        score = np.zeros(self.index_size)
        doc_len = np.array(self.doc_len)
        for q in query:
            q_freq = np.array([(doc.get(q) or 0) for doc in self.doc_freqs]) # tf_td in the paper Trotmam et al, Improvements to BM25 and Language Models Examined.
            score += (self.idf.get(q) or 0) * (self.delta + (q_freq * (self.k1 + 1)) /
                                               (self.k1 * (1 - self.b + self.b * doc_len / self.avgdl) + q_freq))  # Different from BM25L, delta is added to the tf_td component
        return score

    def get_batch_scores(self, query, doc_ids):
        assert all(di < len(self.doc_freqs) for di in doc_ids)
        score = np.zeros(len(doc_ids))
        doc_len = np.array(self.doc_len)[doc_ids]
        for q in query:
            q_freq = np.array([(self.doc_freqs[di].get(q) or 0) for di in doc_ids]) # tf_td in the paper Trotmam et al, Improvements to BM25 and Language Models Examined.
            score += (self.idf.get(q) or 0) * (self.delta + (q_freq * (self.k1 + 1)) /
                                               (self.k1 * (1 - self.b + self.b * doc_len / self.avgdl) + q_freq))
        return score.tolist()

class tf_l_delta_p_idf(Rank):
    # Uses the heuristic that nonlinear gan from an additional occurence of a term in a document should be modeled using a log function.
    def __init__(self,index,b=0.75,delta=1.):
        self.b = b
        self.delta = delta
        super().__init__(index)

    def _calc_idf(self,nd):
        for word,freq in nd.items():
            idf = math.log((self.index_size + 1) / freq) # idf = log ((N+1)/(df_t)) IDF calculated as in the BM25Plus algorithm
            self.idf[word] = idf

    def get_scores(self, query):
        #tf_l_td = 1 + ln(tf_td)
        #tf_delta_td = tf_td + delta
        #tf_p_td = tf_td/(1 - b + b*(L_d/L_avg))
        #Combination according to Rousseau & Vazirgiannis - 
        #tf_l_delta_p = 1 + ln(1 + ln(tf_p_td + delta))

        score = np.zeros(self.index_size)
        doc_len = np.array(self.doc_len)
        for q in query:
            q_freq = np.array([(doc.get(q) or 0) for doc in self.doc_freqs]) # tf_td in the paper Trotmam et al, Improvements to BM25 and Language Models Examined.
            score += (self.idf.get(q) or 0) * (1 + np.log(1 + np.log((q_freq/(1 - self.b + self.b * doc_len / self.avgdl)) + self.delta)))

        return score

    def get_batch_scores(self, query, doc_ids):
        assert all(di < len(self.doc_freqs) for di in doc_ids)
        score = np.zeros(len(doc_ids))
        doc_len = np.array(self.doc_len)[doc_ids]
        for q in query:
            q_freq = np.array([(self.doc_freqs[di].get(q) or 0) for di in doc_ids]) # tf_td in the paper Trotmam et al, Improvements to BM25 and Language Models Examined.
            score += (self.idf.get(q) or 0) * (1 + np.log(1 + np.log((q_freq/(1 - self.b + self.b * doc_len / self.avgdl)) + self.delta)))
        return score.tolist()