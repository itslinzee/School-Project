#include <string>

#include "Event.h"

Event::Event()
{

}

Event::Event(int time, std::string dev, int seqNumber)
{
	timeOfEvent = time;
	device = dev;
	processSeqNumber = seqNumber;
}

int Event::getTimeOfEvent() const
{
	return timeOfEvent;
}

std::string Event::getDevice() const
{
	return device;
}

int Event::getProcSeqNumber() const
{
	return processSeqNumber;
}

bool Event::operator<(const Event& e) const
{
	if (this->timeOfEvent >= e.getTimeOfEvent())
		return true;
	else
		return false;
}

