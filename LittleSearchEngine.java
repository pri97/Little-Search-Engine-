package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			System.out.println("Loading Keywords from file: " + docFile);
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		HashMap<String, Occurrence> keyWord = new HashMap<String, Occurrence>();
		Scanner scanner = new Scanner(new File(docFile));
		while(scanner.hasNext()){
			String word1 = getKeyWord(scanner.next());
			if(word1 != null){
				if(keyWord.containsKey(word1)){
					keyWord.get(word1).frequency++;
				}
				else{
					keyWord.put(word1, new Occurrence(docFile, 1));
				}
			}
			
		}
		return keyWord;
			
		
		
		
		//return null;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		// COMPLETE THIS METHOD
		Iterator<String> iteratorForKeys = kws.keySet().iterator();
		
		while(iteratorForKeys.hasNext()){
			String keyWord1 = iteratorForKeys.next().toString();
		
			if(keywordsIndex.containsKey(keyWord1)){
				ArrayList<Occurrence> keywordOccurrence = keywordsIndex.get(keyWord1);
			
				keywordOccurrence.add(kws.get(keyWord1));
				this.insertLastOccurrence(keywordOccurrence);
			}
			else{
				ArrayList<Occurrence> newList = new ArrayList<Occurrence>();
				newList.add(kws.get(keyWord1));
				keywordsIndex.put(keyWord1, newList);
			}
		
		}
		
				

		
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		word = word.trim();
		word = word.toLowerCase();
		char ending = word.charAt(word.length()-1);
		while( ending == '.' || ending == ','|| ending == ':' || ending == ';'|| ending == '!' || ending == '?' ) {
			word = word.substring(0,word.length()-1);
			if(word.length() > 1){
				ending =  word.charAt(word.length()-1);
			}
			else{
				break;
			}
		}
		
		if(word.length() == 0)
			return null;
		//checking for noise word
		for(String noiseWord : noiseWords.keySet()) {
				if(word.equals(noiseWord)){
					return null;
				}			
		}
		//checking for non letter characters in the word
		for( int i=0; i<word.length();i++){
			if(!Character.isLetter(word.charAt(i))) {
				return null; 
			}	
		}
		
		return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		int lastFrequency = occs.get(occs.size()-1).frequency;//last frequency
		Occurrence temp = occs.get(occs.size()-1);//taking the last item in the list
		int low = 0;//index of the first item
		int high = occs.size()-1;//index of last item
		int midpoint;// for binary search
		ArrayList<Integer> midIndexes = new ArrayList<Integer>();
		if(occs.size() == 1){ //if size of array list is zero
			return null;
		}
		
		while(low <= high){
			midpoint = (low + high) / 2;
			midIndexes.add(midpoint);
			if(lastFrequency > occs.get(midpoint).frequency){
				high = midpoint - 1;//goes on the left side of midpoint
				
			}
			else if(lastFrequency < occs.get(midpoint).frequency){
				low = midpoint + 1;// goes on the right side of midpoint
			}
			else{
				break;
			}
		}
		
		if(midIndexes.get(midIndexes.size()-1) == 0) {
			if(temp.frequency < occs.get(0).frequency){
				occs.add(1,temp);
				occs.remove(occs.size()-1);
				
				return midIndexes;
			}
		}
		occs.add(midIndexes.get(midIndexes.size()-1),temp);
		occs.remove(occs.size()-1);
				return midIndexes;
		
	}
	
	
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		ArrayList<Occurrence> occurrenceWords1 = keywordsIndex.get(kw1);
		ArrayList<Occurrence> occurrenceWords2 = keywordsIndex.get(kw2);
				
	    	
	    ArrayList<String> top5List = new ArrayList<String>();
		ArrayList<Occurrence> top5 = new ArrayList<Occurrence>();
		
		/*if(combinedTemp.size() == 2) {
			Occurrence tmp1 = combinedTemp.get(0);
			Occurrence tmp2 = combinedTemp.get(1);
			if(tmp1.frequency > tmp2.frequency) {
				top5List.add(tmp1.document);
				top5List.add(tmp2.document);
			}
			else {
				top5List.add(tmp2.document);
				top5List.add(tmp1.document);
			}
		}*/
		
		if(keywordsIndex.get(kw1) != null){
			occurrenceWords1 = keywordsIndex.get(kw1);
		}
		if(keywordsIndex.get(kw2) != null){
			occurrenceWords2 = keywordsIndex.get(kw2);
		}
		
			int size1 = 0;
			int size2 = 0;
			
			if(occurrenceWords1 != null)
				size1 = occurrenceWords1.size();
			
			for( int k = 0; k < size1; k++){
								if(top5.size() <= 4){
					Occurrence occurrence1 = occurrenceWords1.get(k);
					int F1 = occurrenceWords1 .get(k).frequency;
					String doc1 = occurrenceWords1.get(k).document;
					if(occurrenceWords2 != null)
						size2 = occurrenceWords2.size();
					
					if(size2 == 0) {
						top5.add(occurrence1);
					}
					for(int n=0; n < size2; n++){
						Occurrence occurrence2 = occurrenceWords2.get(n);
						int F2 = occurrenceWords2.get(n).frequency;
						String doc2 = occurrenceWords2.get(n).document;
						if(F2 <= F1){
							if(!top5.contains(occurrence1) && top5.size() <= 4){
								top5.add(occurrence1); 
							}
						}
						else if(F2 > F1){
								if(!top5.contains(occurrence2) && top5.size() <= 4){
									top5.add(occurrence2);
																}
						}
					}
				}
			}
			
			if(occurrenceWords2 != null)
				size2 = occurrenceWords2.size();
			
			for( int k = 0; k < size2; k++){
					if(top5.size() <= 4){
					Occurrence occurrence2 = occurrenceWords2.get(k);
					int F2 = occurrenceWords2 .get(k).frequency;
					String doc2 = occurrenceWords2.get(k).document;
					size1 = 0;
					if(occurrenceWords1 != null)
						size1 = occurrenceWords1.size();
					
					if(size1== 0) {
						top5.add(occurrence2);
					}
					for(int n=0; n < size1; n++){
						Occurrence occurrence1 = occurrenceWords1.get(n);
						int F1 = occurrenceWords1.get(n).frequency;
						String doc1 = occurrenceWords1.get(n).document;
						if(F2 <= F1){
							if(!top5.contains(occurrence1) && top5.size() <= 4){
								top5.add(occurrence1); 
															}
						}
						else if(F2 > F1){
								if(!top5.contains(occurrence2) && top5.size() <= 4){
									top5.add(occurrence2);
							}
						}
					}
				}
			}

		/*for( int m = 0; m < top5.size(); m++){
			if(m+1 == top5.size()){
				System.out.println(top5.get(m));
			}
			else{
				System.out.print(top5.get(m) + ",");
			}
		}*/
		if(top5.size() == 0){
			return null;
		}
		
		for(int i=0; i < top5.size(); i++) {
			if(!top5List.contains(top5.get(i).document))
				top5List.add(top5.get(i).document);
		}
	
		

		return top5List;
	}
		
}
