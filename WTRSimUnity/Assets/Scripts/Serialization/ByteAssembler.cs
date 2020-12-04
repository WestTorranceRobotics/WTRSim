using System;

public interface ByteAssembler
{
    byte[] getBytes(Object[] objs, long packetsSent, long packetsReceived);
}