import java.util.*;
import java.io.*;
class Node implements Comparable<Node>
{
    public char data;
    public int frequency;
    public Node left;
    public Node right;
    public Node(Character dat, int freq)
    {
        data = dat;
        frequency = freq;
        left = null;
        right = null;
    }

    public Node(Character dat)
    {
        data = dat;
        left = null;
        right = null;
    }

    @Override
    public int compareTo(Node b)
    {
        return Integer.compare(this.frequency, b.frequency);
    }

    public String toString()
    {
        String s = "";
        if(data == 0)
        {
            s += "_";
        }
        else
        {
            s += data;
        }
        return "" + s;

    }
}

public class HuffmanTree
{

    public static HashMap<Character, Integer> getFreqMap(String fileName) throws Exception
    {
        Scanner scan = new Scanner(new File(fileName + ".txt"));
        scan.useDelimiter("");
        HashMap<Character, Integer> freq = new HashMap<>();
        while(scan.hasNextLine())
        {
            char a = scan.next().charAt(0);
            if(freq.containsKey(a))
            {
                int frequency = freq.get(a);
                freq.replace(a, ++frequency);
            }
            else
            {
                freq.put(a, 1);
            }

        }
        return freq;
    }

    public static Node buildHuffmanTree(HashMap<Character, Integer> freqMap)
    {
        Node root;
        PriorityQueue<Node> p = new PriorityQueue<>(new HuffmanTreeComparator());
        int sum = 0;
        ArrayList<Character> keys =  new ArrayList<>(freqMap.keySet());
        for(int i = 0; i < freqMap.size(); i++)
        {
            p.add(new Node((char) keys.get(i), freqMap.get(keys.get(i))));
            sum += freqMap.get(keys.get(i));
        } 

        do
        {
            Node a = p.remove();
            Node b = p.remove();
            Node c = new Node((char) 0, a.frequency + b.frequency);
            root = c; 
            c.left = a;
            c.right = b;
            p.add(c);            
        }
        while(root.frequency != sum);
        return root;
    }

    public static HashMap<Character, String> getBitStringMap(Node root) 
    {
        HashMap<Character, String> bitString = new HashMap<>();
        String s = "";
        traverseBitString(root, s, bitString);
        return bitString;
    }

    public static void traverseBitString(Node node, String st, HashMap<Character, String> h)
    {
        if(node.data != (char) 0)
        {
            h.put(node.data, st);
            return;
        }
        else
        {
            traverseBitString(node.left, st + 0, h);
            traverseBitString(node.right,st + 1, h);
        }
    }

    public static void writeCompressedFile(String fileName, HashMap<Character, String> bitMap) throws Exception
    {
        Scanner scan = new Scanner(new File(fileName + ".txt"));
        scan.useDelimiter("");
        BitOutputStream b = new BitOutputStream(fileName + ".short");
        while(scan.hasNext())
        {
            char s = scan.next().charAt(0);
            String bits = bitMap.get(s);
            for(int i = 0; i < bits.length(); i++)
            {
                b.writeBit(Integer.parseInt(bits.charAt(i) + ""));
            }
        }

        b.close();
    }

    public static void writeTreeFile(String fileName, HashMap<Character, String> bitMap) throws Exception
    {
        Scanner scan = new Scanner(new File(fileName + ".txt"));
        scan.useDelimiter("");
        FileWriter writer = new FileWriter(fileName + ".code");
        BufferedWriter buff = new BufferedWriter(writer);
        ArrayList<Character> chars = new ArrayList<>();
        while(scan.hasNext())
        {
            char s = scan.next().charAt(0);
            if(!chars.contains(s))
            {
                chars.add(s);
                buff.write((int) s);
                buff.newLine();
                buff.write(bitMap.get(s));
                buff.newLine();
            }
        }
        buff.close();
    }

    public static Node rebuildHuffmanTree(String fileName) throws Exception
    {
        Scanner scan = new Scanner(new File(fileName + ".code"));
        scan.useDelimiter("");
        Node root = new Node((char) 0);

        while(scan.hasNextLine())
        {
            Node r = root;
            char c = (char) scan.next().charAt(0);
            scan.nextLine();
            String bit = scan.nextLine();

            int i = 0;
            for(i = 0; i < bit.length() - 1; i++)
            {
                if(bit.charAt(i) == '0')
                {
                    if(r.left == null)
                    {
                        r.left = new Node((char) 0);
                        r = r.left;
                    }
                    else
                    {
                        r = (Node) r.left;
                    }
                }
                else if(bit.charAt(i) == '1')
                {
                    if(r.right == null)
                    {
                        r.right = new Node((char) 0);
                        r = r.right;
                    }
                    else
                    {
                        r = (Node) r.right;
                    }
                }
            }
            if(bit.charAt(i) == '0')
            {
                r.left = new Node((char) c);
            }
            else
            {
                r.right = new Node((char) c);
            }
        }

        return root;
    }

    public static void decompressFile(String fileName, Node root) throws Exception
    {
        String bits = "";
        BitInputStream b = new BitInputStream(fileName + ".short");
        FileWriter writer = new FileWriter("new_" + fileName + ".code");
        BufferedWriter buff = new BufferedWriter(writer);
        Node r = root;
        while(true)
        {
            int num = b.readBit();     
            if(num == 0)
            {
                bits += "0";
                if(r.left.data == (char) 0)
                {
                    r = r.left;
                }
                else
                {
                    r = r.left;
                    if(r.data == '*')
                    {
                        break;
                    }
                    buff.write(r.data);
                    r = root;    
                }
            }
            else if(num == 1)
            {
                bits += 1;
                if(r.right.data == (char) 0)
                {
                    r = r.right;
                }
                else
                {
                    r = r.right;
                    if(r.data == '*')
                    {
                        break;
                    }
                    buff.write(r.data);
                    r = root;
                }
            }
        }
        buff.close();
    }

    public static void main (String[] args) throws Exception
    {
        HashMap<Character, Integer> freq = getFreqMap("Hamlet");
        Node r = buildHuffmanTree(freq);
        HashMap<Character, String> bit = getBitStringMap(r);
        writeCompressedFile("Hamlet", bit);
        writeTreeFile("Hamlet", bit);
        Node r2 = rebuildHuffmanTree("Hamlet");
        decompressFile("Hamlet", r2);
        Scanner scan = new Scanner(new File("new_Hamlet.code"));
        while(scan.hasNextLine())
        {
            String s = scan.nextLine();
            System.out.println(s);
        }
    }
}