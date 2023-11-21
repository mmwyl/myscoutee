#! /bin/bash

while true; do
    inotifywait -r -e modify,create,delete /etc/nginx
    nginx -s reload
done &

if [[ -z "$WAIT_FOR" ]]; then 
    nginx -g 'daemon off;'; 
else 
    /scripts/wait-for-it.sh -t 0 $WAIT_FOR -- nginx -g 'daemon off;';
fi