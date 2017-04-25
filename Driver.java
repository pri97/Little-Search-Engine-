package search;

import java.io.*;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Driver {

	public static void main(String[] args) throws IOException{
		LittleSearchEngine lse = new LittleSearchEngine();
		lse.makeIndex("docs.txt", "noisewords.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			System.out.println("Enter a word to check: ");
			String w1 = lse.getKeyWord(reader.readLine().trim());
			
			System.out.println("Enter another word to check: ");
			String w2 = lse.getKeyWord(reader.readLine().trim());
			
			System.out.println("Result = " + lse.top5search(w1, w2));
		}

	}

}
