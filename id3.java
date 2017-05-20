import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.*; 

class Node {
	private Node left;
	private Node right;

	private String name;
	private boolean leafNode;
	private String leafValue;
	private int nodeNumber;
	private static int depth = -1;
	private Set<String> attributes;
	
	public Node() {
		super();
	}
	
	public Node(String attribute, Node left, Node right){
		this.name = attribute;
		this.left = left;
		this.right = right;
		this.setLeafNode(Boolean.FALSE);
	}
	
	public Node(String leaf_value){
		this.leafValue = leaf_value;
		this.setLeafNode(Boolean.TRUE);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Set<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<String> attributes) {
		this.attributes = attributes;
	}
	
	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}
	
	
	public boolean isLeafNode() {
		return leafNode;
	}

	public void setLeafNode(boolean leaf_node) {
		this.leafNode = leaf_node;
	}

	public String getLeafValue() {
		return leafValue;
	}

	public void setLeafValue(String leaf_value) {
		this.leafValue = leaf_value;
	}
	
	public int getNodeNumber() {
		return nodeNumber;
	}

	public void setNodeNumber(int node_number) {
		this.nodeNumber = node_number;
	}
		
	public void printTree(){
		depth++;
		if(this.name == null){
			System.out.print(" : " + leafValue);
		}
		else{
			System.out.println();
			for(int i=0; i<depth;i++){
				System.out.print(" | ");
			}
			System.out.print(name + " = 0");
		}

		if(left != null){
			left.printTree();
			if(this.name == null){
				System.out.print(" : " + leafValue);
			}
			else{
				System.out.println();
				for(int i=0; i<depth;i++){
					System.out.print(" | ");
				}
				System.out.print(name + " = 1" );
			}
			right.printTree();
		}
		depth--;
	}	
}

class Tree {

	private int no_NonLeafNodes = 0;

	public Node buildTree(ArrayList<ArrayList<String>> sampleSet, ArrayList<String> attrList, boolean flag) throws FileNotFoundException{
		int neg_count = 0;
		int pos_count = 0;

		for(int i=1; i < sampleSet.size();i++){
			if(sampleSet.get(i).get(sampleSet.get(i).size()-1).equalsIgnoreCase("1")){
				pos_count++;
			}
			else{
				neg_count++;
			}
		}
		if (attrList.isEmpty() || neg_count == sampleSet.size()-1){
			return new Node("0");

		}
		else if(attrList.isEmpty() || pos_count == sampleSet.size()-1){
			return new Node("1");
		}
		else{

			calculateGain calculate_Gain = new calculateGain();
			String bestAttribute = calculate_Gain.bestAttribute(sampleSet,attrList,flag);

			ArrayList<String> attr = new ArrayList<String>();

			HashMap<String, ArrayList<ArrayList<String>>> newMap = calculateGain.mapOnBestAttr(sampleSet, bestAttribute);
			for(String att: attrList){
				if(!att.equalsIgnoreCase(bestAttribute)){
					attr.add(att);
				}
			}


			if (newMap.size() < 2){
				String value = "0";
				if(pos_count > neg_count){
					value = "1";
				}

				return new Node(value);
			}


			return new Node(bestAttribute, buildTree(newMap.get("0"),attr,flag),buildTree(newMap.get("1"),attr,flag));
		}
	}
	
	public void Tree_Copy(Node first, Node second){
		second.setLeafNode(first.isLeafNode());
		second.setName(first.getName());
		second.setLeafValue(first.getLeafValue());

		if(!first.isLeafNode()){
			second.setLeft(new Node());
			second.setRight(new Node());

		Tree_Copy(first.getLeft(), second.getLeft());
	   Tree_Copy(first.getRight(), second.getRight());

		}
	}
	
	public List<Node> getListOfLeafNode(Node root){
		List<Node> leafNodeList = new ArrayList<>();
		if(root.isLeafNode()){ 
			leafNodeList.add(root);
		}
		else{
			if(!root.getLeft().isLeafNode()){
				getListOfLeafNode(root.getLeft());
			}
			if(!root.getRight().isLeafNode()){
				getListOfLeafNode(root.getRight());
			}
		}
		return leafNodeList;
	}
	
