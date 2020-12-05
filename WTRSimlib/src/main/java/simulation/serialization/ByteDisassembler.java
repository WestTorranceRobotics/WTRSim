package simulation.serialization;

import java.util.ArrayList;

public interface ByteDisassembler {
    ArrayList<Object> data = new ArrayList<Object>();
    public ArrayList<Object> getData(byte[] bytes);
}
