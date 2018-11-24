#
# This is updated with gskit version 8 linux 64 bit version
#
gsk8capicmd_64 -keydb -create -db qma_ssl.kdb -pw kdbpassword -type cms -expire 1095 -stash
gsk8capicmd_64 -keydb -create -db qmb_ssl.kdb -pw kdbpassword -type cms -expire 1095 -stash
gsk8capicmd_64 -keydb -create -db wmqca.kdb -pw kdbpassword -type cms -expire 1095 -stash
gsk8capicmd_64 -cert -create -db wmqca.kdb -pw kdbpassword -label wmqca -size 2048 -dn "CN=MYCA\\,OU=WMQ\\,O=Curtis\\,L=Shoreview\\,ST=MN\\,C=US\\" -expire 1095
gsk8capicmd_64 -cert -extract -db wmqca.kdb -pw kdbpassword -label wmqca -target wmqca.crt -format ascii
gsk8capicmd_64 -cert -add -db qma_ssl.kdb -pw kdbpassword -label wmqca -file wmqca.crt -format ascii
gsk8capicmd_64 -cert -add -db qmb_ssl.kdb -pw kdbpassword -label wmqca -file wmqca.crt -format ascii
gsk8capicmd_64 -certreq -create -db qma_ssl.kdb -pw kdbpassword -label ibmwebspheremqqma_ssl -size 2048 -dn "CN=qma_ssl\\,OU=WMQ\\,O=Curtis\\,L=Shoreview\\,ST=MN\\,C=US\\" -file qm1req.arm
gsk8capicmd_64 -certreq -create -db qmb_ssl.kdb -pw kdbpassword -label ibmwebspheremqqmb_ssl -size 2048 -dn "CN=qmb_ssl\\,OU=WMQ\\,O=Curtis\\,L=Shoreview\\,ST=MN\\,C=US\\" -file qm2req.arm
gsk8capicmd_64 -cert -sign -file qm1req.arm -db wmqca.kdb -pw kdbpassword -label wmqca -target qm1cert.arm -format ascii -expire 1090
gsk8capicmd_64 -cert -sign -file qm2req.arm -db wmqca.kdb -pw kdbpassword -label wmqca -target qm2cert.arm -format ascii -expire 1090
gsk8capicmd_64 -cert -receive -db qma_ssl.kdb -pw kdbpassword -file qm1cert.arm -format ascii
gsk8capicmd_64 -cert -receive -db qmb_ssl.kdb -pw kdbpassword -file qm2cert.arm -format ascii