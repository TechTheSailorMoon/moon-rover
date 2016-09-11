#ifndef _POSITION_h
#define _POSITION_h

#include <Printable.h>
#include <vector>
#include "Log.h"

class Position : public Printable
{
public:
	Position()
	{
		x = 0;
		y = 0;
		orientation = 0;
	}

	Position(float _x, float _y, float _o)
	{
		x = _x;
		y = _y;
		orientation = _o;
	}

	Position(const std::vector<uint8_t> & data)
	{
		if (data.size() == 5)
		{
			uint16_t _x, _y, _o;
			_x = data.at(0) << 4;
			_x += data.at(1) >> 4;
			_y = (data.at(1) & 0xFF) << 8;
			_y += data.at(2);
			_o = data.at(3) << 8;
			_o += data.at(4);

			x = (float)_x - 1500;
			y = (float)_y;
			orientation = ((float)_o) / 1000;
		}
		else
		{
			x = 0;
			y = 0;
			orientation = 0;
			Log::warning("Position(vector<uint8_t>): initialisation invalide");
		}
	}

	void operator= (volatile const Position & newPosition) volatile
	{
		this->x = newPosition.x;
		this->y = newPosition.y;
		this->orientation = newPosition.orientation;
	}

	std::vector<uint8_t> getVector()
	{
		std::vector<uint8_t> data;

		float rX = x + 1500, rY = y, rO = orientation * 1000;
		if (rX < 0)
			rX = 0;
		else if (rX > 3000)
			rX = 3000;
		if (rY < 0)
			rY = 0;
		else if (rY > 2000)
			rY = 2000;
		if (rO < 0)
			rO = 0;
		else if (rO > 6283)
			rO = 6283;

		uint16_t _x = (uint16_t)rX;
		uint16_t _y = (uint16_t)rY;
		uint16_t _o = (uint16_t)rO;
		
		uint16_t hw_x, lw_x_hw_y, lw_y;
		hw_x = _x & 0x0FF0;
		hw_x = hw_x >> 4;

		lw_x_hw_y = (_x & 0x000F) << 4;
		lw_x_hw_y += (_y & 0x0F00) >> 8;

		lw_y = _y & 0x00FF;

		data.push_back((uint8_t)hw_x);
		data.push_back((uint8_t)lw_x_hw_y);
		data.push_back((uint8_t)lw_y);
		data.push_back((uint8_t)(_o >> 8));
		data.push_back((uint8_t)_o);

		return data;
	}

	size_t printTo(Print& p) const
	{
		return p.printf("%g_%g_%g", x, y, orientation);
	}

	float x; // mm
	float y; // mm
	float orientation; // radians
};

#endif
