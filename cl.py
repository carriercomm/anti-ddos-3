#!/usr/bin/env python

import socket , time


TCP_IP = '10.10.10.1'
TCP_PORT = 5006
BUFFER_SIZE = 1024
MESSAGE = "from ma1"

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((TCP_IP, TCP_PORT))
while True:
	s.send(MESSAGE)
	time.sleep(1)
s.close()

