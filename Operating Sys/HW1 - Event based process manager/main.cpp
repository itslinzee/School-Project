#define _CRT_SECURE_NO_WARNINGS

#include <vector>
#include <string>
#include <queue>
#include <iostream>

#include "Event.h"

#define START_TIME 0
#define FIRST_LINE 1
#define LAST_LINE 2
#define CURRENT_LINE 3
#define STATE 4
#define PROC_NUM 5

// Comparator class to find highest priority of events
class myComp
{
public:
	myComp() {}
	bool operator() (const Event& lhs, const Event& rhs) const
	{
		if (lhs.getTimeOfEvent() != rhs.getTimeOfEvent())
		{
			return lhs.getTimeOfEvent() > rhs.getTimeOfEvent();
		}
		else if (lhs.getDevice() != rhs.getDevice())
		{
			return lhs.getDevice().at(0) > rhs.getDevice().at(0);
		}
		else
		{
			return lhs.getProcSeqNumber() > rhs.getProcSeqNumber();
		}
	}
};

enum States
{
	READY,
	RUNNING,
	BLOCKED,
	TERMINATED,
	NEW
};

void printArrival(int procSeqNum, int timeOfArrival);
void printSummary();
void printProcSummary();
void coreRequest(Event procEvent, int releaseTime);
void coreRequestComplete();
void ssdRequest(Event procEvent, int releaseTime);
void ssdRequestComplete();
void inputRequest(Event procEvent, int releaseTime);
void inputRequestComplete();

// Stores process sequence number
std::queue<int> readyQ;
std::queue<int> ssdQ;
std::queue<int> inputQ;

// Resources (First int = busy; second int = time)
std::vector<std::pair<int, int> > cores;
std::pair<int, int> ssd;
std::pair<int, int> input;

// Holds processes and their current request
std::vector<std::vector<int> > processTable;

// Holds all requests for all processes
std::vector<std::pair<std::string, int> > dataTable;

// Event list
std::priority_queue<Event, std::vector<Event>, myComp> eventList;

float currentTime = 0;
int ssdCount = 0;
float ssdTimes = 0;
float coreTime = 0;
int processCount = 0;

// Event scheduler simulation
int main()
{
	int numCores = 0;

	std::string operation = "";
	int accessTime = 0;

	// Count lines in file = lines in data table
	int line = 0;

	// Get number of cores
	std::cin >> operation >> numCores;

	// Set up cores
	for (unsigned int i = 0; i < numCores; i++)
	{
		cores.push_back(std::pair<int, int>(0, 0));
	}

	// Read in operation and access time
	while (std::cin >> operation >> accessTime)
	{
		// Add operation and access time to data table
		dataTable.push_back(std::make_pair(operation, accessTime));

		// If new process, add to process table and increment process counter
		if (operation.compare("NEW") == 0)
		{
			processCount++;
			std::vector<int> process;
			process.resize(6);
			process.at(START_TIME) = accessTime;
			process.at(FIRST_LINE) = line;
			process.at(LAST_LINE) = 0;
			process.at(CURRENT_LINE) = line;
			process.at(STATE) = States::NEW;
			process.at(PROC_NUM) = processCount - 1;
			processTable.push_back(process);

			// Create arrival event for new process
			Event newEvent(process.at(START_TIME), operation, process.at(PROC_NUM));
			eventList.push(newEvent);
		}

		line++;
	}

	// Update last lines for each process
	for (unsigned int i = 1; i < processTable.size(); i++)
	{
		processTable.at(i - 1).at(LAST_LINE) = processTable.at(i).at(FIRST_LINE) - 1;
	}
	processTable.at(processTable.size() - 1).at(LAST_LINE) = line - 1;


	// Process events until event list is empty
	while (!eventList.empty())
	{
		// Get next event from event list queue
		Event currentEvent = eventList.top();
		int procSeqNumber = currentEvent.getProcSeqNumber();

		// Set current time
		currentTime = currentEvent.getTimeOfEvent();

		// Find process in process table that has same first line(seq number) as current event
		for (unsigned int i = 0; i < processTable.size(); i++)
		{
			if (processTable.at(i).at(PROC_NUM) == procSeqNumber)
			{
				// Make sure current event is not new arrival
				if (processTable.at(i).at(STATE) != States::NEW)
				{
					// If not a new arrival, process completion request of current resource
					int currentLine = processTable.at(i).at(CURRENT_LINE);
					std::pair<std::string, int> currentPair = dataTable.at(currentLine);
					std::string currentDevice = std::get<0>(currentPair);

					switch (currentDevice[0])
					{
					case 'N':
						break;
					case 'C':
						coreRequestComplete();
						break;
					case 'S':
						ssdTimes = ssdTimes + currentTime;
						ssdRequestComplete();
						break;
					case 'I':
						inputRequestComplete();
						break;
					default:
						break;
					}
				}
				// If process is new arrival, print summary
				else
				{
					printArrival(procSeqNumber, currentTime);
				}

				// If at end of process
				if (processTable.at(i).at(CURRENT_LINE) == processTable.at(i).at(LAST_LINE))
				{
					// Terminate process and output states of remaining processes
					processTable.at(i).at(STATE) = States::TERMINATED;
					std::printf("Process %i terminated at %.0f ms\n", processTable.at(i).at(PROC_NUM), currentTime);

					if (!processTable.empty())
					{
						printProcSummary();
					}

					// Remove process from process table
					processTable.erase(processTable.begin() + i);
				}
				else
				{
					// Otherwise, increment current line and process request at current line of data table
					processTable.at(i).at(CURRENT_LINE)++;

					int currentLine = processTable.at(i).at(CURRENT_LINE);
					std::pair<std::string, int> currentPair = dataTable.at(currentLine);
					std::string currentDevice = std::get<0>(currentPair);
					int accessTime = std::get<1>(currentPair);
					int releaseTime = currentTime + accessTime;

					switch (currentDevice[0])
					{
					case 'N':
						break;
					case 'C':
						coreTime = coreTime + accessTime;
						coreRequest(currentEvent, releaseTime);
						break;
					case 'S':
						ssdCount++;
						ssdTimes = ssdTimes - currentTime;
						ssdRequest(currentEvent, releaseTime);
						break;
					case 'I':
						inputRequest(currentEvent, releaseTime);
						break;
					default:
						break;
					}
				}
			}
		}

		// Pop top value off event list
		eventList.pop();
	}

	printSummary();

	return 0;
}

