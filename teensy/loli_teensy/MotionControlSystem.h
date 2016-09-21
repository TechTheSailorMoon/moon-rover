// MotionControlSystem.h

#ifndef _MOTIONCONTROLSYSTEM_h
#define _MOTIONCONTROLSYSTEM_h

#if defined(ARDUINO) && ARDUINO >= 100
	#include "arduino.h"
#else
	#include "WProgram.h"
#endif

#include "Singleton.h"
#include "Motor.h"
#include "PID.h"
#include "Average.h"
#include "Encoder.h"
#include "Position.h"
#include "Trajectory.h"
#include "DirectionController.h"
#include "Log.h"
#include "BlockingMgr.h"
#include <math.h>


#define FREQ_ASSERV			1000	// Fr�quence d'asservissement (en Hz)
#define AVERAGE_SPEED_SIZE	50		// Nombre de valeurs � utiliser dans le calcul de la moyenne glissante permettant de lisser la mesure de vitesse


class MotionControlSystem : public Singleton<MotionControlSystem>
{
private:
	Motor motor;
	Encoder leftMotorEncoder;
	Encoder rightMotorEncoder;

	Encoder leftFreeEncoder;
	Encoder rightFreeEncoder;

	DirectionController & direction;

	// Trajectoire en cours de parcours
	TrajectoryPoint currentTrajectory[UINT8_MAX + 1];
	
	// Point de la trajectoire courante sur lequel le robot se situe actuellement
	volatile uint8_t trajectoryIndex;

	// Prochain point d'arr�t sur la trajectoire courante. Tant qu'aucun point d'arr�t n'a �t� re�u, nextStopPoint vaut MAX_UINT_16.
	volatile uint16_t nextStopPoint;

	// Position absolue du robot sur la table (en mm et radians)
	volatile Position position;


	/*
	* 		D�finition des variables d'�tat du syst�me (position, vitesse, consigne, ...)
	*
	* 		Les unit�s sont :
	* 			Pour les distances		: ticks
	* 			Pour les vitesses		: ticks/seconde
	* 			Ces unit�s seront vraies pour une fr�quence d'asservissement �gale � FREQ_ASSERV
	*/

	//	Asservissement en vitesse du moteur droit
	PID rightSpeedPID;
	volatile int32_t rightSpeedSetpoint;	// ticks/seconde
	volatile int32_t currentRightSpeed;		// ticks/seconde
	volatile int32_t rightPWM;
	BlockingMgr rightMotorBlockingMgr;

	//	Asservissement en vitesse du moteur gauche
	PID leftSpeedPID;
	volatile int32_t leftSpeedSetpoint;		// ticks/seconde
	volatile int32_t currentLeftSpeed;		// ticks/seconde
	volatile int32_t leftPWM;
	BlockingMgr leftMotorBlockingMgr;

	//	Asservissement en position : translation
	//  (Ici toutes les grandeurs sont positives, le sens de d�placement sera donn� par maxMovingSpeed)
	PID translationPID;
	volatile int32_t translationSetpoint;	// ticks
	volatile int32_t currentDistance;		// ticks
	volatile int32_t movingSpeedSetpoint;	// ticks/seconde
	
	StoppingMgr endOfMoveMgr;
	volatile int32_t currentMovingSpeed;	// ticks/seconde

	//  Asservissement sur trajectoire
	volatile float curvatureOrder;	// Consigne de courbure, en m^-1
	float curvatureCorrectorK1;		// Coefficient du facteur "erreur de position"
	float curvatureCorrectorK2;		// Coefficient du facteur "erreur d'orientation"
	
	//  Vitesse (alg�brique) de translation maximale : une vitesse n�gative correspond � une marche arri�re
	volatile int32_t maxMovingSpeed;	// en ticks/seconde

	//  Pour le calcul de l'acc�l�ration :
	volatile int32_t previousMovingSpeed;	// en ticks.s^-2

	//  Acc�l�ration maximale (variation maximale de movingSpeedSetpoint)
	volatile int32_t maxAcceleration;	// ticks*s^-2

	//	Pour faire de jolies courbes de r�ponse du syst�me, la vitesse moyenne c'est mieux !
	Average<int32_t, AVERAGE_SPEED_SIZE> averageLeftSpeed;
	Average<int32_t, AVERAGE_SPEED_SIZE> averageRightSpeed;
	Average<int32_t, AVERAGE_SPEED_SIZE> averageCurrentSpeed;

public:
	// Type d�crivant l'�tat du mouvement
	enum MovingState
	{
		STOPPED,		// Robot � l'arr�t, � la position voulue.
		MOVE_INIT,		// L'ordre de mouvement a �t� re�u, mais le robot n'envoie pas encore un PWM aux moteurs de propulsion (il attend d'avoir les roues de direction en position)
		MOVING,			// Robot en mouvent vers la position voulue.
		EXT_BLOCKED,	// Robot bloqu� par un obstacle ext�rieur (les roues patinent).
		INT_BLOCKED,	// Roues du robot bloqu�es.
		EMPTY_TRAJ		// La trajectoire courante est termin�e, le dernier point n'�tant pas un point d'arr�t.
	};
	
private:
	volatile MovingState movingState;

	// Variables d'activation des diff�rents PID
	volatile bool positionControlled;	//  Asservissement en position
	volatile bool leftSpeedControlled;	//	Asservissement en vitesse � gauche
	volatile bool rightSpeedControlled;	//	Asservissement en vitesse � droite
	volatile bool pwmControlled;		//	Mise � jour des PWM gr�ce � l'asservissement en vitesse


public:
	MotionControlSystem();

	/* Asservissement (fonction � appeller dans l'interruption associ�e) */
	void control();
private:
	/* Mise � jour des variables :
		position
		currentRightSpeed (maj + filtrage)
		currentLeftSpeed (maj + filtrage)
		currentDistance
		currentMovingSpeed (maj + filtrage) */
	void updateSpeedAndPosition();

	/* Mise � jour des variables :
		trajectoryIndex
		nextStopPoint
		currentDistance (si trajectoryIndex a �t� incr�ment�)
		translationSetpoint (si nextStopPoint a �t� modifi�) */
	void updateTrajectoryIndex();

	void manageStop();
	void manageBlocking();
public:

	/* Activation et d�sactivation de l'asserv */
	void enablePositionControl(bool);
	void enableLeftSpeedControl(bool);
	void enableRightSpeedControl(bool);
	void enablePwmControl(bool);

	/* Gestion des d�placements */
	void addTrajectoryPoint(const TrajectoryPoint &, uint8_t);
	MovingState getMovingState() const;
	void gotoNextStopPoint();
	void stop();
	void setMaxMovingSpeed(int32_t);
	int32_t getMaxMovingSpeed() const;

	/* Setters et getters des constantes d'asservissement */
	void setTranslationTunings(float, float, float);
	void setLeftSpeedTunings(float, float, float);
	void setRightSpeedTunings(float, float, float);
	void setTrajectoryTunings(float, float);
	void getTranslationTunings(float &, float &, float &) const;
	void getLeftSpeedTunings(float &, float &, float &) const;
	void getRightSpeedTunings(float &, float &, float &) const;
	void getTrajectoryTunings(float &, float &) const;

	/* Setter et getter de la position */
	void setPosition(const Position &);
	void getPosition(Position &) const;
	uint8_t getTrajectoryIndex() const;
	void resetPosition(void);
};


#endif

