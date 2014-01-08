(ns concurrent.futures
  (:require [concurrent.primes :as primes]))


(time (apply + (take-while #(< % 2e6) (primes/prime-seq))))

(time (println (map deref (map #(future (nth (primes/prime-seq) (* % 1e5))) [1 1 1 1]))))
(time (println (let [p (primes/prime-seq)] (map deref (map #(future (nth p (* % 1e5))) [1 1 1 1])))))
(time (println (map #(nth (prime-seq) (* % 1e5)) [1 1 1 1])))
