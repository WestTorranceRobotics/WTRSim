using Object = System.Object;
using System.Text;
using System.Linq;
using DateTimeOffset = System.DateTimeOffset;
using UnityEngine;
using Newtonsoft.Json;
using System;

public class DatagramAssembler: ByteAssembler
{
    private string id = "WTRU";
    private byte[] preData; //Contains metadata length and sender id.
    private byte[] metaDataBytes; //Contains metadata.
    private byte[] bodyData; //Contains body data.

    public DatagramAssembler()
    {
    }

    public byte[] getBytes(Object[] objs, long packetsSent, long packetsReceived)
    {

        bodyData = Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(objs));

        byte[] idBytes = new byte[ 4 ];
        for ( int i = 0; i < idBytes.Length; i++ )
        {
            idBytes[ i ] = (byte) id[ i ];
        }

        MetaData metaData = new MetaData(packetsSent, packetsReceived);
        metaDataBytes = Encoding.UTF8.GetBytes(JsonUtility.ToJson(metaData));

        byte[] metaDataBytesLength = new byte[ 2 ];
        metaDataBytesLength[ 0 ] = (byte) ((metaDataBytes.Length >> 8) & 0xFF);
        metaDataBytesLength[ 1 ] = (byte) (metaDataBytes.Length & 0xFF);
 
        preData = metaDataBytesLength.Concat(idBytes).ToArray();

        byte[] datagram;
        datagram = preData.Concat(metaDataBytes.Concat(bodyData).ToArray()).ToArray();
    
        return datagram;
    }

    public class MetaData
    {
        public long sent;
        public long received;
        public long sysTime;

        public MetaData(long packetsSent, long packetsReceived)
        {
            sent = packetsSent;
            received = packetsReceived;
            sysTime = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
            Debug.Log(sent);
        }
    }

}

