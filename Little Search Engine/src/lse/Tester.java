package lse;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
public class Tester {

	public static void main(String[] args) throws FileNotFoundException {
		LittleSearchEngine eng = new LittleSearchEngine();
		String docsFile = "docs.txt";
		String noiseFile = "noisewords.txt";
		eng.makeIndex(docsFile, noiseFile);
		
		//Search Method
		Scanner sc = new Scanner(System.in);
		String word = "blank";
		while(!word.equals("")) {
			System.out.print("Type word to search for and then hit enter: ");
			word = sc.nextLine();
			if(!word.equals("")) {
				if(eng.keywordsIndex.containsKey(word)) {
					for(int i = 0; i < eng.keywordsIndex.get(word).size(); i++) {
						System.out.println(word + ": " + eng.keywordsIndex.get(word).get(i).frequency + " times in " + eng.keywordsIndex.get(word).get(i).document);
					}
				}
				else
					System.out.println(word + " does not appear in any doc.");
			}
		}
		
		//Top 5 Test
		System.out.println("Enter word 1 to search for top 5: ");
		String word1 = sc.nextLine();
		System.out.println("Enter word 2 to search for top 5: ");
		String word2 = sc.nextLine();
		ArrayList<String> top5 = eng.top5search(word1, word2);
		for(String s : top5) {
			System.out.println(s);
		}
		sc.close();
	}

}
