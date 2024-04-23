CREATE KEYSPACE IF NOT EXISTS zola WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

USE zola;

DROP TABLE IF EXISTS user_review;
CREATE TABLE user_review (
  user_id int,
  transaction_id varchar,
  insertion_time timestamp,
  rating int,
  business_name varchar,
  callback varchar,
  text_info varchar,
  author_name varchar,
  PRIMARY KEY (user_id, insertion_time, transaction_id)
) WITH CLUSTERING ORDER BY (insertion_time DESC);
CREATE INDEX user_review_user_id ON user_review(user_id);
CREATE INDEX user_review_rating ON user_review(rating);
CREATE INDEX user_review_business_name ON user_review(business_name);
CREATE INDEX user_review_author_name ON user_review(author_name);