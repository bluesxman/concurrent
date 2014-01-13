(ns concurrent.primes)

;; Based on Michelle O'Reilly's "The Genuine Sieve of Eratosthenes"
;; http://www.cs.hmc.edu/~oneill/papers/Sieve-JFP.pdf
;;
;; Using a tree (insead of a heap) as on page 6 since clojure trees are log32(n) (i.e. fast) and they're default clojure
;; A priority queue for clojure is here: https://github.com/clojure/data.priority-map
;;
;; Furthermore, optimizing by the algorithm by not doing a multi-map of composites to primes/pointers.
;; Instead, will keep incrementing the pointer until we find a spot in the map where its empty (i.e.
;; the composite does not map to a prime).  Then we assoc the composite with that prime.
;;
;; No wheel optimization, just skipping even candidates

;; 1) Get the prime at n (the composite)
;; 2) Find the next composite the prime would divide into
;; 3) If there's no entry in the table, map the next composite to that prime, remove n from the table, and return this new table.
;; 4) Else goto step 2
(defn- update-composites
  [table n]
  (let [prime (table n)
        incr (* 2 prime)]
    (loop [nxt-comp (+ n incr)]
      (if (table nxt-comp)
        (recur (+ nxt-comp incr))
        (dissoc (assoc table nxt-comp prime) n)))))

;; 1) Take the next candidate from the sequence.
;; 2) If it's in the table then its a composite.
;; 3) If it's a composite then skip it, update the table, and recurse.
;; 4) Else it's a prime so add it to the sequence, add the prime to the table, and recurse.
;; 5) The first possible composite of the prime is its square so use this as the key.
;; 6) The value in the table is the prime.
(defn- inc-sieve
  [num-seq table]
  (let [n (first num-seq)]
    (if (contains? table n)
      (inc-sieve (rest num-seq) (update-composites table n))
      (cons n (lazy-seq (inc-sieve (rest num-seq) (assoc table (* n n) n)))))))

(defn prime-seq
  "Creates infinite sequence of primes using incremental functional sieve."
  []
  (cons 2 (inc-sieve (iterate #(+ 2 %) 3) {})))
