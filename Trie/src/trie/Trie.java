package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		
		TrieNode root = new TrieNode(null, null, null);
		if(allWords.length == 0) {
			return root;
		}
		
		root.firstChild = new TrieNode(new Indexes(0, (short)0, (short)(allWords[0].length()- 1)), null, null);
		TrieNode ptr = root.firstChild;
		TrieNode prev = root.firstChild;
		int endOfPrefix = 0;
		
		for(int i = 1; i < allWords.length; i++) {
			String word = allWords[i];

			while(ptr != null) {
				int ptrStringIndex = ptr.substr.wordIndex;
				int startingIndex = ptr.substr.startIndex;
				int endingIndex = ptr.substr.endIndex;
				String ptrString = allWords[ptrStringIndex].substring(startingIndex, endingIndex + 1);
				
				if(startingIndex > word.length()) {
					prev = ptr;
					ptr = ptr.sibling;
					continue;
				}
				
				endOfPrefix = 0;
				while(endOfPrefix < word.substring(startingIndex).length()
						&& endOfPrefix < ptrString.length()
						&& word.substring(startingIndex).charAt(endOfPrefix) == ptrString.charAt(endOfPrefix))
					endOfPrefix++;
				
				if(endOfPrefix != 0) {
					endOfPrefix += startingIndex;
				}
				
				if(endOfPrefix == 0) { //There was no matching prefix
					prev = ptr;
					ptr = ptr.sibling;
				}
				else {
					if(endOfPrefix - 1 == endingIndex) { //The entire prefix matched, so now move down to the child of that node
						prev = ptr;
						ptr = ptr.firstChild;
					}
					else { //Only part of it matched, so now it needs to do the work
						prev = ptr;
						break;
					}
				}
				
			}
			
			if(ptr == null) { //No prefix found, add the new word to the end of the row
				prev.sibling = new TrieNode(new Indexes(i, (short) 0, (short) (word.length() -1)), null, null);
			}
			
			else {
				TrieNode oldParentChild = ptr.firstChild;
				short oldEndIndex = ptr.substr.endIndex;
				
				ptr.substr.endIndex = (short) (endOfPrefix - 1);
				ptr.firstChild = new TrieNode(new Indexes(ptr.substr.wordIndex, (short) (endOfPrefix), oldEndIndex),
						oldParentChild,
						new TrieNode(new Indexes((short) i, (short) (endOfPrefix), (short) (word.length() - 1)), null, null));
				
			}
			
			ptr = root.firstChild;
			prev = ptr;
			endOfPrefix = 0;
		}
		
		return root;
	}
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		
		if(root == null) {
			return null;
		}
		
		ArrayList<TrieNode> list = new ArrayList<>();
		TrieNode ptr = root;
		
		
		while(ptr != null){ // Traversing the trie
			System.out.println(0);
			if(ptr.substr == null)	//Makes sure to start the process at a legit node, not the root
				ptr = ptr.firstChild;		
			
			String ptrStr = allWords[ptr.substr.wordIndex].substring(0, ptr.substr.endIndex + 1);
			if(allWords[ptr.substr.wordIndex].startsWith(prefix) || (prefix.startsWith(ptrStr) )) {
				if(ptr.firstChild == null && allWords[ptr.substr.wordIndex].length() >= prefix.length()) {
					list.add(ptr);
					ptr = ptr.sibling;
				}
				else {
					ArrayList<TrieNode> listadd = completionList(ptr.firstChild, allWords, prefix);
					if(listadd != null) {
						list.addAll(completionList(ptr.firstChild, allWords, prefix));
					}
					ptr = ptr.sibling;
				}
			}
			
			else{
				ptr = ptr.sibling;
			}
		}
		
		if(list.isEmpty())
			return null;
		
		return list;
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
