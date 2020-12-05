package simulation.serialization;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.gson.Gson;

/**
 * PacketAssembler is responsible for preparing packets to be sent.
 */
public class PacketAssembler implements ByteAssembler {
    private String id = "WTRJ"; //The sender id.
    private byte[] preData; //Bytes of preData, specifically the id and metaData bytes length.
    private byte[] metaDataBytes; //Bytes of metaData.
    private byte[] bodyData; //Bytes of bodyData.
    private Gson jsonBuilder; //Object used to build Json.
    private byte[] idBytes; //Bytes representing id.
    private byte[] metaDataLengthBytes; //Bytes representing the length of metaData.

    /**
     * Constructor for PacketAssembler.
     */
    public PacketAssembler() {
        jsonBuilder = new Gson();
        idBytes = new byte[4];
        metaDataLengthBytes = new byte[2];

        for (int i = 0; i < idBytes.length; i++) {
            idBytes[i] = (byte) id.charAt(i);
        }
    }


    @Override
    public byte[] getBytes(Object[] objs, long packetsSent, long packetsReceived) {

        //bodyData serialized
        bodyData = jsonBuilder.toJson(objs).getBytes();
        
        //metaData serialized
        MetaData metaData = new MetaData(packetsSent, packetsReceived);
        metaDataBytes = jsonBuilder.toJson(metaData).getBytes();
       
        //metaDataLength serialized
        metaDataLengthBytes[0] =  (byte) ((metaDataBytes.length >> 8) & 0xFF);
        metaDataLengthBytes[1] =  (byte) (metaDataBytes.length & 0xFF);

        //preData assembled
        preData = new byte[metaDataLengthBytes.length + idBytes.length];
        System.arraycopy(metaDataLengthBytes, 0, preData, 0, metaDataLengthBytes.length);
        System.arraycopy(idBytes, 0, preData, metaDataLengthBytes.length, idBytes.length);

        //datagram assembled
        byte[] datagram = new byte[ preData.length + metaDataBytes.length + bodyData.length];
        System.arraycopy(preData, 0, datagram, 0, preData.length);
        System.arraycopy(metaDataBytes, 0, datagram, preData.length, metaDataBytes.length);
        System.arraycopy(bodyData, 0, datagram, preData.length + 
            metaDataBytes.length, bodyData.length
        );
       
        return datagram;
    }

    private class MetaData {
        long sent;
        long received;
        long sysTime;

        MetaData(long packetsSent, long packetsReceived) {
            sent = packetsSent;
            received = packetsReceived;
            sysTime = System.currentTimeMillis();
        }
    }
}

