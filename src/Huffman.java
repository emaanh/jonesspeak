import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Huffman {
	private Node root; 
	private final String file;
	private Map<String, Integer> wordFrequencies;
	private final Map<String, String> huffmanCodes;
	private static int encodingBase = 36;
	private ArrayList<String> invalidWords = new ArrayList<>();
	private String invalidWordString;

	
	public Huffman() throws IOException {
		this.file = "src/1m.txt";
//		this.message = message.toLowerCase();
		fillFrequenciesMap();
		huffmanCodes = new HashMap<>();
	}
	
	public void fillFrequenciesMap() throws IOException {
		String line;
		String word;
		Integer frequency;
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		wordFrequencies = new HashMap<>();
		
		for(int i =0; i<1000000;i++) {
			line = reader.readLine();
			if(line != null && line.matches("^[A-Za-z0-9\\s]+$")) {		
				word = line.replaceAll("(\\w+)\\s\\d+", "$1");
				frequency = Integer.parseInt(line.replaceAll("\\w+\\s(\\d+)", "$1"));			
				wordFrequencies.put(word, frequency);
			}
			
			if(i%100000 ==0) {
				System.out.println(i +" codes created.");
			}
		}
		
		reader.close();
	}
	
	public String encode(String decodedText) {
		invalidWords = new ArrayList<>();

		Queue<Node> queue = new PriorityQueue<>();
		wordFrequencies.forEach((word, frequency) -> 
			queue.add(new Leaf(word, frequency)));
		
		while(queue.size()>1) {
			queue.add(new Node(queue.poll(), queue.poll()));
		}

		generateHuffmanCodes(root = queue.poll(), "");
		
		
		return getEncodedText(decodedText);
	}

	private void generateHuffmanCodes(Node node, String code) {
		
		if(node instanceof Leaf) {
			huffmanCodes.put(((Leaf) node).getWord(), code);
			return;
		}
		
		generateHuffmanCodes(node.getLeftNode(), code.concat("0"));
		generateHuffmanCodes(node.getRightNode(), code.concat("1"));
		
	}
	
	private String getEncodedText(String decodedText) {
		StringBuilder sb = new StringBuilder();
		
		for(String word : decodedText.toLowerCase().split(" ")) {
			String code = huffmanCodes.get(word);
			if(code == null) {
				invalidWords.add(word);
			}
			else {
				sb.append(code);
				}
		}
	
//		return sb.toString();
		return bitsToBase(sb.toString());
	}
	
	public String decode(String encodedText) {
		
		encodedText = baseToBits(encodedText);
	
		StringBuilder sb = new StringBuilder();
		Node current = root;
		for(char character : encodedText.toCharArray()) {
			current = character == '0' ? current.getLeftNode() : current.getRightNode();
			if(current instanceof Leaf) {
				sb.append(((Leaf) current).getWord()+" ");
				current = root;
			}
		}
		
		return sb.toString().trim();
	}

	public static String bitsToBase(String bits) {		
		return (new BigInteger("1"+bits, 2).toString(encodingBase)).toUpperCase();
	}
	
	public static String baseToBits(String base) {
		return  (new BigInteger(base, encodingBase).toString(2)).substring(1);
	}
	
	
	public void printCodes() {
		huffmanCodes.forEach((character, code)-> 
		System.out.println(character + ": "+code));
	}
	
	public String getInvalidWords() {
		invalidWordString = "";
		invalidWords.forEach((word)-> invalidWordString += ", "+word);
		
		if(invalidWordString.equals("")){return "";}
		
		return invalidWordString.substring(2);

	}


}
