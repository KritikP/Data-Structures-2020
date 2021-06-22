package friends;

import java.util.ArrayList;

import structures.Queue;
import structures.Stack;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null or empty array list if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		if(!g.map.containsKey(p1) || !g.map.containsKey(p2)) {
			return null;
		}
		
		Queue<Person> q = new Queue<Person>();
		ArrayList<String> chain = new ArrayList<String>();
		int[] distances = new int[g.members.length];
		int[] pred = new int[g.members.length];
		boolean[] visited = new boolean[g.members.length];
		
		q.enqueue(g.members[g.map.get(p1)]);	//Enqueuing the starting point
		distances[g.map.get(p1)] = 0;			//Setting the initial distance to 0
		visited[g.map.get(p1)] = true;			//Marking the starting point as visited
		pred[g.map.get(p1)] = -1;				//Mark the starting point as having no predecessor
		
		while(!q.isEmpty()) {
			Person ptr = q.dequeue();
			for(Friend i = ptr.first; i != null; i = i.next) {
				if(!visited[i.fnum]) {
					q.enqueue(g.members[i.fnum]);
					visited[i.fnum] = true;
					distances[i.fnum] = distances[g.map.get(ptr.name)] + 1;
					pred[i.fnum] = g.map.get(ptr.name);
				}
				if(g.members[i.fnum].name.equals(p2)){
					for(int j = i.fnum; j != -1; j = pred[j]) {
						chain.add(0, g.members[j].name);
					}
					return chain;
				}
			}
		}
		
		return chain;
	}
	
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null or empty array list if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		
		ArrayList<ArrayList<String>> groups = new ArrayList<ArrayList<String>>();
		boolean[] visited = new boolean[g.members.length];
		
		for(int i = 0; i < g.members.length; i++) {
			if(groups.isEmpty()) {
				ArrayList<String> group = cliqueHelper(g, school, g.members[i], visited);
				if(group != null) {
					groups.add(group);
				}
			}
			else {
				if(school.equals(g.members[i].school)) {
					boolean isNotInside = true;
					for(int j = 0; j < groups.size(); j++) {
						if(groups.get(j).contains(g.members[i].name)) {
							isNotInside = false;
							break;
						}
					}
					if(isNotInside) {
						ArrayList<String> group = cliqueHelper(g, school, g.members[i], visited);
						groups.add(group);
					}
				}
				
			}
		}
		
		return groups;
		
	}
	
	private static ArrayList<String> cliqueHelper(Graph g, String school,
			Person pers, boolean[] visited) {
		ArrayList<String> strlist = new ArrayList<String>();
		if(school.equals(pers.school) && !visited[g.map.get(pers.name)]) {
			strlist.add(pers.name);
			visited[g.map.get(pers.name)] = true;
			for(Friend i = pers.first; i != null; i = i.next) {
				ArrayList<String> strs = cliqueHelper(g, school, g.members[i.fnum], visited);
				if(strs != null) {
					strlist.addAll(strs);
				}
			}
		}
		else
			return null;
		
		return strlist;
	}
	
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null or empty array list if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		
		ArrayList<String> connects = new ArrayList<String>();
		int[] dfsnum = new int[g.members.length];
		int[] back = new int[g.members.length];
		boolean[] visited = new boolean[g.members.length];
		
		for(int i = 0; i < g.members.length; i++) {
			if(!visited[i]) {
				dfs(g, dfsnum, back, visited, i, 0, connects, 0);
			}
		}
				
		return connects;
		
	}
	
	private static void dfs(Graph g, int[] dfs, int[] back,
			boolean[] visited, int pers, int dfscount, ArrayList<String> connects, int prev) {

		if(!visited[pers]) {
			visited[pers] = true;
			dfs[pers] = dfscount + 1;
			back[pers] = dfs[pers];
		}
		
		for(Friend i = g.members[pers].first; i != null; i = i.next) {
			if(!visited[i.fnum]) {
				dfs(g, dfs, back, visited, i.fnum, dfscount + 1, connects, pers);
				back[pers] = Math.min(back[pers], back[i.fnum]);
				
				if(dfs[pers] != 1) { // Not starting point
					if(dfs[pers] <= back[i.fnum] && !connects.contains(g.members[pers].name)) {		//This means pers is a connector, so add it in
						connects.add(g.members[pers].name);
					}
				}
				else { // Starting Point
					if(g.members[pers].first.next != null) {
						if(dfs[pers] <= back[i.fnum] && !connects.contains(g.members[pers].name)) {		//This means pers is a connector, so add it in
							connects.add(g.members[pers].name);
						}
					}
				}
				
			}
			else if(prev == i.fnum) {
				
			}
			else {
				back[pers] = Math.min(back[pers], dfs[i.fnum]);
			}
		}
	}
}

