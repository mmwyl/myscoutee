#!/bin/sh

# Set ptrace_scope to 0
#echo 0 > /proc/sys/kernel/yama/ptrace_scope

# Start Mosquitto
#/usr/sbin/mosquitto -c /mosquitto/config/mosquitto.conf &

#sleep 5 &

# Start gdbserver in the background
#/usr/bin/gdbserver --multi --attach --debug :8888 $(pidof mosquitto)

#COPY docker-entrypoint.sh /
#EXPOSE 1883 9229
#ENTRYPOINT ["/docker-entrypoint.sh"]
#CMD ["/bin/sh", "-c", "gdbserver --daemon :9229 /usr/sbin/mosquitto -c /mosquitto/config/mosquitto.conf"]
