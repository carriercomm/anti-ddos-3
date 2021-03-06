#!/usr/bin
# =====================================================================
# 
# This script is to generate ospf configuration for the VMs
# that has XORP installed, and start xorp process. 
#
# The script first to get the network configuration information, 
# such as virtual interfaces (neither eth0 nor lo), hostname, etc. 
# 
# Secondly, the script will start xorp process in the VM 
#
#
# Code by Xuan Liu
# xliu@bbn.com
# 
# June 9, 2014
# =====================================================================


vm_info_file=$PWD/vm_info.txt
host=echo `hostname`
echo $host

# get hostname 
hostname | sudo tee $vm_info_file > /dev/null
# get interface information
/sbin/ifconfig | egrep 'OVS|inet addr' | sudo tee -a $vm_info_file > /dev/null
# get timestamp
timestamp=$(date +"%Y-%m-%d %r")
echo $timestamp


xorp_conf_dir=/etc/xorp

if [ -d $xorp_conf_dir ] 
then
   echo "XORP dir exist"
else
   echo "creating xorp dir"
   sudo mkdir $xorp_conf_dir
fi

sudo /usr/bin/awk -f $PWD/ospfd-conf-gen.awk $vm_info_file "$timestamp" 24 | sudo tee $PWD/ospfd.conf > /dev/null

sudo cp $PWD/ospfd.conf $xorp_conf_dir/.

# check if xorp has been added to the group
xorp_group=`sudo cat /etc/group | grep "xorp"`
if [ "$xorp_group" = "" ]
then
   echo "Add xorp to group"
   sudo groupadd xorp
else
   echo "xorp is already added to the group"
fi



# first stop current xorp process if it's running

xorp_pids=`ps -ef | grep xorp_ | /usr/bin/awk '{ if ( $1 == "root" ) {print $2}}'`
if [ "$xorp_pids" = "" ]
then
   echo "xorp is not running at this time"
else
   echo "xorp is running, stop it first"
   ps -ef | grep xorp_ | /usr/bin/awk '{ if ( $1 == "root" ) {print "sudo kill -9 " $2}}' | sh
fi


# start xorp
cd /usr/local/xorp/sbin/
echo "XORP is starting ......"
sudo ./xorp_rtrmgr -b $xorp_conf_dir/ospfd.conf -l /tmp/xorp_rtrmgr_log -d


# restart the interface

/sbin/ifconfig -a | grep OVS | awk '{ if (substr($1, 4,4) != 0) { print "sudo /sbin/ifconfig " $1 " down"}}' |sh
/sbin/ifconfig -a | grep OVS | awk '{ if (substr($1, 4,4) != 0) { print "sudo /sbin/ifconfig " $1 " up"}}' |sh 




