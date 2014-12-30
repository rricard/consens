(ns consens.protocol
  "General purpose distributed system I/O protocol")

(defprotocol IConsens
  "Define a Consens node"
  (rd [k] "Read a key and return the data immediately")
  (wr [k d] "Try to write data in a key in the whole system"))
