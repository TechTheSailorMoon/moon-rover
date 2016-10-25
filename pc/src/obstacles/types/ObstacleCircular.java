/*
Copyright (C) 2016 Pierre-François Gimenez

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
*/

package obstacles.types;

import java.awt.Color;
import java.awt.Graphics;

import graphic.Fenetre;
import graphic.printable.Couleur;
import graphic.printable.Layer;
import robot.RobotReal;
import utils.Vec2RO;
import utils.Vec2RW;

/**
 * Obstacle circulaire
 * @author pf
 *
 */
public class ObstacleCircular extends Obstacle
{
	// le Vec2 "position" indique le centre de l'obstacle
	
	// rayon de cet obstacle
	public final int radius;
	public final int squared_radius;
	
	public ObstacleCircular(Vec2RO position, int rad)
	{
		super(position);
		this.radius = rad;
		squared_radius = rad * rad;
	}

	public ObstacleCircular(Vec2RO position, int rad, Couleur c)
	{
		super(position,c);
		this.radius = rad;
		squared_radius = rad * rad;
	}

	@Override
	public String toString()
	{
		return super.toString()+", rayon: "+radius;
	}

	@Override
	public double squaredDistance(Vec2RO position)
	{
		double out = Math.max(0, position.distance(this.position) - radius);
		return out * out;
	}

	@Override
	public boolean isColliding(ObstacleRectangular o)
	{
		// Calcul simple permettant de vérifier les cas absurdes où les obstacles sont loin l'un de l'autre
		if(position.squaredDistance(o.centreGeometrique) >= (radius+o.demieDiagonale)*(radius+o.demieDiagonale))
			return false;
		return o.squaredDistance(position) < radius*radius;
	}

	@Override
	public void print(Graphics g, Fenetre f, RobotReal robot)
	{
		if(c != null)
			g.setColor(c);
		if(radius <= 0)
			g.fillOval(f.XtoWindow(position.getX())-5, f.YtoWindow(position.getY())-5, 10, 10);
		else
			g.fillOval(f.XtoWindow(position.getX()-radius), f.YtoWindow(position.getY()+radius), f.distanceXtoWindow((radius)*2), f.distanceYtoWindow((radius)*2));
	}

	/**
	 * Renvoie la position "nearestPos" la plus proche de "pos" tout en étant sur le cercle de centre "position" et de rayon "radius+distance"
	 * Est utilisé pour aller sur les cratères
	 * @param pos
	 * @param nearestPos
	 * @param distance
	 */
	public void getNearestPosition(Vec2RO pos, Vec2RW nearestPos, double distance)
	{
		pos.copy(nearestPos);
		nearestPos.minus(position);
		nearestPos.scalar((radius+distance)/position.distance(pos));
		nearestPos.plus(position);
	}

	/**
	 * Donne l'orientation à avoir au point donné par getNearestPosition
	 * @param position
	 * @return
	 */
	public double getNearestOrientation(Vec2RO pos)
	{
		return Math.atan2(pos.getY()-position.getY(), pos.getX()-position.getX());
	}
}
