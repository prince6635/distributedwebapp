#!/bin/bash
load=`top -n 1 | sed -n '1p' | awk '{print $11}'` 
load=${load%\,*}
disk_usage=`df -h | sed -n '2p' | awk '{print $(NF - 1)}'` 
disk_usage=${disk_usage%\%*}
overhead=`expr $load \> 2.00` 
if [ $overhead -eq 1 ];then
	echo "system load is overhead" 
fi
if [ $disk_usage -gt 85 ];then
	echo "disk is nearly full, need more disk space"
fi
exit 0
