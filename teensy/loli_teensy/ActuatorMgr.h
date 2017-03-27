#ifndef _ACTUATORMGR_h
#define _ACTUATORMGR_h

#if defined(ARDUINO) && ARDUINO >= 100
	#include "arduino.h"
#else
	#include "WProgram.h"
#endif

#include "Singleton.h"
#include "utils.h"
#include "ControlerNet.h"
#include "DynamixelMotor.h"
#include "InterfaceAX12.h"
#include "ax12config.h"


#define	ANGLE_DOWN		65
#define ANGLE_HALFWAY	80
#define ANGLE_UP		155
#define ANGLE_RELEASE	150

#define TOLERANCE_ANGLE	5

#define FAN_STARTUP_DURATION	2000 // ms
#define FUNNY_ACTION_DURATION	5000 // ms


/*
	Chaque m�thode est � ex�cuter en boucle, la permi�re fois avec l'argument 'launch' � true.
	Elle renvoie 'true' si l'action est ternim�e, 'false' sinon.
	Si la m�thode peut donner un retour sur la r�ussite de l'action, elle renvoie un uint8_t prenant les valeurs:
	SUCCESS:0x00	FAILURE:0x01	RUNNING:0xFF
*/
class ActuatorMgr : public Singleton<ActuatorMgr>
{
public:
	ActuatorMgr():
		ax12interface(InterfaceAX12::Instance()),
		ax12net(ax12interface.serial, ID_NET_AX12)
	{
		pinMode(PIN_VENTILATEUR, OUTPUT);
		digitalWrite(PIN_VENTILATEUR, LOW);
		ax12net.init();
		ax12net.enableTorque();
		ax12net.jointMode();
	}

	bool pullDownNet(bool launch)
	{
		static uint32_t lastCommTime;
		return moveTheAX12(launch, ANGLE_DOWN, TOLERANCE_ANGLE, lastCommTime);
	}

	bool putNetHalfway(bool launch)
	{
		static uint32_t lastCommTime;
		return moveTheAX12(launch, ANGLE_HALFWAY, TOLERANCE_ANGLE, lastCommTime);
	}

	bool pullUpNet(bool launch)
	{
		static uint32_t lastCommTime;
		return moveTheAX12(launch, ANGLE_UP, TOLERANCE_ANGLE, lastCommTime);
	}

	bool openNet(bool launch)
	{
		return controlerNet.openNet(launch);
	}

	bool closeNet(bool launch)
	{
		return controlerNet.closeNet(launch);
	}

	uint8_t ejectLeftSide(bool launch)
	{
		return controlerNet.ejectLeftSide(launch);
	}

	uint8_t rearmLeftSide(bool launch)
	{
		return controlerNet.rearmLeftSide(launch);
	}

	uint8_t ejectRightSide(bool launch)
	{
		return controlerNet.ejectRightSide(launch);
	}

	uint8_t rearmRightSide(bool launch)
	{
		return controlerNet.rearmRightSide(launch);
	}

	bool funnyAction(bool launch)
	{
		static uint32_t startTime;
		static uint32_t fanStartTime;
		static uint32_t lastCommTime;
		static uint8_t stage;
		if (launch)
		{
			controlerNet.stop();
			stage = 0;
			moveTheAX12(true, ANGLE_UP, TOLERANCE_ANGLE, lastCommTime);
			startTime = millis();
			return false;
		}
		else
		{
			if (stage == 0)
			{
				if (moveTheAX12(false, ANGLE_UP, TOLERANCE_ANGLE, lastCommTime))
				{ // AX12 en place pour bloquer la fus�e
					digitalWrite(PIN_VENTILATEUR, HIGH);
					fanStartTime = millis();
					stage = 1;
				}
			}
			else if (stage == 1)
			{
				if (millis() - fanStartTime > FAN_STARTUP_DURATION)
				{
					moveTheAX12(true, ANGLE_RELEASE, TOLERANCE_ANGLE, lastCommTime);
					stage = 2;
				}
			}

			if (millis() - startTime > FUNNY_ACTION_DURATION)
			{
				digitalWrite(PIN_VENTILATEUR, LOW);
				return true;
			}
			else
			{
				return false;
			}
		}
	}

	uint8_t crossFlipFlop()
	{
		// TODO
	}

	void setAngleNet(uint8_t angle)
	{
		ax12net.goalPositionDegree(angle);
	}

private:
	ControlerNet controlerNet;
	InterfaceAX12 ax12interface;
	DynamixelMotor ax12net;

	bool moveTheAX12(bool launch, uint16_t goalPosition, uint16_t tolerance, uint32_t & lastCommunicationTime)
	{
		if (launch)
		{
			ax12net.goalPositionDegree(goalPosition);
			lastCommunicationTime = millis();
			return false;
		}
		else if (millis() - lastCommunicationTime > 100)
		{
			lastCommunicationTime = millis();
			int32_t currentPosition = ax12net.currentPositionDegree();
			return ABS(currentPosition - (int32_t)goalPosition) <= (int32_t)tolerance;
		}
		else
		{
			return false;
		}
	}
};


#endif

