#!/bin/bash
. /opt/mqm/bin/setmqenv -s
dspmq | awk '/Ended/ {print $1}' | awk '{ match($0, /QMNAME\((.*)\)/,arr); if(arr[1] != "") print arr[1] }' | xargs -l strmqm
