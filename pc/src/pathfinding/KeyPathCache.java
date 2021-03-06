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

package pathfinding;

import robot.Cinematique;
import robot.Robot;
import scripts.ScriptNames;

/**
 * Clé pour le cache du pathfinding
 * 
 * @author pf
 *
 */

public class KeyPathCache
{
	public ScriptNames s;
	public Cinematique arrivee;
	public final GameState<? extends Robot> chrono;
	public boolean shoot;

	public KeyPathCache(GameState<? extends Robot> chrono, ScriptNames s, boolean shoot)
	{
		this.chrono = chrono;
		this.s = s;
		this.shoot = shoot;
	}

	public KeyPathCache(GameState<? extends Robot> chrono, Cinematique arrivee, boolean shoot)
	{
		this.chrono = chrono;
		this.arrivee = arrivee;
		this.shoot = shoot;
	}

	public int getCinem()
	{
		return chrono.robot.codeForPFCache();
	}

	public KeyPathCache(GameState<? extends Robot> chrono)
	{
		this.chrono = chrono;
	}

	@Override
	public int hashCode()
	{
		if(s != null)
			return (getCinem() + s.hashCode()) * 2 + (shoot ? 1 : 0);
		return ((getCinem() << 10) + arrivee.hashCode()) * 2 + (shoot ? 1 : 0);
	}

	@Override
	public boolean equals(Object o)
	{
		if(s != null)
			return o instanceof KeyPathCache && ((KeyPathCache) o).shoot == shoot && ((KeyPathCache) o).s.equals(s) && ((KeyPathCache) o).getCinem() == getCinem();
		return o instanceof KeyPathCache && ((KeyPathCache) o).shoot == shoot && ((KeyPathCache) o).arrivee.hashCode() == arrivee.hashCode() && ((KeyPathCache) o).getCinem() == getCinem();
	}

	@Override
	public String toString()
	{
		if(s != null)
			return s + "-" + getCinem() + "-" + shoot;
		return arrivee.hashCode() + "-" + getCinem() + "-" + shoot;
	}
}