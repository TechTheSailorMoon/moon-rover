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

package remoteControl;

import java.io.Serializable;

/**
 * Commande de contrôle à distance
 * @author pf
 *
 */

public enum Commandes implements Serializable
{
	SPEED_UP, SPEED_DOWN,
	TURN_RIGHT, TURN_LEFT,
	STOP, 
	RESET_WHEELS,
	SHUTDOWN,
	PING,
	LEVE_FILET, BAISSE_FILET,
	FERME_FILET, OUVRE_FILET,
	EJECTE_GAUCHE, EJECTE_DROITE,
	REARME_GAUCHE, REARME_DROITE;
	
	private Commandes()
	{}
	
	public int code = -1;
	
	public void setCode(int code)
	{
		this.code = code;
	}
	
}
