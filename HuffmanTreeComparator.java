import java.util.*;
import java.io.*;
public class HuffmanTreeComparator implements Comparator<Node>
{
    @Override
    public int compare(Node a, Node b)
    {
        return Integer.compare(a.frequency, b.frequency);
    }
}
    