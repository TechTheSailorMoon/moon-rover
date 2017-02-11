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

package serie.trame;

import serie.Ticket;

import java.nio.ByteBuffer;

import serie.SerialProtocol.OutOrder;

/**
 * Un ordre à envoyer sur la série
 * @author pf
 *
 */

public class Order
{
	public enum Type
	{
		SHORT,
		LONG;
	}

	public ByteBuffer message;
	public Ticket ticket;
	public OutOrder ordre;
	
	public Order(ByteBuffer message, OutOrder ordre, Ticket ticket)
	{
		this.message = message;
		this.ticket = ticket;
		this.ordre = ordre;
	}

	public Order(ByteBuffer message, OutOrder ordre)
	{
		this(message, ordre, new Ticket());
		this.message.flip();
	}

	public Order(OutOrder ordre)
	{
		this(null, ordre, new Ticket());
	}

	public Order(OutOrder ordre, Ticket t)
	{
		this(null, ordre, t);
	}

}