	public void calNoOfLeafNodes(Node root){		
		if(!root.isLeafNode()){								
			no_NonLeafNodes++;
			root.setNodeNumber(no_NonLeafNodes);
			calNoOfLeafNodes(root.getLeft());
			calNoOfLeafNodes(root.getRight());
		}
	}
	
	public String majorityClassCal(Node root){
		int negCount = 0;
		int posCount = 0;
		String majority = "0";
		List<Node> leafNodes = getListOfLeafNode(root);
		for(Node node : leafNodes){
			if(node.getLeafValue().equalsIgnoreCase("1")){
				posCount++;
			}
			else{
				negCount++;
			}
		}
		if(posCount>negCount){
			majority = "1";
		}

		return majority;
	}
	
	public void nodeReplace(Node root, int N){
		if(!root.isLeafNode()){
			if(root.getNodeNumber() == N){
		
				String leafValueToBeChanged = majorityClassCal(root);
				root.setLeafNode(Boolean.TRUE);
				root.setLeft(null);
				root.setRight(null);
				root.setLeafValue(leafValueToBeChanged);
			}
			else{
				nodeReplace(root.getLeft(), N);
				nodeReplace(root.getRight(), N);
			}

		}
	}
	
	public int getNoOfNonLeafNodes() {		
		int number = no_NonLeafNodes;
		setNoOfNonLeafNodes(0);
		return number;
	}
	
	public void setNoOfNonLeafNodes(int noNonLeafNodes) {
		this.no_NonLeafNodes = noNonLeafNodes;
	}
	
	public Node buildPrunedTree(Node root,ArrayList<ArrayList<String>> sampleSet, int k, ArrayList<ArrayList<String>> validationData){
		Node treeBest;
		Node treePrime;
		treeBest = new Node();
		Tree_Copy(root, treeBest);
		
		double bestAccuracyOfTree = tree_Accuracy(treeBest, validationData);
		treePrime = new Node();
		for(int i=1; i<=sampleSet.size();i++){
			Tree_Copy(root, treePrime);
			
			Random random = new Random();

			int M = 1 + random.nextInt(k);
			for(int j=0; j<=M; j++){
				calNoOfLeafNodes(treePrime);			
				int N = getNoOfNonLeafNodes();
				
				if(N>1){
					int P = random.nextInt(N) + 1;
					nodeReplace(treePrime, P);
				}
				else{
					break;
				}
			}
			double accuracyOfPrimeTree = tree_Accuracy(treePrime, validationData);
			if (accuracyOfPrimeTree > bestAccuracyOfTree){
				bestAccuracyOfTree = accuracyOfPrimeTree;
				Tree_Copy(treePrime, treeBest);
				
			}
		}
		return treeBest;
	}

	public boolean checkOutput(Node root, ArrayList<String> row, ArrayList<String> attrList){
		Node nodeCopy = root;
		while(true){
			if(nodeCopy.isLeafNode()){
				if(nodeCopy.getLeafValue().equalsIgnoreCase(row.get(row.size()-1))){
					return true;
				}
				else{
					return false;
				}
			}

			int index = attrList.indexOf(nodeCopy.getName());
			String value = row.get(index);
			if(value.equalsIgnoreCase("0")){
				nodeCopy = nodeCopy.getLeft();
			}
			else{
				nodeCopy = nodeCopy.getRight();
			}
		}
	}

	public double tree_Accuracy(Node node, ArrayList<ArrayList<String>> dataToBeChecked){
		double accuracy = 0;
		int positiveExamples = 0;

		ArrayList<String> attributes = dataToBeChecked.get(0);
		for(ArrayList<String> row : dataToBeChecked.subList(1, dataToBeChecked.size())){	
			boolean exampleCheck = checkOutput(node, row, attributes);					
			if(exampleCheck){
				positiveExamples++;
			}
		}
		accuracy = (((double) positiveExamples / (double) (dataToBeChecked.size()-1)) * 100.00);

		return accuracy;
	}
}


