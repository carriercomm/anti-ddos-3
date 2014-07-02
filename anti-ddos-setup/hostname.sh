#!/bin/bash

read -r nickname </var/emulab/boot/nickname
host=$(echo $nickname | cut -f1 -d.)
echo $host > /etc/hostname
/bin/hostname -F /etc/hostname

echo 10.10.13.2 controller1 > /etc/hosts
echo 10.10.11.2 controller2 >> /etc/hosts
