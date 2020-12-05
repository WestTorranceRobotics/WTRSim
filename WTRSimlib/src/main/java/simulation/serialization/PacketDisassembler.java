package simulation.serialization;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PacketDisassembler implements ByteDisassembler {
    byte[] idBytes;
    
    byte[] metaDataLengthBytes;

    volatile AtomicInteger packetsRejected; 

    /**
     * Constructor for PacketDisassembler.
     */
    public PacketDisassembler() {
        idBytes = new byte[4];
        metaDataLengthBytes = new byte[2];
        packetsRejected = new AtomicInteger();
    }

    @Override
    public ArrayList<Object> getData(byte[] bytes) {
        data.clear();

        //idBytes extracted
        System.arraycopy(bytes, 2, idBytes, 0, idBytes.length);
        
        //metaDataLength extracted
        System.arraycopy(bytes, 0, metaDataLengthBytes, 0, metaDataLengthBytes.length);
        int metaDataLength = new BigInteger(metaDataLengthBytes).intValue();

        //jsonBytes extracted
        byte[] jsonBytes = new byte[bytes.length - 6];
        System.arraycopy(bytes, 6, jsonBytes, 0, jsonBytes.length);
        
        //metaDataBytes extracted
        byte[] metaDataBytes = new byte[metaDataLength];
        System.arraycopy(jsonBytes, 0, metaDataBytes, 0, metaDataBytes.length);

        //bodyDataBytes extracted
        byte[] bodyDataBytes = new byte[jsonBytes.length - metaDataLength];
        System.arraycopy(jsonBytes, metaDataBytes.length, bodyDataBytes, 0, bodyDataBytes.length);

        //Adding all data to data ArrayList
        data.add(metaDataLength);
        data.add(new String(idBytes));
        data.add(new String(metaDataBytes));
        data.add(new String(bodyDataBytes));
        
        return data;
    }
}