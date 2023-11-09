# myscoutee-auth

docker run -it alpine:2.0.18-debug /bin/sh

target extended-remote localhost:8888 (extended-remote does not stop the gdbserver, remote does)

tar -xvzf gdb-13.1.tar.gz

$ ./configure --enable-languages=c,c++ --disable-multilib
$ make -j$(nproc)

mosquitto_sub -t 'test/topic' -v
mosquitto_pub -t 'test/topic' -m 'hello world'

export CC=gcc

bug:

1) alpine linux uses mus-libc, ubuntu is libc, hence with gdb it won't work,
but sharing the container built one, the debug information can be used in the launch configuration!
2) executables are different, alpine linux can't run an ubuntu generated one
3) gdbserver can run outside of the container there is no difference

Dvelve is a go debugger (cgo is a c for go)
go get github.com/go-delve/delve/cmd/dlv
go build -gcflags "all=-N -l" -o mylib.so -buildmode=c-shared mylib.go
dlv --listen=:2345 --headless=true --api-version=2 --log mylib.so
CMD ["gdbserver", "0.0.0.0:12345", "/app/my_c_program"] && \
    ["dlv", "--listen=:2345", "--headless=true", "--api-version=2", "--log", "/app/my_go_code"]
