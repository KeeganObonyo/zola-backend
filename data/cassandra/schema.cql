CREATE KEYSPACE IF NOT EXISTS elmer WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

USE zola;

DROP TABLE IF EXISTS user_review_matrices;
CREATE TABLE user_review_matrices (
  transaction_id varchar,
  insertion_time timestamp,
  rating int,
  business_name varchar,
  callback varchar,
  text_info varchar,
  author_name varchar,
  PRIMARY KEY (business_name, insertion_time)
) WITH CLUSTERING ORDER BY (insertion_time DESC);
