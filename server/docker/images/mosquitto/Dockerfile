FROM eclipse-mosquitto:2.0.18

COPY plugins /mosquitto/plugins

RUN set -x && \
    apk --no-cache add --virtual build-deps \
        build-base \
        cmake curl-dev cjson-dev; \
    find /mosquitto/plugins \( -name "CMakeFiles" -type d -o \
              -name "CMakeCache.txt" -o \
              -name "cmake_install.cmake" -o \
              -name "Makefile" -o \
              -name "*.so" \) -exec rm -rf {} +; \
    cd /mosquitto/plugins && \
    cmake -DCMAKE_BUILD_TYPE=Release && \
    cmake --build . && \
    cd /; \
    install -s -m755 \
        /mosquitto/plugins/mosquitto_external_auth/mosquitto_external_auth.so \
        /usr/lib/mosquitto_external_auth.so; \
    apk del build-deps; \
    apk add curl cjson; \
    rm -rf /mosquitto/plugins;

VOLUME ["/mosquitto/data", "/mosquitto/log"]

EXPOSE 1883
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["/usr/sbin/mosquitto", "-c", "/mosquitto/config/mosquitto.conf"]

#CMD ["sleep", "infinity"]
