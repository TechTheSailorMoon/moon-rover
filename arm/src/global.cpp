#include "global.h"

bool isSymmetry;
bool marcheAvant;
Uart<2> serial_rb;
Uart<1> serial_ax12;
double x_odo, y_odo; // abscisse et ordonn�e exprim�es en mm
double orientation_odo; // exprim� en radians
SemaphoreHandle_t serial_rb_mutex = xSemaphoreCreateMutex();
double cos_orientation_odo, sin_orientation_odo;
bool asserEnable;
