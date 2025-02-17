package named_types;

public class CabinNumber
{
    private int m_CabinNumber;
    public CabinNumber(int cabinNumber) {m_CabinNumber = cabinNumber;}
    public int get() {return m_CabinNumber;}

    @Override
    public boolean equals(Object obj) {

        if(obj == null) return false;
        if(obj == this) return true;
        if(getClass() != obj.getClass()) return false;
        CabinNumber cn = (CabinNumber) obj;
        return (cn.m_CabinNumber == this.m_CabinNumber);
    }

    @Override
    public String toString() {  return "" + m_CabinNumber;}

    @Override
    public int hashCode() {
        int hash = this.m_CabinNumber;
        hash ^= (hash << 13);
        hash ^= (hash >>> 17);
        hash ^= (hash << 5);
        return hash;
    }
}
