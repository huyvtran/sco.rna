# sco.rna

RNA service integration component. Creates and stores the requests, performing periodic polling in order to 
update local state of the request states. 

## Installation with docker

### Configuration

Configure properties in file `docker-configs/app.env`

### Build and run

From the root of the project: 

* Try the system executing `docker-compose up`
* Run as a daemon executing `docker-compose start`
* Stop the daemon executing `docker-compose stop`
