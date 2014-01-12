(ns concurrent.primes)

;; Based on Michelle O'Reilly's "The Genuine Sieve of Eratosthenes"
;; http://www.cs.hmc.edu/~oneill/papers/Sieve-JFP.pdf
;; Using a tree (insead of a heap) as on page 6 since clojure trees are log32(n) (i.e. fast) and they're default clojure
;; A priority queue for clojure is here: https://github.com/clojure/data.priority-map
;; No wheel optimization, just skipping even candidates

;; 1) Get the list of primes at n.
;; 2) From that list create a new list of keys by adding each prime to n.
;; 3) For each new key, take the list in the table at that location (if it exists) and append the prime to the list.
;; 4) Remove n from the table...
;; 5) and add the new key-value pairs to the table.
(defn- update-composites
  [table n]
  (let [primes (table n)
        iter #(+ n (* 2 %))
        new-keys (map iter primes)
        new-vals (map #(conj (table (iter %)) %) primes)]
    (apply assoc (dissoc table n) (interleave new-keys new-vals))))

;; 1) Take the next candidate from the sequence.
;; 2) If it's in the table then its a composite.
;; 3) If it's a composite then skip it, update the table, and recurse.
;; 4) Else it's a prime so add it to the sequence, add the prime to the table, and recurse.
;; 5) The first possible composite of the prime is its square so use this as the key.
;; 6) The value in the table is a list containing the prime.  Since eventually "iterators"
;; for primes can resolve to the same key in the table, >1 prime can be at the same
;; key in the table.  Therefore values in the table are lists of primes.
(defn- inc-sieve
  [num-seq table]
  (let [n (first num-seq)]
    (if (contains? table n)
      (inc-sieve (rest num-seq) (update-composites table n))
      (cons n (lazy-seq (inc-sieve (rest num-seq) (assoc table (* n n) (list n))))))))

(defn prime-seq
  "Creates infinite sequence of primes using incremental functional sieve."
  []
  (cons 2 (inc-sieve (iterate #(+ 2 %) 3) {})))


(defn nth-prime
  "Finds the nth prime without using sequences"
  [n]
  (if (= n 1)
    2
    (loop [table {}
           i 3
           cnt 2]
      (if (contains? table i)
        (recur (update-composites table i) (+ i 2) cnt)
        (if (= n cnt)
          i
          (recur (assoc table (* i i) (list i)) (+ i 2) (inc cnt)))))))
