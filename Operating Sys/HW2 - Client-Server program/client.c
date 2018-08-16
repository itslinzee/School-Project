#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <netdb.h>

void error(char *msg) {
	perror(msg);
	exit(0);
}

int main() {

	int count = 1;

	// Get information from user for server name and socket number
	char hostname[128];
	printf("Enter server host name: ");
	scanf("%s", hostname);

	int port_no;
	printf("Enter server port number: ");
	scanf("%i", &port_no);

	// Specify address for socket
	struct sockaddr_in server_addr;
	struct hostent *server;

	server = gethostbyname(hostname);
	if(server == NULL) {
		error("ERROR no such host");
	}

	// Set up server address
	bzero((char *) &server_addr, sizeof(server_addr));
	server_addr.sin_family = AF_INET;
	bcopy((char *)server->h_addr, (char *)&server_addr.sin_addr.s_addr, server->h_length);
	server_addr.sin_port = htons(port_no);

	do {

		// Create a socket
		int client_sd = socket(AF_INET, SOCK_STREAM, 0);
		if(client_sd < 0) {
			error("ERROR opening socket");
		}

		// Prompt user for major of salary requested
		char buffer[128];
		printf("Enter a college major: ");
		bzero(buffer, 128);
		fgets(buffer, 127, stdin);

		// Check if user is finished
		char major[128];
		strcpy(major, buffer);
		if(buffer[0] == '\n' && count != 1) {
			close(client_sd);
			exit(0);
		}

		// Connect socket
		int status = connect(client_sd, (struct sockaddr *) &server_addr, sizeof(server_addr));
		if(status == -1) {
			error("ERROR connecting to remote socket\n");
		}

		// Write request to server
		status = write(client_sd, buffer, sizeof(buffer));
		if(status < 0) {
			error("ERROR writing to socket");
		}

		// Receive response from server
		char answer[16];
		status = read(client_sd, answer, sizeof(answer));
		if(status < 0) {
			error("ERROR reading from socket");
		}

		// Parse data received from server
		int early_sal;
		int mid_sal;
		sscanf(answer, "%7d %7d", &early_sal, &mid_sal);

		// Print out data received
		if(early_sal == -1) {
			printf("That major is not in the table.\n\n");
		} else {
			printf("The average early career pay for a(n) %s major is $%i.\n", major, early_sal);
			printf("The corresponding mid-career pay is $%i\n\n", mid_sal);
		}

		// Close the socket
		close(client_sd);

		count++;

	} while(1);

	return 0;
}