public class id3 {

	public static void main(String[] args) throws NumberFormatException, IOException {
	
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		BufferedReader brr=new BufferedReader(new InputStreamReader(System.in));
		
			System.out.println("enter the number of nodes to prune");
			int K = Integer.parseInt(br.readLine());;
			String trainingDataFile = "training_set.csv";
			String validationDataFile = "validation_set.csv";
			String testFile = "test_set.csv";
			System.out.println("enter 1 to print or 0 to not print the tree for pruning");
			int boolean_print= Integer.parseInt(brr.readLine());

			id3 test = new id3();
			Read read = test.new Read();
			Tree tree = new Tree();

			try {

				ArrayList<ArrayList<String>> dataSetTraining = read.read(trainingDataFile);
				ArrayList<ArrayList<String>> dataSetValidation = read.read(validationDataFile);
				ArrayList<ArrayList<String>> dataSetTest = read.read(testFile);

				ArrayList<String> attributeList = dataSetTraining.get(0);
				boolean flag = false;

				Node tree_InformationGain = tree.buildTree(dataSetTraining, attributeList, flag);

				
					System.out.println("***************************Tree****************************");
					System.out.println();
					tree_InformationGain.printTree();
					System.out.println();
					System.out.println();
				

				System.out.println(" The accuracy of Tree is : "
						+ tree.tree_Accuracy(tree_InformationGain, dataSetTest));

				System.out.println();
                 
				if(K==0)
				{
					Node tree_prune_zero = tree.buildTree(dataSetTraining, attributeList, flag);

					
					System.out.println("***************************Tree****************************");
					System.out.println();
					tree_prune_zero.printTree();
					System.out.println();
					System.out.println();
				
					System.out.println(" The accuracy of Tree is : "
							+ tree.tree_Accuracy(tree_prune_zero, dataSetTest));

					System.out.println();

				}
				
				else
				{
				Node prunedTreeInformationGain = tree.buildPrunedTree(tree_InformationGain, dataSetTraining, K, dataSetValidation);

				if (boolean_print==1) {
					System.out.println("*******************************Pruned Tree********************************");
					System.out.println();
					tree_InformationGain.printTree();
					System.out.println();
					System.out.println();
                 
					
						System.out.println(" The accuracy of Tree is : "
								+ tree.tree_Accuracy(tree_InformationGain, dataSetTest));

						System.out.println();
					
					

				System.out.println(" The accuracy of the pruned tree is : "
						+ tree.tree_Accuracy(prunedTreeInformationGain, dataSetTest));

				System.out.println();
				}
				}
				attributeList = dataSetTraining.get(0);
				flag = true;



			} catch (IOException e) {
				System.out.println("Some file could not be found, try again");

			}
		

	}
	public class Read {


		public ArrayList<ArrayList<String>> read(String fileName) throws IOException{

			ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
			File file = new File(fileName);
			Scanner input;
			input = new Scanner(file);
			while(input.hasNext()){
				String[] dataForEachRow = input.next().split(",");
				data.add(new ArrayList<String>(Arrays.asList(dataForEachRow)));

			}
			input.close();
			return data;
		}
	}
}


	
class calculateGain {

	HashMap<String, ArrayList<String>> dataMap;
	HashMap<String, Double> gainMap ;

	public static double entropy(double pos, double neg){
		double total = pos + neg;
		double posProbability = pos/total;
		double negProbability = neg/total;

		if(pos == neg){
			return 1;
		}
		else if(pos == 0 || neg == 0){
			return 0;
		}
		else{
			double entropy = ((-posProbability) * (Math.log(posProbability)/Math.log(2))) + ((-negProbability)*(Math.log(negProbability)/Math.log(2)));
			return entropy;
		}

	}
	
		
	public double informationGain(double rootPositive, double rootNegative, double positiveLeft, double negativeLeft, double positiveRight, double negativeRight){
		double totalRoot = rootPositive + rootNegative;
		double rootEntropy = entropy(rootPositive, rootNegative);
		double leftEntropy = entropy(positiveLeft,negativeLeft);
		double rightEntropy = entropy(positiveRight, negativeRight);
		double totalLeft = positiveLeft + negativeLeft;
		double totalRight = positiveRight + negativeRight;

		double gain = rootEntropy - (((totalLeft/totalRoot)* leftEntropy) + ((totalRight/totalRoot) * rightEntropy));

		return gain;
	}
	
