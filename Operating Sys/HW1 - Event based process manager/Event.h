#ifndef EVENT_H
#define EVENT_H

#include <string>
#include <algorithm>

class Event
{
public:
	Event();
	Event(int time, std::string dev, int seqNumber);

	// For debugging
	int getTimeOfEvent() const;
	std::string getDevice() const;
	int getProcSeqNumber() const;
	bool operator < (const Event&) const;

private:
	int timeOfEvent;
	std::string device;
	int processSeqNumber;
};

#endif

