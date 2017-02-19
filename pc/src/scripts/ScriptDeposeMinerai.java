/*
Copyright (C) 2013-2017 Pierre-François Gimenez

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

package scripts;

import exceptions.UnableToMoveException;
import pathfinding.GameState;
import robot.Robot;
import utils.Log;

/**
 * Le script qui dépose le minerai
 * @author pf
 *
 */

public class ScriptDeposeMinerai extends Script
{
	public ScriptDeposeMinerai(Log log)
	{
		super(log);
	}

	@Override
	public void setUpCercleArrivee()
	{
		// TODO
	}

	@Override
	protected void run(GameState<? extends Robot> state) throws InterruptedException, UnableToMoveException
	{
		// TODO
	}
	
	@Override
	protected void termine(GameState<? extends Robot> state) throws InterruptedException, UnableToMoveException
	{
	}
	
	@Override
	public boolean equals(Object other)
	{
		return other instanceof ScriptDeposeMinerai; // de toute façon, il n'y a qu'un seul script de ce type
	}
	
	@Override
	public int hashCode()
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return "DEPOSE";
	}
}