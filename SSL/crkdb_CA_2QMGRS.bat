REM This series of IBM gskit commands is what could used to set up SSL for two queue managers.
REM Three keystores are created.
REM wmqca.kdb will serve as the container of the certificate authority.
REM qma_ssl.kdb and qmb_ssl.kdb are the queue manager SSL keystores.
gsk7cmd -keydb -create -db qma_ssl.kdb -pw kdbpassword -type cms -expire 1095 -stash
gsk7cmd -keydb -create -db qmb_ssl.kdb -pw kdbpassword -type cms -expire 1095 -stash
gsk7cmd -keydb -create -db wmqca.kdb -pw kdbpassword -type cms -expire 1095 -stash
gsk7cmd -cert -create -db wmqca.kdb -pw kdbpassword -label wmqca -size 2048 -dn " CN=MYCA, OU=WMQ, O=Curtis, L=Shoreview, ST=MN, C=US" -expire 1095
gsk7cmd -cert -extract -db wmqca.kdb -pw kdbpassword -label wmqca -target wmqca.crt -format ascii
gsk7cmd -cert -add -db qma_ssl.kdb -pw kdbpassword -label wmqca -file wmqca.crt -format ascii
gsk7cmd -cert -add -db qmb_ssl.kdb -pw kdbpassword -label wmqca -file wmqca.crt -format ascii
gsk7cmd -certreq -create -db qma_ssl.kdb -pw kdbpassword -label ibmwebspheremqqma_ssl -size 2048 -dn " CN=qma_ssl, OU=WMQ, O=Curtis, L=Shoreview, ST=MN, C=US" -file qm1req.arm
gsk7cmd -certreq -create -db qmb_ssl.kdb -pw kdbpassword -label ibmwebspheremqqmb_ssl -size 2048 -dn " CN=qmb_ssl, OU=WMQ, O=Curtis, L=Shoreview, ST=MN, C=US" -file qm2req.arm
gsk7cmd -cert -sign -file qm1req.arm -db wmqca.kdb -pw kdbpassword -label wmqca -target qm1cert.arm -format ascii -expire 1090
gsk7cmd -cert -sign -file qm2req.arm -db wmqca.kdb -pw kdbpassword -label wmqca -target qm2cert.arm -format ascii -expire 1090
gsk7cmd -cert -receive -db qma_ssl.kdb -pw kdbpassword -file qm1cert.arm -format ascii
gsk7cmd -cert -receive -db qmb_ssl.kdb -pw kdbpassword -file qm2cert.arm -format ascii