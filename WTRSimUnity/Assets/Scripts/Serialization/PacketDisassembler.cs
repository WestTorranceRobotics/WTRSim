using System;
using System.Text;
using System.Collections.Generic;

public class PacketDisassembler : ByteDisassembler
{
    List<Object> data;

    byte[] idBytes;
    byte[] metaDataLengthBytes;
    volatile int packetsRejected;

    /**
     * Constructor for PacketDisassembler.
     */
    public PacketDisassembler()
    {
        idBytes = new byte[ 4 ];
        metaDataLengthBytes = new byte[ 2 ];
        data = new List<Object>();
    }

    public List<Object> getData(byte[] bytes)
    {
        data.Clear();

        //idBytes extracted
        Array.Copy(bytes, 2, idBytes, 0, idBytes.Length);

        //metaDataLength extracted
        Array.Copy(bytes, 0, metaDataLengthBytes, 0, metaDataLengthBytes.Length);
        if ( BitConverter.IsLittleEndian ) {
            Array.Reverse(metaDataLengthBytes);
        }
        int metaDataLength = BitConverter.ToInt16(metaDataLengthBytes, 0);


        //jsonBytes extracted
        byte[] jsonBytes = new byte[ bytes.Length - 6 ];
        Array.Copy(bytes, 6, jsonBytes, 0, jsonBytes.Length);

        //metaDataBytes extracted
        byte[] metaDataBytes = new byte[ metaDataLength ];
        Array.Copy(jsonBytes, 0, metaDataBytes, 0, metaDataBytes.Length);

        //bodyDataBytes extracted
        byte[] bodyDataBytes = new byte[ jsonBytes.Length - metaDataLength ];
        Array.Copy(jsonBytes, metaDataBytes.Length, bodyDataBytes, 0, bodyDataBytes.Length);

        //Adding all data to data ArrayList
        data.Add(metaDataLength);
        data.Add(Encoding.UTF8.GetString(idBytes));
        data.Add(Encoding.UTF8.GetString(metaDataBytes));
        data.Add(Encoding.UTF8.GetString(bodyDataBytes));

        return data;
    }
}