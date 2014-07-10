#!/usr/local/bin/rubydir/bin/ruby

require 'socket'

socket = Socket.new(Socket::PF_INET,Socket::SOCK_RAW,147)
socket.send("test",0, Socket.pack_sockaddr_in(0,"10.10.10.2"))
socket.close