// Print summary of processes at arrival of a new process
void printArrival(int procSeqNum, int timeOfArrival)
{
	for (unsigned int i = 0; i < processTable.size(); i++)
	{
		if (processTable.at(i).at(PROC_NUM) == procSeqNum)
		{
			std::printf("Process %i starts at %i ms\n", processTable.at(i).at(PROC_NUM), timeOfArrival);
			if (!processTable.empty())
			{
				printProcSummary();
			}
		}
	}
}

// Print summary of processes at arrival and termination
void printProcSummary()
{
	for (unsigned int i = 0; i < processTable.size(); i++)
	{
		int procID = processTable.at(i).at(PROC_NUM);
		int procState = processTable.at(i).at(STATE);
		std::string stateStr = "";

		switch (procState)
		{
		case 0:
			stateStr = "READY";
			break;
		case 1:
			stateStr = "RUNNING";
			break;
		case 2:
			stateStr = "BLOCKED";
			break;
		case 3:
			stateStr = "TERMINATED";
			break;
		default:
			break;
		}

		if (procState != States::NEW)
		{
			std::cout << "Process " << procID << " is " << stateStr << std::endl;
		}
	}

	std::printf("\n");
}


// Process for a new core request
void coreRequest(Event procEvent, int releaseTime)
{
	bool bIsAssigned = false;

	// Look for an open core
	for (unsigned int i = 0; i < cores.size(); i++)
	{
		// If there is an open core, assign process
		if (std::get<0>(cores.at(i)) == 0 && bIsAssigned == false)
		{
			std::get<0>(cores.at(i)) = 1;
			std::get<1>(cores.at(i)) = releaseTime;

			// Find process in process table and set state to running
			for (unsigned int j = 0; j < processTable.size(); j++)
			{
				if (processTable.at(j).at(PROC_NUM) == procEvent.getProcSeqNumber())
				{
					processTable.at(j).at(STATE) = States::RUNNING;
				}
			}

			// Create new event for when request completes and add to event list
			Event newEvent(releaseTime, "CORE", procEvent.getProcSeqNumber());
			eventList.push(newEvent);

			bIsAssigned = true;
		}
	}

	// If unable to find open core, add process to ready queue
	if (bIsAssigned == false)
	{
		readyQ.push(procEvent.getProcSeqNumber());

		// Find process in process table and set state to ready
		for (unsigned int j = 0; j < processTable.size(); j++)
		{
			if (processTable.at(j).at(PROC_NUM) == procEvent.getProcSeqNumber())
			{
				processTable.at(j).at(STATE) = States::READY;
			}
		}
	}
}

// Process for core completion
void coreRequestComplete()
{
	bool coreReleased = false;

	// Find core that has just been released and release core
	for (unsigned int i = 0; i < cores.size(); i++)
	{
		if (currentTime == std::get<1>(cores.at(i)) && coreReleased == false)
		{
			std::get<0>(cores.at(i)) = 0;
			std::get<1>(cores.at(i)) = 0;
			coreReleased = true;
		}
	}

	// Determine if ready queue has waiting processes
	if (!readyQ.empty())
	{
		// Get sequence number of next process to use core
		int waitingProcSeqNum = readyQ.front();

		// Find current line of waiting process
		int currentLine = 0;
		for (unsigned int i = 0; i < processTable.size(); i++)
		{
			if (processTable.at(i).at(PROC_NUM) == waitingProcSeqNum)
			{
				currentLine = processTable.at(i).at(CURRENT_LINE);
			}
		}

		// Get time next process will release core
		std::pair<std::string, int> waitingCoreRequest = dataTable.at(currentLine);
		int releaseTime = currentTime + std::get<1>(waitingCoreRequest);

		// Create new event for release of core
		Event nextCoreEvent(releaseTime, "CORE", waitingProcSeqNum);

		// Request core for process
		coreRequest(nextCoreEvent, releaseTime);

		// Remove process from ready queue
		readyQ.pop();
	}
}

