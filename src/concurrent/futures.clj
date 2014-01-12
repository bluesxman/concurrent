(ns concurrent.futures
  (:require [concurrent.primes :refer :all]))


(time (apply + (take-while #(< % 2e6) (primes/prime-seq))))

(time (println (map deref (map #(future (nth (primes/prime-seq) (* % 3e5))) [1 1 1 1]))))
(time (println (let [p (primes/prime-seq)] (map deref (map #(future (nth p (* % 3e5))) [1 1 1 1])))))
(time (println (map #(nth (primes/prime-seq) (* % 3e5)) [1 1 1 1])))

