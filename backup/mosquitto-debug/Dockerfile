FROM alpine:3.18

RUN set -x && \
    apk update && apk --no-cache add --virtual build-deps \
        build-base \
        cmake \
        make \
        cjson-dev \
        gnupg \
        libressl-dev \
        linux-headers \
        util-linux-dev \
        gdb \
        libwebsockets-dev;

COPY start.sh /

VOLUME ["/mosquitto/data", "/mosquitto/log"]

EXPOSE 1883

CMD ["sleep", "infinity"]
