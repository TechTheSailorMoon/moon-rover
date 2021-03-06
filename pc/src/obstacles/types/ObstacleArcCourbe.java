/*
 * Copyright (C) 2013-2017 Pierre-François Gimenez
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 */

package obstacles.types;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import graphic.Fenetre;
import robot.RobotReal;
import utils.Vec2RO;

/**
 * Obstacle d'un arc de trajectoire courbe
 * Construit à partir de plein d'obstacles rectangulaires
 * 
 * @author pf
 *
 */

public class ObstacleArcCourbe extends Obstacle
{
	private static final long serialVersionUID = -2425339148551754268L;

	public ObstacleArcCourbe()
	{
		super(null);
	}

	public List<ObstacleRectangular> ombresRobot = new ArrayList<ObstacleRectangular>();

	@Override
	public double squaredDistance(Vec2RO position)
	{
		double min = Double.MAX_VALUE;
		for(ObstacleRectangular o : ombresRobot)
		{
			min = Math.min(min, o.squaredDistance(position));
			if(min == 0)
				return 0;
		}
		return min;
	}

	@Override
	public boolean isColliding(ObstacleRectangular obs)
	{
		for(ObstacleRectangular o : ombresRobot)
			if(obs.isColliding(o))
				return true;
		return false;
	}

	@Override
	public void print(Graphics g, Fenetre f, RobotReal robot)
	{
		for(ObstacleRectangular o : ombresRobot)
			o.print(g, f, robot);
	}

	@Override
	public double getTopY()
	{
		double out = ombresRobot.get(0).getTopY();
		for(ObstacleRectangular o : ombresRobot)
			out = Math.max(out, o.getTopY());
		return out;
	}

	@Override
	public double getBottomY()
	{
		double out = ombresRobot.get(0).getBottomY();
		for(ObstacleRectangular o : ombresRobot)
			out = Math.min(out, o.getBottomY());
		return out;
	}

	@Override
	public double getLeftmostX()
	{
		double out = ombresRobot.get(0).getLeftmostX();
		for(ObstacleRectangular o : ombresRobot)
			out = Math.min(out, o.getLeftmostX());
		return out;
	}

	@Override
	public double getRightmostX()
	{
		double out = ombresRobot.get(0).getRightmostX();
		for(ObstacleRectangular o : ombresRobot)
			out = Math.max(out, o.getRightmostX());
		return out;
	}

}
