using System;
using System.Collections.Generic;

public interface ByteDisassembler
{
    List<Object> getData(byte[] bytes);
}
