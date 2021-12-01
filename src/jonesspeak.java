import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.Base64;

public class jonesspeak {
	
	private String[] dictionary;
	
	public jonesspeak() {

	}
	
	public void formatDictionary() throws IOException {
		String file1 ="src/1m.txt";
		int file1Size = getSize(file1);
		BufferedReader reader = new BufferedReader(new FileReader(file1));
		dictionary = new String[file1Size];
		for(int i =0; i<dictionary.length;i++) {dictionary[i] = reader.readLine().replaceAll("(\\w+)\\s\\d+", "$1");}
		reader.close();
	}
	
	public String encrypt(String message) throws UnsupportedEncodingException {
		String[] words = message.toLowerCase().trim().split("\s+");
		String[] wordsIndex = new String[words.length];
		String encryptedMsg = "";
		for(int i = 0; i<words.length; i++) {		
		    encryptedMsg = encryptedMsg + " "+new BigInteger(Integer.toString(Arrays.asList(dictionary).indexOf(words[i])), 10).toString(36);}
		return encryptedMsg.trim();
	}
	
	public String decrypt(String message) throws UnsupportedEncodingException {
		String[] wordsIndex = message.toLowerCase().trim().split("\s+");
		String decryptedMsg = "";
		for(int i = 0; i<wordsIndex.length; i++) {
			if(wordsIndex[i].length()>4) {return "invalid message";}
			int indexBase10 = Integer.valueOf(Long.valueOf(wordsIndex[i], 36).toString());
			if(indexBase10>=dictionary.length) {return "invalid message";}
			decryptedMsg = decryptedMsg + " "+dictionary[indexBase10];
		}
		
		return decryptedMsg.trim();
	
	}
	
	public int getSize(String file) throws IOException {
	     int lines = 0;
	     BufferedReader reader = new BufferedReader(new FileReader(file));
	     while (reader.readLine() != null) lines++;
	     reader.close();
	     return lines;
	}


			

}

