package com.trilead.ssh2.packets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.junit.jupiter.api.Test;

public class PacketSessionExecCommandTest
{
	/**
	 * Servers pass the exec command to a shell, which reads it as UTF-8: é, ő, ű and an emoji must reach the server
	 * as their UTF-8 bytes, not as ISO-8859-1 (é as 0xE9, the others as '?').
	 */
	@Test
	public void commandIsSentAsUtf8() throws IOException
	{
		String command = "echo 'é ő ű \uD83D\uDE80'";
		byte[] expected = bytes('e', 'c', 'h', 'o', ' ', '\'',
				0xC3, 0xA9, ' ',
				0xC5, 0x91, ' ',
				0xC5, 0xB1, ' ',
				0xF0, 0x9F, 0x9A, 0x80, '\'');

		TypesReader tr = new TypesReader(new PacketSessionExecCommand(7, true, command).getPayload());

		assertEquals(Packets.SSH_MSG_CHANNEL_REQUEST, tr.readByte());
		assertEquals(7, tr.readUINT32());
		assertEquals("exec", tr.readString());
		assertTrue(tr.readBoolean());
		assertArrayEquals(expected, tr.readByteString());
		assertEquals(0, tr.remain());
	}

	private static byte[] bytes(int... values)
	{
		byte[] result = new byte[values.length];
		for (int i = 0; i < values.length; i++)
			result[i] = (byte) values[i];
		return result;
	}
}
