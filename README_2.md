# eMRTD Identity Source microservice

References *DELIVERABLES D2.1, D3.2*


## Overview  

...

The code is structured to have a clear MVC structure.

The underlying library integrating swagger to SpringBoot is [springfox](https://github.com/springfox/springfox)

The following environment variables are to be set:
	
	- ASYNC_SIGNATURE= boolean, if true denotes RSA signing for JWTs, else HS256 signing is conducted
	- KEYSTORE_PATH = path to the keystore holding the RSA certificate used for signing JWTs, encrypting JWEs, ...
	- KEY_PASS= password for the certificate
	- STORE_PASS= password for the keystore containing the certificate
	- HTTPSIG_CERT_ALIAS= for the httpsig protocol
	- SIGNING_SECRET= HS256 secret used for symmetric signing of jwts, e.g. QjG+wP1CbAH2z4PWlWIDkxP4oRlgK2vos5/jXFfeBw8=
	- CONFIGURATION_MANAGER_URL= location of the configuration manager
	- SESSION_MANAGER_URL= location of the session manager
	- SSL_* = information related to your ssl certificate
		
		SSL_KEYSTORE_PATH
		SSL_STORE_PASS
		SSL_KEY_PASS
		SSL_CERT_ALIAS
            
## Docker container

Current version: **mvjatos/seal-emrtd-is:0.0.1**

## docker-compose example

```
 emrtd-is:
        image: mvjatos/seal-emrtd-is:0.0.1
        environment:
            - KEYSTORE_PATH=/resources/testKeys/keystore.jks
            - KEY_PASS=
            - STORE_PASS=
            - HTTPSIG_CERT_ALIAS=
            - SIGNING_SECRET=
            - ASYNC_SIGNATURE=true
            - CONFIGURATION_MANAGER_URL=https://vm.project-seal.eu:8083
            - SESSION_MANAGER_URL=http://SessionManager:8090
            - SSL_KEYSTORE_PATH=
            - SSL_STORE_PASS=
            - SSL_KEY_PASS=
            - SSL_CERT_ALIAS=
        volumes:
            - /SEAL/EMRTD-IS/resources:/resources
        links:
            - ConfManager:vm.project-seal.eu
            - SessionManager:SessionManager
        ports:
          - 9073:8073
        depends_on:
          - SessionManager
          - ConfManager 

