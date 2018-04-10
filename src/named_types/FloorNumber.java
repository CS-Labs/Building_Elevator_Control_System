package named_types;

public class FloorNumber
{
    private int m_Floor;
    public FloorNumber(int floor) {m_Floor = floor;}
    public int get() {return m_Floor;}

    @Override
    public boolean equals(Object obj) {

        if(obj == null) return false;
        if(obj == this) return true;
        if(getClass() != obj.getClass()) return false;
        FloorNumber fn = (FloorNumber) obj;
        return (fn.m_Floor == this.m_Floor);
    }

}