package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		
		if(docFile == null) {
			throw new FileNotFoundException();
		}
		Scanner sc = new Scanner(new File(docFile));
		HashMap<String, Occurrence> map = new HashMap<String, Occurrence>();
		
		while(sc.hasNext()) {
			String word = getKeyword(sc.next());
			if(word != null) {
				if(map.containsKey(word)) {
					map.get(word).frequency++;
				}
				else {
					map.put(word, new Occurrence(docFile, 1));
				}
			}
		}
		sc.close();
		return map;
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
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		for(String key : kws.keySet()) {
			if(keywordsIndex.containsKey(key)) {
				keywordsIndex.get(key).add(kws.get(key));
				insertLastOccurrence(keywordsIndex.get(key));
			}
			else {
				ArrayList<Occurrence> occs = new ArrayList<Occurrence>();
				occs.add(kws.get(key));
				keywordsIndex.put(key, occs);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		word = word.toLowerCase();
		StringTokenizer tk = new StringTokenizer(word, ".,?:;!");
		if(!tk.hasMoreTokens())
			return null;
		
		String item = tk.nextToken();
		for(int i = 0; i < item.length(); i++) {
			if(!Character.isLetter(item.charAt(i)))
				return null;
		}
		
		if(noiseWords.contains((String)item)) {
			return null;
		}
		
		return item;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		if(occs.size() <= 1)
			return null;
		
		int right = occs.size() - 2;
		int left = 0;
		int mid = (right + left) / 2;
		Occurrence item = occs.remove(right + 1);
		ArrayList<Integer> list = new ArrayList<Integer>();
		while(left <= right) {
			list.add(mid);
			if(occs.get(mid).frequency == item.frequency) {
				occs.add(mid, item);
				return list;
			}
			else if(item.frequency < occs.get(mid).frequency) {
				left = mid + 1;
			}
			else {
				right = mid - 1;
			}
			mid = (right + left) / 2;
		}
		if(occs.get(mid).frequency < item.frequency) {
			occs.add(mid, item);
		}
		else {
			occs.add(mid + 1, item);
		}
		
		return list;
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
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<String> docs = new ArrayList<String>();
		ArrayList<Occurrence> occs1 = keywordsIndex.get(kw1);
		ArrayList<Occurrence> occs2 = keywordsIndex.get(kw2);	
		
		int i = 0;
		int j = 0;
		int count = 0;
		if(occs1 == null && occs2 == null) {
			return docs;
		}
		if(occs1 == null) {
			occs1 = occs2;
			i = occs2.size() + 1;
		}
		if(occs2 == null) {
			occs2 = occs1;
			j = occs1.size() + 1;
		}
		
		while((i < occs1.size() || j < occs2.size()) && count < 5) {
			if(i >= occs1.size()) {
				if(!docs.contains(occs2.get(j).document)) {
					docs.add(occs2.get(j).document);
					count++;
				}
				j++;
			}
			else if(j >= occs2.size()) {
				if(!docs.contains(occs1.get(i).document)) {
					docs.add(occs1.get(i).document);
					count++;
				}
				i++;
			}
			else{
				if(occs1.get(i).frequency >= occs2.get(j).frequency) {
					if(!docs.contains(occs1.get(i).document)) {
						docs.add(occs1.get(i).document);
						count++;
					}
					i++;
				}
				else {
					if(!docs.contains(occs2.get(j).document)) {
						docs.add(occs2.get(j).document);
						count++;
					}
					j++;
				}
			}
		}
		
		return docs;
	
	}
}
