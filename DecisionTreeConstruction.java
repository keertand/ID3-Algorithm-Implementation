

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class DecisionTreeConstruction {
	private static int count = 0;

	public static void main(String[] args) {
		
		if (args.length != 6) {
			System.out.println("Six command line args needed");
			return;
		}
		 int L = Integer.parseInt(args[0]);
		 int K = Integer.parseInt(args[1]);
		 int[] widthAndLength = DimensionsofDataSheet(args[2]);

		int[][] values = new int[widthAndLength[1]][widthAndLength[0]];
		String[] AttributeNames = new String[widthAndLength[0]];
		int[] isDone = new int[widthAndLength[0]];
		int[] indexList = new int[values.length];
		
		loaddata(args[2], values, AttributeNames, isDone, indexList, widthAndLength[0]);
		node root = constructDecisionTree(null, values, isDone, widthAndLength[0] - 1, indexList, null);
	
		node pruneTree = postPruneAlgorithm(args[3], L, K, root, values, widthAndLength[0] - 1);
		
		System.out.println("The Accuracy over Tesing data for decision Tree  " + Accuracy(args[4], root));
	
		System.out.println("The Accuracy over Tesing data for Pruned Tree  " + Accuracy(args[4], pruneTree));
		if (args[5].equalsIgnoreCase("yes"))
			printTree(pruneTree, 0, AttributeNames);
		
		
		
		
		node varroot = constructDecisionTreewithVarianceImpurity(null, values, isDone, widthAndLength[0] - 1, indexList, null);
		
		node varpruneTree = postPruneAlgorithm(args[3], L, K, root, values, widthAndLength[0] - 1);
		
		System.out.println("The Accuracy over Tesing data for decision Tree using variance impurity" + Accuracy(args[4], varroot));
	
		System.out.println("The Accuracy over Tesing data for Pruned Tree using variance impurity" + Accuracy(args[4], varpruneTree));

		if (args[5].equalsIgnoreCase("yes"))
			printTree(pruneTree, 0, AttributeNames);		

	}

/* This function will find the best attribute and return a node */
	private static node GetBestAttributeAndConstructNode(node root, int[][] values, int[] isDone, int width,int[] indexList) 
	{
		int i = 0;
		int k = 0;
		double maxInfoGain = 0;
		int maxLeftIndex[] = null;
		int maxRightIndex[] = null;
		int maxIndex = -1;
		for (; i < width; i++) {
			if (isDone[i] == 0) {
				double negatives = 0;
				double positives = 0;
				double left = 0;
				double right = 0;
				double leftEntrophy = 0;
				double rightEntrophy = 0;
				int[] leftIndex = new int[values.length];
				int[] rightIndex = new int[values.length];
				double entrophy = 0;
				double rightPositives = 0;
				double infoGain = 0;
				double rightNegatives = 0, leftPositives = 0, leftNegatives = 0;
				for (k = 0; k < indexList.length; k++) {
					if (values[indexList[k]][width] == 1) {
						positives++;
					} else {
						negatives++;
					}
					if (values[indexList[k]][i] == 1) {
						rightIndex[(int) right++] = indexList[k];
						if (values[indexList[k]][width] == 1) {
							rightPositives++;
						} else {
							rightNegatives++;
						}

					} else {
						leftIndex[(int) left++] = indexList[k];
						if (values[indexList[k]][width] == 1) {
							leftPositives++;
						} else {
							leftNegatives++;
						}

					}

				}

				entrophy = (-1 * calculateLog(positives / indexList.length) * ((positives / indexList.length)))
						+ (-1 * calculateLog(negatives / indexList.length) * (negatives / indexList.length));
				leftEntrophy = (-1 * calculateLog(leftPositives / (leftPositives + leftNegatives))
						* (leftPositives / (leftPositives + leftNegatives)))
						+ (-1 * calculateLog(leftNegatives / (leftPositives + leftNegatives))
								* (leftNegatives / (leftPositives + leftNegatives)));
				rightEntrophy = (-1 * calculateLog(rightPositives / (rightPositives + rightNegatives))
						* (rightPositives / (rightPositives + rightNegatives)))
						+ (-1 * calculateLog(rightNegatives / (rightPositives + rightNegatives))
								* (rightNegatives / (rightPositives + rightNegatives)));
				if (Double.compare(Double.NaN, entrophy) == 0) {
					entrophy = 0;
				}
				if (Double.compare(Double.NaN, leftEntrophy) == 0) {
					leftEntrophy = 0;
				}
				if (Double.compare(Double.NaN, rightEntrophy) == 0) {
					rightEntrophy = 0;
				}

				infoGain = entrophy
						- ((left / (left + right) * leftEntrophy) + (right / (left + right) * rightEntrophy));
				if (infoGain >= maxInfoGain) {
					maxInfoGain = infoGain;
					maxIndex = i;
					int leftTempArray[] = new int[(int) left];
					for (int index = 0; index < left; index++) {
						leftTempArray[index] = leftIndex[index];
					}
					int rightTempArray[] = new int[(int) right];
					for (int index = 0; index < right; index++) {
						rightTempArray[index] = rightIndex[index];
					}
					maxLeftIndex = leftTempArray;
					maxRightIndex = rightTempArray;

				}
			}
		}
		root.Attribute = maxIndex;
		root.leftIndices = maxLeftIndex;
		root.rightIndices = maxRightIndex;
		return root;
	}

	private static node VarianceimpAndConstructNode(node root, int[][] values, int[] isDone, int width,
			int[] indexList) {
		int i = 0;
		int k = 0;
		double maxGain = 0;
		int maxLeftIndex[] = null;
		int maxRightIndex[] = null;
		int maxIndex = -1;
		for (; i < width; i++) {
			if (isDone[i] == 0) {
				double negatives = 0;
				double positives = 0;
				double left = 0;
				double right = 0;
				double leftvariance = 0;
				double rightvariance = 0;

				double variance = 0;
				double rightPositives = 0;
				double Gain = 0;
				double rightNegatives = 0, leftPositives = 0, leftNegatives = 0;

				int[] leftIndex = new int[values.length];
				int[] rightIndex = new int[values.length];
				for (k = 0; k < indexList.length; k++) {
					if (values[indexList[k]][width] == 1) {
						positives++;
					} else {
						negatives++;
					}
					if (values[indexList[k]][i] == 1) {
						rightIndex[(int) right++] = indexList[k];
						if (values[indexList[k]][width] == 1) {
							rightPositives++;
						} else {
							rightNegatives++;
						}

					} else {
						leftIndex[(int) left++] = indexList[k];
						if (values[indexList[k]][width] == 1) {
							leftPositives++;
						} else {
							leftNegatives++;
						}

					}

				}

				variance = ((positives / indexList.length)) * (negatives / indexList.length);
				leftvariance = (leftPositives / (leftPositives + leftNegatives))
						* (leftNegatives / (leftPositives + leftNegatives));
				rightvariance = (rightPositives / (rightPositives + rightNegatives))
						* (rightNegatives / (rightPositives + rightNegatives));
				if (Double.compare(Double.NaN, variance) == 0) {
					variance = 0;
				}
				if (Double.compare(Double.NaN, leftvariance) == 0) {
					leftvariance = 0;
				}
				if (Double.compare(Double.NaN, rightvariance) == 0) {
					rightvariance = 0;
				}

				Gain = variance - ((left / (left + right) * leftvariance) + (right / (left + right) * rightvariance));

				if (Gain >= maxGain) {
					maxGain = Gain;
					maxIndex = i;
					int leftTempArray[] = new int[(int) left];
					for (int index = 0; index < left; index++) {
						leftTempArray[index] = leftIndex[index];
					}
					int rightTempArray[] = new int[(int) right];
					for (int index = 0; index < right; index++) {
						rightTempArray[index] = rightIndex[index];
					}
					maxLeftIndex = leftTempArray;
					maxRightIndex = rightTempArray;

				}
			}
		}
		root.Attribute = maxIndex;
		root.leftIndices = maxLeftIndex;
		root.rightIndices = maxRightIndex;
		return root;
	}
	
	/*
	 * This Function will Construct The decision Tree
	 */

	public static node constructDecisionTree(node root, int[][] values, int[] isDone, int width, int[] indexList,
			node parent) {
		if (root == null) {
			root = new node();
			if (indexList == null || indexList.length == 0) {
				root.label = finalclassification(root, values, width);
				root.isLeaf = true;
				return root;
			}
			if (AllPositive(indexList, values, width)) {
				root.label = 1;
				root.isLeaf = true;
				return root;
			}
			if (AllNegative(indexList, values, width)) {
				root.label = 0;
				root.isLeaf = true;
				return root;
			}
			if (width == 1 || allattributesdone(isDone)) {
				root.label = finalclassification(root, values, width);
				root.isLeaf = true;
				return root;
			}
		}
		root = GetBestAttributeAndConstructNode(root, values, isDone, width,
		 indexList);
		root.parent = parent;
		if (root.Attribute != -1)
			isDone[root.Attribute] = 1;
		int leftIsDone[] = new int[isDone.length];
		int rightIsDone[] = new int[isDone.length];
		for (int j = 0; j < isDone.length; j++) {
			leftIsDone[j] = isDone[j];
			rightIsDone[j] = isDone[j];

		}

		root.left = constructDecisionTree(root.left, values, leftIsDone, width, root.leftIndices, root);
		root.right = constructDecisionTree(root.right, values, rightIsDone, width, root.rightIndices, root);
		return root;
	}

	public static node constructDecisionTreewithVarianceImpurity(node root, int[][] values, int[] isDone, int width, int[] indexList,
			node parent) {
		if (root == null) {
			root = new node();
			if (indexList == null || indexList.length == 0) {
				root.label = finalclassification(root, values, width);
				root.isLeaf = true;
				return root;
			}
			if (AllPositive(indexList, values, width)) {
				root.label = 1;
				root.isLeaf = true;
				return root;
			}
			if (AllNegative(indexList, values, width)) {
				root.label = 0;
				root.isLeaf = true;
				return root;
			}
			if (width == 1 || allattributesdone(isDone)) {
				root.label = finalclassification(root, values, width);
				root.isLeaf = true;
				return root;
			}
		}
		
		root = VarianceimpAndConstructNode(root, values, isDone, width, indexList);
		root.parent = parent;
		if (root.Attribute != -1)
			isDone[root.Attribute] = 1;
		int leftIsDone[] = new int[isDone.length];
		int rightIsDone[] = new int[isDone.length];
		for (int j = 0; j < isDone.length; j++) {
			leftIsDone[j] = isDone[j];
			rightIsDone[j] = isDone[j];

		}

		root.left = constructDecisionTree(root.left, values, leftIsDone, width, root.leftIndices, root);
		root.right = constructDecisionTree(root.right, values, rightIsDone, width, root.rightIndices, root);
		return root;
	}
	/*
	Implementing the postpruning algorithm given in the document
	 */
	public static node postPruneAlgorithm(String filePath, int L, int K, node root, int[][] values, int width) {
		node postPrunedTree = new node();
		int i = 0;
		postPrunedTree = root;
		double maxAccuracy = Accuracy(filePath, root);
		for (i = 0; i < L; i++) {
			node newRoot = duplicate(root);
			Random randomNumbers = new Random();
			int M = 1 + randomNumbers.nextInt(K);
			for (int j = 1; j <= M; j++) {
				count = 0;
				int noOfNonLeafNodes = CountNonLeafNodes(newRoot);
				if (noOfNonLeafNodes == 0)
					break;
				node nodeArray[] = new node[noOfNonLeafNodes];
				FillArray(newRoot, nodeArray);
				int s = randomNumbers.nextInt(noOfNonLeafNodes);
				nodeArray[s].isLeaf = true;
				nodeArray[s].label = finalclassificationatnode(nodeArray[s], values, width);
				nodeArray[s].left = null;
				nodeArray[s].right = null;

			}
			double accuracy = Accuracy(filePath, newRoot);

			if (accuracy > maxAccuracy) {
				postPrunedTree = newRoot;
				maxAccuracy = accuracy;
			}
		}
		return postPrunedTree;
	}

	/*
	 * This will be print the tree if we give yes.
	 */
	private static void printTree(node root, int printLines, String[] AttributeNames) {
		int printLinesForThisLoop = printLines;
		if (root.isLeaf) {
			System.out.println(" " + root.label);
			return;
		}
		for (int i = 0; i < printLinesForThisLoop; i++) {
			System.out.print("| ");
		}
		if (root.left != null && root.left.isLeaf && root.Attribute != -1)
			System.out.print(AttributeNames[root.Attribute] + "= 0 :");
		else if (root.Attribute != -1)
			System.out.println(AttributeNames[root.Attribute] + "= 0 :");

		printLines++;
		printTree(root.left, printLines, AttributeNames);
		for (int i = 0; i < printLinesForThisLoop; i++) {
			System.out.print("| ");
		}
		if (root.right != null && root.right.isLeaf && root.Attribute != -1)
			System.out.print(AttributeNames[root.Attribute] + "= 1 :");
		else if (root.Attribute != -1)
			System.out.println(AttributeNames[root.Attribute] + "= 1 :");
		printTree(root.right, printLines, AttributeNames);
	}


	/*
	 * This function checks if all the examples have output as 1
	 */
	public static boolean AllPositive(int[] indexList, int[][] values, int width) {
		boolean a = true;
		for (int i : indexList) {
			if (values[i][width] == 0) {
				a = false;
				break;
			}
		}
		return a;

	}

	/* This function checks if all the examples have output as 0 i */

	public static boolean AllNegative(int[] indexList, int[][] values, int width) {
		boolean a = true;
		for (int i : indexList) {
			if (values[i][width] == 1) {
				a = false;
				break;

			}
		}
		return a;

	}

	/*
	 * This function will check if all the Attributes are processed or not.
	 */
	public static boolean allattributesdone(int[] isDone) {
		boolean allDone = true;
		for (int i : isDone) {
			if (i == 0)
				allDone = false;
		}
		return allDone;
	}

	private static double calculateLog(double fraction) {
		return Math.log10(fraction) / Math.log10(2);
	}

	// This function will set the possible classification at a node
	public static int finalclassification(node root, int[][] values, int width) {
		int ones = 0;
		int zeros = 0;
		if (root.parent == null) {
			int i = 0;
			for (i = 0; i < values.length; i++) {
				if (values[i][width] == 1) {
					ones++;
				} else {
					zeros++;
				}
			}
		} else {
			for (int i : root.parent.leftIndices) {
				if (values[i][width] == 1) {
					ones++;
				} else {
					zeros++;
				}
			}

			for (int i : root.parent.rightIndices) {
				if (values[i][width] == 1) {
					ones++;
				} else {
					zeros++;
				}
			}
		}
		return zeros > ones ? 0 : 1;

	}





	/*
	  This function will create copy for the given tree and returns it.
	 */
	public static node duplicate(node root) {
		if (root == null)
			return root;

		node temp = new node();
		temp.label = root.label;
		temp.isLeaf = root.isLeaf;
		temp.leftIndices = root.leftIndices;
		temp.rightIndices = root.rightIndices;
		temp.Attribute = root.Attribute;
		temp.parent = root.parent;
		temp.left = duplicate(root.left); // cloning left child
		temp.right = duplicate(root.right); // cloning right child
		return temp;
	}

	/*
	 * This maps the nodes of tree into an array of nodes
	 */
	private static void FillArray(node root, node[] Array) {
		if (root == null || root.isLeaf) {
			return;
		}
		Array[count++] = root;

		FillArray(root.left, Array);

		FillArray(root.right, Array);

	}



	/*
	 * This function will measure Accuracy of the decision tree
	 */
	private static double Accuracy(String filePath, node root) {
		int[][] validationSet = constructValidationSet(filePath);
		double count = 0;
		for (int i = 1; i < validationSet.length; i++) {
			count += Classificationcheck(validationSet[i], root);
		}
		return count / validationSet.length;
	}

	/*
	 * This function will verify if the given Example is correctly classified
	 * based on the decision tree
	 * 
	 */
	private static int Classificationcheck(int[] setValues, node newRoot) {
		int index = newRoot.Attribute;
		int correctlyClassified = 0;
		node testingNode = newRoot;
		while (testingNode.label == -1) {

			if (setValues[index] == 1) {
				testingNode = testingNode.right;
			} else {
				testingNode = testingNode.left;
			}
			if (testingNode.label == 1 || testingNode.label == 0) {
				if (setValues[setValues.length - 1] == testingNode.label) {
					correctlyClassified = 1;
					break;
				} else {
					break;
				}
			}
			index = testingNode.Attribute;
		}
		return correctlyClassified;
	}

	/*
	 * This method will construct and return the validation set array from the
	 * file path specified.
	 */
	private static int[][] constructValidationSet(String filePath) {
		int[] widthAndLength = DimensionsofDataSheet(filePath);

		int[][] validationSet = new int[widthAndLength[1]][widthAndLength[0]];
		BufferedReader Reader = null;
		String line = "";
		try {
			Reader = new BufferedReader(new FileReader(filePath));
			int i = 0;
			int count = 0;
			while ((line = Reader.readLine()) != null) {
				String[] lineParameters = line.split(",");
				int j = 0;
				if (count == 0) {
					count++;
					continue;
				} else {
					for (String lineParameter : lineParameters) {
						validationSet[i][j++] = Integer.parseInt(lineParameter);
					}
				}
				i++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (Reader != null) {
				try {
					Reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return validationSet;
	}

	/*
	 * This function will return the finalValue of the classification based on
	 * manjority
	 */
	private static int finalclassificationatnode(node root, int[][] values, int width) {
		int ones = 0;
		int zeros = 0;
		if (root.leftIndices != null) {
			for (int i : root.leftIndices) {
				if (values[i][width] == 1) {
					ones++;
				} else {
					zeros++;
				}
			}
		}

		if (root.rightIndices != null) {
			for (int i : root.rightIndices) {
				if (values[i][width] == 1) {
					ones++;
				} else {
					zeros++;
				}
			}
		}
		return zeros > ones ? 0 : 1;
	}

	/*
	 * This function counts the number of non leaf nodes and returns it's count.
	 */
	private static int CountNonLeafNodes(node root) {
		if (root == null || root.isLeaf)
			return 0;
		else
			return (1 + CountNonLeafNodes(root.left) + CountNonLeafNodes(root.right));
	}


	private static int[] DimensionsofDataSheet(String csvFile) {
		BufferedReader Reader = null;
		String line = "";
		int count = 0;
		int[] widthAndLength = new int[2];
		try {

			Reader = new BufferedReader(new FileReader(csvFile));
			while ((line = Reader.readLine()) != null) {
				if (count == 0) {
					String[] country = line.split(",");
					widthAndLength[0] = country.length;
				}
				count++;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (Reader != null) {
				try {
					Reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		widthAndLength[1] = count;
		return widthAndLength;
	}

	/*
	 * This function loads all the required data
	 */
	private static void loaddata(String filePath, int[][] values, String[] AttributeNames, int[] isDone,
			int[] indexList, int width) {
		BufferedReader Reader = null;
		String line = "";
		for (int s = 0; s < width; s++) {
			isDone[s] = 0;
		}
		int s = 0;
		for (s = 0; s < values.length; s++) {
			indexList[s] = s;
		}
		try {

			Reader = new BufferedReader(new FileReader(filePath));
			int i = 0;
			while ((line = Reader.readLine()) != null) {
				String[] lineParameters = line.split(",");
				int j = 0;
				if (i == 0) {
					for (String lineParameter : lineParameters) {
						AttributeNames[j++] = lineParameter;
					}
				}

				else {

					for (String lineParameter : lineParameters) {
						values[i][j++] = Integer.parseInt(lineParameter);
					}
				}
				i++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (Reader != null) {
				try {
					Reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


}


class node {
	node parent;
	node left;
	node right;
	boolean isLeaf = false;
	int Attribute = -1;
	int leftIndices[];
	int rightIndices[];
	int label = -1;

}
