package com.trilead.ssh2.packets;

import java.nio.charset.StandardCharsets;

/**
 * PacketSessionExecCommand.
 *
 * @author Christian Plattner, plattner@trilead.com
 * @version $Id: PacketSessionExecCommand.java,v 1.1 2007/10/15 12:49:55 cplattne Exp $
 */
public class PacketSessionExecCommand
{
	byte[] payload;

	public int recipientChannelID;
	public boolean wantReply;
	public String command;

	public PacketSessionExecCommand(int recipientChannelID, boolean wantReply, String command)
	{
		this.recipientChannelID = recipientChannelID;
		this.wantReply = wantReply;
		this.command = command;
	}

	public byte[] getPayload()
	{
		if (payload == null)
		{
			TypesWriter tw = new TypesWriter();
			tw.writeByte(Packets.SSH_MSG_CHANNEL_REQUEST);
			tw.writeUINT32(recipientChannelID);
			tw.writeString("exec");
			tw.writeBoolean(wantReply);
			// Servers hand the command to a shell that reads UTF-8; writeString(String) would send ISO-8859-1.
			byte[] commandBytes = command.getBytes(StandardCharsets.UTF_8);
			tw.writeString(commandBytes, 0, commandBytes.length);
			payload = tw.getBytes();
		}
		return payload;
	}
}
