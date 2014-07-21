#!/usr/bin/env python

import socket, time, threading, csv



TCP_IP = '10.10.10.1'
TCP_PORT = 5006
BUFFER_SIZE = 20  # Normally 1024, but we want fast response
counter = 0
last = 0

def check():
	global counter
	global last
	f = open('packet_counter.csv', 'a')
	w = csv.writer(f)
	data = [['Malicious2', counter - last]]
	w.writerows(data)
	f.close()
	last = counter
	#time.sleep(1)
	th = threading.Timer(1.0,check)
	th.start()
check()
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((TCP_IP, TCP_PORT))
s.listen(1)

conn, addr = s.accept()
print 'Connection address:', addr
while 1:
    data = conn.recv(BUFFER_SIZE)
    if not data: break
    counter += 1  
    print "received data:", data
conn.close()

