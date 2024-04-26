#!/bin/bash

/opt/domains/zola/data/scripts/test/deploy.sh

echo ":::::: Dumping test Data into mysql"
export MYSQLCONTAINER=$(docker ps  | grep mysql | cut -d" " -f 1)
cat /opt/domains/zola/data/mysql/schema.sql | docker exec -i $MYSQLCONTAINER mysql -u root --password=admin
cat /opt/domains/zola/data/mysql/patch.sql | docker exec -i $MYSQLCONTAINER mysql -u root --password=admin zola

echo ":::::: Dumping test Data into cassandra"
export CASSYCONTAINER=$(docker ps  | grep cassandra | cut -d" " -f 1)
docker cp /opt/domains/zola/data/cassandra/. $CASSYCONTAINER:/db-schema
docker exec -i $CASSYCONTAINER cqlsh -f /db-schema/schema.cql
docker exec -i $CASSYCONTAINER cqlsh -f /db-schema/patch.cql

echo ":::::: Done ::::::" 
