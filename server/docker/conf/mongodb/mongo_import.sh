#! /bin/bash
ls -1 /docker-entrypoint-initdb.d/myscoutee_db/*.json | while read jsonfile; do mongoimport -h 127.0.0.1 -d myscoutee_db -u guest -p guest --mode=upsert --file $jsonfile --jsonArray --type json; done
