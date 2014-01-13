(ns concurrent.futures
  (:require [concurrent.primes :refer :all]))

(time (apply + (take-while #(< % 2e6) (prime-seq))))

(time (println (map deref (map #(future (nth (prime-seq) (* % 1e5))) [1 1 1 1]))))
(time (println (let [p (prime-seq)] (map deref (map #(future (nth p (* % 1e5))) [1 1 1 1])))))
(time (println (map #(nth (prime-seq) (* % 1e5)) [1 1 1 1])))

