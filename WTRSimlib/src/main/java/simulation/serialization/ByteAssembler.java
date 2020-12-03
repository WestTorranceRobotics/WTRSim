package simulation.serialization;

public interface ByteAssembler {
    public byte[] getBytes(Object[] objs, long packetsSent, long packetsReceived);
}