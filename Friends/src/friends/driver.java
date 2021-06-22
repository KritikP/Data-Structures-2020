package friends;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import friends.Friends;
import friends.Graph;

public class driver {
	public static void main(String[] args) throws FileNotFoundException{
		Scanner sc = new Scanner(System.in);
		Graph g;
		while (true) {
			System.out.print("\nEnter the file: ");
			String file = sc.nextLine();
			if (file.length() != 0) {
				g = new Graph(new Scanner(new File(file)));
			} else {
				break;
			}


			System.out.println("Enter the number for corresponding method:");
			System.out.println("1 shortestChain");
			System.out.println("2 cliques");
			System.out.println("3 connectors");
			String method = sc.nextLine();
			
			ArrayList<String> result = new ArrayList<>();
			ArrayList<ArrayList<String>> resultschool = new ArrayList<>();
			if (method.equals("1")) {
				System.out.println("Enter the name (Start)");
				String start = sc.nextLine();
				System.out.println("Enter the name (End)");
				String end = sc.nextLine();
				result = Friends.shortestChain(g, start, end);
			}else if (method.equals("2")) {
				System.out.println("Enter the school name");
				String school = sc.nextLine();
				resultschool = Friends.cliques(g, school);
			}else if (method.equals("3")) {
				result = Friends.connectors(g);
			}else{
			break;}
			if (result==null||result.isEmpty()) {
				if (resultschool==null||resultschool.isEmpty()) {
				System.out.print("not found");}
				else {
					System.out.println("Result (School):");
					for (int i = 0;i<resultschool.size();i++) {
						ArrayList <String> temp = resultschool.get(i);
						System.out.println("------");
						for(int j =0; j<temp.size();j++) {
							System.out.print(temp.get(j) + ", ");
						}
						System.out.println("");
				 }
				}
				}else {
				System.out.println("Result: ");
				for(int i = 0;i<result.size();i++) {
				System.out.print(result.get(i) + ", ");
				}
		}
			
	}
		sc.close();
	}
}
