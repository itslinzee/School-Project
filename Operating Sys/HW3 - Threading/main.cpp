#include <pthread.h>
#include <signal.h>
#include <unistd.h>
#include <iostream>
#include <string>
#include <vector>

static std::string traffic = "N";
static int maxCarsInTunnel = 0;
static int carsInTunnel = 0;
static int numBBCars = 0;
static int numWBCars = 0;
static int numCarsWait = 0;
static int totalNumCars = 0;
static int carID = 0;

static pthread_mutex_t traffic_lock;

static pthread_cond_t wake_up = PTHREAD_COND_INITIALIZER;

void *WBcar(void *arg) {
	int crossTime = (int) arg;
	int carNo = carID;
	bool hasWaited = false;

	//Car arrives at tunnel
	pthread_mutex_lock(&traffic_lock);
	printf("Car #%i going to Whittier arrives at the tunnel.\n", carNo);

	//Determine if car must wait at tunnel
	while(traffic.compare("WB") != 0 || carsInTunnel >= maxCarsInTunnel) {
		if(!hasWaited && traffic.compare("WB") == 0) {
			numCarsWait++;
			hasWaited = true;
		}
		pthread_cond_wait(&wake_up, &traffic_lock);
	}

	//Car enters tunnel
	carsInTunnel++;
	printf("Car #%i going to Whittier enters the tunnel.\n", carNo);
	pthread_mutex_unlock(&traffic_lock);

	//Sleep for cross time
	sleep(crossTime);

	//Car exits tunnel
	pthread_mutex_lock(&traffic_lock);
	carsInTunnel--;
	pthread_cond_broadcast(&wake_up);
	printf("Car #%i going to Whittier exits the tunnel.\n", carNo);
	numWBCars++;
	pthread_mutex_unlock(&traffic_lock);

	//Terminate thread
	pthread_exit((void*) 0);
}

void *BBcar(void *arg) {
	int crossTime = (int) arg;
	int carNo = carID;

	bool hasWaited = false;

	//Car arrives at tunnel
	pthread_mutex_lock(&traffic_lock);
	printf("Car #%i going to Bear Valley arrives at the tunnel.\n", carNo);

	//Determine if car must wait at tunnel
	while(traffic.compare("BB") != 0 || carsInTunnel >= maxCarsInTunnel) {
		if(!hasWaited && traffic.compare("BB") == 0) {
			numCarsWait++;
			hasWaited = true;
		}
		pthread_cond_wait(&wake_up, &traffic_lock);
	}

	//Car enters tunnel
	carsInTunnel++;
	printf("Car #%i going to Bear Valley enters the tunnel.\n", carNo);
	pthread_mutex_unlock(&traffic_lock);

	//Sleep for cross time
	sleep(crossTime);

	//Car exits tunnel
	pthread_mutex_lock(&traffic_lock);
	carsInTunnel--;
	pthread_cond_broadcast(&wake_up);
	printf("Car #%i going to Bear Valley exits the tunnel.\n", carNo);
	numBBCars++;
	pthread_mutex_unlock(&traffic_lock);

	//Terminate thread
	pthread_exit((void*) 0);
}

void *tunnel() {
	//Change status of tunnel status every 5 sec and output change
	while(true) {
		//Open traffic to Whittier bound cars
		pthread_mutex_lock(&traffic_lock);
		traffic = "WB";
		pthread_cond_broadcast(&wake_up);
		printf("\nThe tunnel is now open to Whittier-bound traffic.\n\n");
		pthread_mutex_unlock(&traffic_lock);
		sleep(5);

		//Close tunnel to all traffic
		pthread_mutex_lock(&traffic_lock);
		traffic = "N";
		printf("\nThe tunnel is now closed to ALL traffic.\n\n");
		pthread_mutex_unlock(&traffic_lock);
		sleep(5);

		//Open traffic to Bear Valley bound traffic
		pthread_mutex_lock(&traffic_lock);
		traffic = "BB";
		pthread_cond_broadcast(&wake_up);
		printf("\nThe tunnel is now open to Bear Valley-bound traffic.\n\n");
		pthread_mutex_unlock(&traffic_lock);
		sleep(5);

		//Close tunnel to all traffic
		pthread_mutex_lock(&traffic_lock);
		traffic = "N";
		printf("\nThe tunnel is now closed to ALL traffic.\n\n");
		pthread_mutex_unlock(&traffic_lock);
		sleep(5);
	}
}

int main() {
	int delayTime;
	std::string direction;

	int crossingTime;

	//Initialize mutex
	pthread_mutex_init(&traffic_lock, NULL);

	//Get max number of cars allowed in tunnel
	std::cin >> maxCarsInTunnel;
	printf("Max cars allowed in tunnel: %i\n", maxCarsInTunnel);

	//Create tunnel thread
	pthread_t tunneltid;
	pthread_create(&tunneltid, NULL, tunnel, NULL);

	//Create array for car thread ids
	pthread_t cartids[128];

	//Read next input line and create child thread
	while(std::cin >> delayTime >> direction >> crossingTime) {

		//Wait for car to arrive
		sleep(delayTime);

		//Create child/car thread
		if(direction.compare("BB") == 0) {
			pthread_create(&cartids[carID], NULL, BBcar, (void*) crossingTime);
		}
		else {
			pthread_create(&cartids[carID], NULL, WBcar, (void*) crossingTime);
		}

		//Increment carID (total number of cars)
		carID++;
	}


	//Wait for all car threads to finish
	for(int i = 0; i < carID; i++) {
		pthread_join(cartids[i], NULL);
	}

	//Output summary
	printf("%i car(s) going to Bear Valley arrived at the tunnel.\n", numBBCars);
	printf("%i car(s) going to Whittier arrived at the tunnel.\n", numWBCars);
	printf("%i car(s) were delayed.\n", numCarsWait);

	//Terminate tunnel thread
	pthread_kill(tunneltid, 15);
}
