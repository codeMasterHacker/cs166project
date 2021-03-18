#! /bin/bash

source ../postgresql/startPostgreSQL.sh
sleep 3

source ../postgresql/createPostgreDB.sh
sleep 3

source ./compile.sh
sleep 3

source ./run.sh $USER"_DB" $PGPORT $USER