	public static HashMap<String, ArrayList<String>> populateMap(ArrayList<ArrayList<String>> data) throws FileNotFoundException{
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

		ArrayList<String> keys = data.get(0);	

		for(int i=0;i<keys.size();i++){
			for(int j=1;j<data.size();j++){
				if (map.containsKey(keys.get(i))){
					map.get(keys.get(i)).add(data.get(j).get(i));
				}
				else{
					ArrayList<String> values = new ArrayList<String>();
					values.add(data.get(j).get(i));
					map.put(keys.get(i), values);
				}
			}
		}
		return map;
	}
	
	public static HashMap<String,ArrayList<ArrayList<String>>> mapOnBestAttr(ArrayList<ArrayList<String>> data, String bestAttr){
		HashMap<String, ArrayList<ArrayList<String>>> reducedMap = new HashMap<String, ArrayList<ArrayList<String>>>();
		int index = data.get(0).indexOf(bestAttr);
		
		for(int i=1;i<data.size();i++){
			if(data.get(i).get(index).equalsIgnoreCase("0")){
				if(reducedMap.containsKey("0")){
					reducedMap.get("0").add(data.get(i));
				}
				else{
					ArrayList<ArrayList<String>> dataAdd = new ArrayList<ArrayList<String>>();
					dataAdd.add(data.get(0));
					dataAdd.add(data.get(i));
					reducedMap.put("0",dataAdd);
				}

			}
			else{
				if(reducedMap.containsKey("1")){
					reducedMap.get("1").add(data.get(i));
				}
				else{
					ArrayList<ArrayList<String>> dataAdd = new ArrayList<ArrayList<String>>();
					dataAdd.add(data.get(0));
					dataAdd.add(data.get(i));
					reducedMap.put("1",dataAdd);
				}
			}
		}

		return reducedMap;
	}


	public String bestAttribute(ArrayList<ArrayList<String>> data, ArrayList<String> attributeList,boolean flag) throws FileNotFoundException{
		String bestAttr = "";
		dataMap = populateMap(data);
		gainMap = new HashMap<String, Double>();
		
		double classPositive = 0;
		double classNegative = 0;
		for(String value : dataMap.get("Class")){
			if(value.equalsIgnoreCase("1")){
				classPositive++;
			}
			else{
				classNegative++;
			}
		}

		for(String key: attributeList.subList(0, attributeList.size()-1)){		
			ArrayList<String> temp = dataMap.get(key);
			double positiveLeft = 0;
			double positiveRight = 0;
			double negativeLeft = 0;
			double negativeRight = 0;
			for(int i=0; i<temp.size();i++){								
				if(temp.get(i).equalsIgnoreCase("0")){
					if(dataMap.get("Class").get(i).equalsIgnoreCase("1")){
						positiveLeft++;
					}
					else{
						negativeLeft++;
					}
				}
				else{
					if(dataMap.get("Class").get(i).equalsIgnoreCase("1")){
						positiveRight++;
					}
					else{
						negativeRight++;
					}
				}
			}

	
				Double gainForEachKey = informationGain(classPositive, classNegative, positiveLeft, negativeLeft, positiveRight, negativeRight);
				gainMap.put(key, gainForEachKey);
				
		
		}

		ArrayList<Double> valueList = new ArrayList<Double>(gainMap.values());
		Collections.sort(valueList);
		Collections.reverse(valueList);
		for(String key: gainMap.keySet()){
			if (valueList.get(0).equals(gainMap.get(key))){
				bestAttr = key;
				break;
			}
		}
		return bestAttr;		
	}

}
