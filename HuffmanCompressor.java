import java.util.*;

public class HuffmanCompressor {
    public static class Node{
        char ch;
        int freq;
        Node left, right;

        Node(char ch, int freq){
            this.ch = ch;
            this.freq = freq;
        }

        Node(int freq, Node left, Node right){
            this.freq = freq;
            this.left = left;
            this.right = right;
        }
    }

    public static Map<Character, String> huffmanCodes = new HashMap<>();
    public static Map<String, Character> reverseCodes = new HashMap<>();
    public static class CompressionResult{
        public String compressedString;
        public Map<Character, String> codeTable;

        CompressionResult(String compressedString, Map<Character, String> codeTable){
            this.compressedString = compressedString;
            this.codeTable = codeTable;
        }
    }

    public static CompressionResult compress(String text){
        Map<Character, Integer> freqMap = new HashMap<>();
        for(int i = 0; i <text.length(); i++){
            char ch = text.charAt(i);
            if (freqMap.containsKey(ch)) {
                freqMap.put(ch, freqMap.get(ch)+1);
            }
            else{
                freqMap.put(ch, 1);
            }
        }

        List<Node> nodeList = new ArrayList<>();
        Iterator<Map.Entry<Character, Integer>> freqIter = freqMap.entrySet().iterator();
        while (freqIter.hasNext()) {
            Map.Entry<Character, Integer> entry = freqIter.next();
            nodeList.add(new Node(entry.getKey(), entry.getValue()));
        }

        while (nodeList.size() > 1) {
            int min1 = 0, min2 = 1;
            if(nodeList.get(min2).freq < nodeList.get(min1).freq){
                int temp = min1;
                min1 = min2;
                min2 = temp;
            }
            for (int i = 2; i < nodeList.size(); i++){
                int freq = nodeList.get(i).freq;
                if (freq < nodeList.get(min1).freq) {
                    min2 = min1;
                    min1 = i;
                }
                else if (freq < nodeList.get(min2).freq){
                    min2 = i;
                }
            }

            Node left = nodeList.get(min1);
            Node right = nodeList.get(min2);

            Node parent = new Node(left.freq + right.freq, left, right);

            if(min1 > min2) {
                nodeList.remove(min1);
                nodeList.remove(min2);
            }
            else{
                nodeList.remove(min2);
                nodeList.remove(min1);
            }

            nodeList.add(parent);
        }

        Node root = nodeList.get(0);
        buildCodes(root, "");

        StringBuilder compressed = new StringBuilder();
        for(int i = 0; i < text.length(); i++){
            compressed.append(huffmanCodes.get(text.charAt(i)));
        }
        return new CompressionResult(compressed.toString(), huffmanCodes);
    }

    public static void buildCodes(Node node, String code){
        if (node == null){
            return;
        }
        if (node.left == null && node.right == null){
            huffmanCodes.put(node.ch, code);
            reverseCodes.put(code, node.ch);
        }
        buildCodes(node.left, code + "0");
        buildCodes(node.right, code + "1");
    }

    public static String decompress(String compressedText, Map<Character, String> codeTable){

        reverseCodes.clear();
        Iterator<Map.Entry<Character, String>> iter = codeTable.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Character, String> entry = iter.next();
            reverseCodes.put(entry.getValue(), entry.getKey());
        }

        StringBuilder result = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        for (int i = 0; i < compressedText.length(); i++){
            temp.append(compressedText.charAt(i));
            if (reverseCodes.containsKey(temp.toString())){
                result.append(reverseCodes.get(temp.toString()));
                temp.setLength(0);
            }
        }
        return result.toString();
    }

    public static void main(String[] args){
        String input = "Hello World";
        CompressionResult result = compress(input);

        System.out.println("Compressed String:");
        System.out.println(result.compressedString);

        System.out.println("\nSymbol\tCode");
        Iterator<Map.Entry<Character, String>> it = result.codeTable.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<Character, String> entry = it.next();
            if (entry.getKey() == ' '){
                System.out.println("space\t" + entry.getValue());
            }
            else{
                System.out.println(entry.getKey() + "\t" + entry.getValue());
            }
        }

        String output = decompress(result.compressedString, result.codeTable);
        System.out.println("\nDecompressed String:");
        System.out.println(output);
    }
}