// Process for SSD request
void ssdRequest(Event procEvent, int releaseTime)
{
	if (releaseTime == currentTime)
	{
		Event newEvent(releaseTime, "SSD", procEvent.getProcSeqNumber());
		eventList.push(newEvent);
		return;
	}

	// If ssd is open, assign to process
	if (std::get<0>(ssd) == 0)
	{
		std::get<0>(ssd) = 1;
		std::get<1>(ssd) = releaseTime;

		// Create new event for when request completes and add to event list
		Event newEvent(releaseTime, "SSD", procEvent.getProcSeqNumber());
		eventList.push(newEvent);
	}
	// Otherwise, add to ssd queue
	else
	{
		ssdQ.push(procEvent.getProcSeqNumber());
	}

	// Find process in process table and set state to blocked
	for (unsigned int j = 0; j < processTable.size(); j++)
	{
		if (processTable.at(j).at(PROC_NUM) == procEvent.getProcSeqNumber())
		{
			processTable.at(j).at(STATE) = States::BLOCKED;
		}
	}
}

// Process for SSD completion
void ssdRequestComplete()
{
	// Set ssd to open
	std::get<0>(ssd) = 0;
	std::get<1>(ssd) = 0;

	// If a process is waiting in ssd queue, assign to ssd
	if (!ssdQ.empty())
	{
		// Get sequence number of next process to use ssd
		int waitingProcSeqNum = ssdQ.front();

		// Find current line of waiting process
		int currentLine = 0;
		for (unsigned int i = 0; i < processTable.size(); i++)
		{
			if (processTable.at(i).at(PROC_NUM) == waitingProcSeqNum)
			{
				currentLine = processTable.at(i).at(CURRENT_LINE);
			}
		}

		// Get time next process will release ssd
		std::pair<std::string, int> waitingSsdRequest = dataTable.at(currentLine);
		int releaseTime = currentTime + std::get<1>(waitingSsdRequest);

		// Create new event for release of ssd and add to event list
		Event nextSsdEvent(releaseTime, "SSD", waitingProcSeqNum);

		// Request ssd for process
		ssdRequest(nextSsdEvent, releaseTime);

		// Remove process from ssd queue
		ssdQ.pop();
	}
}

// Process for input request
void inputRequest(Event procEvent, int releaseTime)
{
	// If input is open, assign to process
	if (std::get<0>(input) == 0)
	{
		std::get<0>(input) = 1;
		std::get<1>(input) = releaseTime;

		// Create new event for when request completes and add to event list
		Event newEvent(releaseTime, "INPUT", procEvent.getProcSeqNumber());
		eventList.push(newEvent);
	}
	// Otherwise, add to input queue
	else
	{
		inputQ.push(procEvent.getProcSeqNumber());
	}

	// Find process in process table and set state to blocked
	for (unsigned int j = 0; j < processTable.size(); j++)
	{
		if (processTable.at(j).at(PROC_NUM) == procEvent.getProcSeqNumber())
		{
			processTable.at(j).at(STATE) = States::BLOCKED;
		}
	}
}

// Process for input completion
void inputRequestComplete()
{
	// Set input to open
	std::get<0>(input) = 0;
	std::get<1>(input) = 0;

	// If a process is waiting in input queue, assign to input
	if (!inputQ.empty())
	{
		// Get sequence number of next process to use ssd
		int waitingProcSeqNum = inputQ.front();

		// Find current line of waiting process
		int currentLine = 0;
		for (unsigned int i = 0; i < processTable.size(); i++)
		{
			if (processTable.at(i).at(PROC_NUM) == waitingProcSeqNum)
			{
				currentLine = processTable.at(i).at(CURRENT_LINE);
			}
		}

		// Get time next process will release input
		std::pair<std::string, int> waitingInputRequest = dataTable.at(currentLine);
		int releaseTime = currentTime + std::get<1>(waitingInputRequest);

		// Create new event for release of input and add to event list
		Event nextInputEvent(releaseTime, "INPUT", waitingProcSeqNum);

		// Request input for process
		inputRequest(nextInputEvent, releaseTime);

		// Remove process from input queue
		inputQ.pop();
	}
}

// Print summary at end of simulation
void printSummary()
{
	float avgCoreUtilization = (coreTime / currentTime) * 100;
	float avgSSDUtilization = (ssdTimes / currentTime) * 100;

	std::printf("SUMMARY:\n");
	std::printf("Number of processes that completed: %i\n", processCount);
	std::printf("Total number of SSD accessess: %i\n", ssdCount);
	std::printf("Average SSD access time: %.2f ms\n", (ssdTimes / ssdCount));
	std::printf("Total elapsed time: %.0f ms\n", currentTime);
	std::printf("Core utilization: %.2f percent\n", avgCoreUtilization);
	std::printf("SSD utilization: %.2f percent\n", avgSSDUtilization);
}
