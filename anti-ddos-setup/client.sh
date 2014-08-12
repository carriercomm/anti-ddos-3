#!/bin/bash
source /etc/profile
cd /tmp/
echo $1 | java SomeAgent controller1 9000 $2 client

