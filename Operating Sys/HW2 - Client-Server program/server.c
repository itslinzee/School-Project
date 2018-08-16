#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>

struct Salary {
	char major[128];
	int early_salary;
	int mid_salary;
};

void error(char *msg) {
	perror(msg);
	exit(-1);
}

int main() {

	FILE *file;
	char filename[128];

	// Request name of file containing salaries
	printf("Enter name of file containing salaries (including extension): ");
	scanf("%s", filename);

	// Open the file and enter the info into a table
	file = fopen(filename, "r");

	struct Salary salaries[512];
	int index = 0;
	char *input;
	const char delim[] = "\t";

	// Read file for information and input into salaries array
	char buffer[128];
	while(fgets(buffer, 127, file) != NULL) {
		input = strtok(buffer, delim);
		strcpy(salaries[index].major, input);
		strcat(salaries[index].major, "\n");

		salaries[index].early_salary = atoi(strtok(NULL, delim));

		salaries[index].mid_salary = atoi(strtok(NULL, delim));

		index++;
	}

	// Prompt for port number to listen on
	int port_no;
	printf("Enter server port number: ");
	scanf("%d", &port_no);

	// Create a socket
	int server_sd;
	server_sd = socket(AF_INET, SOCK_STREAM, 0);
	if(server_sd < 0) {
		error("ERROR opening socket");
	}

	// Define the server address
	struct sockaddr_in server_addr, client_addr;
	bzero((char *) &server_addr, sizeof(server_addr));
	server_addr.sin_family = AF_INET;
	server_addr.sin_port = htons(port_no);
	server_addr.sin_addr.s_addr = INADDR_ANY;

	// Bind the socket
	int status = bind(server_sd, (struct sockaddr *) &server_addr, sizeof(server_addr));
	if(status < 0) {
		error("ERROR on binding");
	}

	// Listen for connections
	listen(server_sd, 5);

	do {

		int client_length = sizeof(client_addr);

		// Accept connection from client
		int client_sd;
		client_sd = accept(server_sd, (struct sockaddr*) &client_addr, &client_length);
		if(client_sd < 0) {
			error("ERROR on accept");
		}

		// Receive requested major from client
		bzero(buffer, 128);
		status = read(client_sd, buffer, sizeof(buffer));
		if(status < 0) {
			error("ERROR reading from socket");
		}

		// Find salaries of requested major
		int early_sal = -1;
		int mid_sal = -1;
		for(int i = 0; i < 512; i++) {
			if(strcmp(salaries[i].major, buffer) == 0) {
				early_sal = salaries[i].early_salary;
				mid_sal = salaries[i].mid_salary;
			}
		}

		// Formout message to client
		char server_msg[16];
		sprintf(server_msg, "%7d %7d", early_sal, mid_sal);

		// Send message to client
		status = write(client_sd, server_msg, sizeof(server_msg));
		if(status < 0) {
			error("ERROR writing to socket");
		}

		// Close the socket
		close(client_sd);

	} while(1);

	return 0;
}

