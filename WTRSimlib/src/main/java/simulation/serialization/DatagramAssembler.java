package simulation.serialization;

import com.google.gson.Gson;

public class DatagramAssembler implements ByteAssembler {
    private String id = "WTRJ";
    private byte[] preData;
    private byte[] metaDataBytes;
    private byte[] bodyData;
    private Gson jsonBuilder;

    public DatagramAssembler() {
        jsonBuilder = new Gson();
    }


    @Override
    public byte[] getBytes(Object[] objs, long packetsSent, long packetsReceived) {

        bodyData = jsonBuilder.toJson(objs).getBytes();

        byte[] idBytes = new byte[4];
        for (int i = 0; i < idBytes.length; i++) {
            idBytes[i] = (byte) id.charAt(i);
        }
        
        MetaData metaData = new MetaData(packetsSent, packetsReceived);
        metaDataBytes = jsonBuilder.toJson(metaData).getBytes();
       

        byte[] metaDataBytesLength = new byte[2];
        metaDataBytesLength[0] =  (byte) ((metaDataBytes.length >> 8) & 0xFF);
        metaDataBytesLength[1] =  (byte) (metaDataBytes.length & 0xFF);

        preData = new byte[metaDataBytesLength.length + idBytes.length];
        System.arraycopy(metaDataBytesLength, 0, preData, 0, metaDataBytesLength.length);
        System.arraycopy(idBytes, 0, preData, metaDataBytesLength.length, idBytes.length);

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

