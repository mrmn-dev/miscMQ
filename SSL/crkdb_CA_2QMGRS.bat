REM This series of IBM gskit commands is what could used to set up SSL for two queue managers.
REM Three keystores are created.
REM wmqca.kdb will serve as the container of the certificate authority.
REM qma_ssl.kdb and qmb_ssl.kdb are the queue manager SSL keystores.
gsk7cmd -keydb -create -db qma_ssl.kdb -pw mqssl -type cms -expire 1095 -stash
gsk7cmd -keydb -create -db qmb_ssl.kdb -pw mqssl -type cms -expire 1095 -stash
gsk7cmd -keydb -create -db wmqca.kdb -pw mqssl -type cms -expire 1095 -stash
gsk7cmd -cert -create -db wmqca.kdb -pw mqssl -label wmqca -dn " CN=MYCA, OU=WMQ, O=Curtis, L=Shoreview, ST=MN, C=US" -expire 1095
gsk7cmd -cert -extract -db wmqca.kdb -pw mqssl -label wmqca -target wmqca.crt -format ascii
gsk7cmd -cert -add -db qma_ssl.kdb -pw mqssl -label wmqca -file wmqca.crt -format ascii
gsk7cmd -cert -add -db qmb_ssl.kdb -pw mqssl -label wmqca -file wmqca.crt -format ascii
gsk7cmd -certreq -create -db qma_ssl.kdb -pw mqssl -label ibmwebspheremqqma_ssl -dn " CN=qma_ssl, OU=WMQ, O=Curtis, L=Shoreview, ST=MN, C=US" -file qm1req.arm
gsk7cmd -certreq -create -db qmb_ssl.kdb -pw mqssl -label ibmwebspheremqqmb_ssl -dn " CN=qmb_ssl, OU=WMQ, O=Curtis, L=Shoreview, ST=MN, C=US" -file qm2req.arm
gsk7cmd -cert -sign -file qm1req.arm -db wmqca.kdb -pw mqssl -label wmqca -target qm1cert.arm -format ascii -expire 1090
gsk7cmd -cert -sign -file qm2req.arm -db wmqca.kdb -pw mqssl -label wmqca -target qm2cert.arm -format ascii -expire 1090
gsk7cmd -cert -receive -db qma_ssl.kdb -pw mqssl -file qm1cert.arm -format ascii
gsk7cmd -cert -receive -db qmb_ssl.kdb -pw mqssl -file qm2cert.arm -format ascii
