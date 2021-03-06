#ifndef UTILS_H
#define UTILS_H

#include "math.h"
#include "communication_setup.h"

#define MIN(x,y) (((x)<(y))?(x):(y))
#define MAX(x,y) (((x)>(y))?(x):(y))
#define ABS(x) (((x) > 0) ? (x) : -(x))
#define CONSTRAIN(x,y,z) ( ((x)<(y))?(y):( ((x)>(z))?(z):(x) ) )
#define debug_serial_free	((Stream*)&Serial != (Stream*)&SERIAL_HL && (Stream*)&Serial != (Stream*)&SERIAL_AX)

#ifdef __cplusplus
#ifndef __INTELLISENSE__
extern "C" 
{
#endif
#endif

int modulo(int nombre, int modulo);
float fmodulo(float nombre, float modulo);
float square(float x);

#ifdef __cplusplus
#ifndef __INTELLISENSE__
}
#endif
#endif

#endif
