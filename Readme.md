## DEMO API Swagger OVERVIEW #

This repository is intended to help ISVs to integrate our API in their products. 

It contains examples of sequences of API calls to implement a standard document signature workflow.

As of today, examples are provided in the following programming languages:

* PHP
* JAVA


### Getting started ###

Clone the repository

### Docker ###

If you want to run the demo in a container, Docker Desktop must be installed on your machine

The project contains a minimal dockerfile with a php7+apache base image so that you can launch the demo behind an Apache web server in a container.

There is also a docker compose file with a volume that maps ./php/src on the local host to /var/www/html in the container, so that he changes made locally to the code are propagated to the container.

To see your changes, just refresh the app web page.

To launch the container type

* docker-compose up

### Demo application usage ###

Assign your licence token and contract definition id to the corresponding variables defined at the top of the index file.

build the docker image

Launch the container

When you point your browser to the demo web page, you can see 5 buttons

* Create contract : creates one contract with 1 document and 2 recipients
* Get status : retreives the contrat status (OPEN, SINED, ARCHIVED, ABANDONNED)
* Sign contracts : redirects to the contract signature page
* Sign contract if : loads the signature page into an iframe
* Download signed contract: downloads the signed contract into an iframe

Get Status should be cliked after the contract creation.

Sign contract should be clicked after having selected a signatory in the combo box (next to Get status)

Get signed contract should be clicked after all the signatories have signed.

Otherwise, you wil get error messages.
