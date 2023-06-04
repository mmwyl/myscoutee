#! /bin/bash

#while :; do 
#    sleep 6h & wait $${!}; nginx -s reload; 
#done &

if [[ -z "$WAIT_FOR" ]]; then 
    nginx -g 'daemon off;'; 
else 
    /scripts/wait-for-it.sh -t 0 $WAIT_FOR -- nginx -g 'daemon off;';
fi