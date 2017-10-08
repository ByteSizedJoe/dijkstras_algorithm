// Homework 7: Dijkastra’s Algorithm. This program will construct a forwarding
// table for a source router which will be designated V0, using Dijkastra’s algorithm.
// The program will first prompt a user for an input on how many routers will be in the
// topology and then will validate that number to make sure there are at least two routers input
// the system. Next the program will then use a topo.txt file which will be the cost matrix
// for the algorithm. The numbers in this file will be validated. Through each iteration of the 
// loop, this program will print out the set N’, the set Y’, D(i) for each i and p(i) for each i.
// The programs final output is the shortest path tree in the form a forwarding table for V0.
// By: Joseph Medina for CS3700 Networks
// 07.17.17

import java.util.*;
import java.io.*;

class dijkstrasalgorithm
{
	public static void main(String[] args)
	{
		Scanner input = new Scanner(System.in);//Create a scanner to get input.  
		int numberRouters = 0; //This number represents the number of routers that we will calculate.
        int sourceRouter = 0;//Statically assign V0 as source router for this assignment, could
                                    //take input from user to designate source router.

		System.out.println("Hello there, please input the number of routers you have in the topology we are analyzing."
         + "\nEnter a number greater than 2:");
		numberRouters = input.nextInt();//Take in the users input for number of routers.
        System.out.println();

        //Check if the number of routers is greater than 2, if not, present a error message.
        while(numberRouters < 2)
        {
            System.out.println("You cannot analyze a topology of 1 or less routers, input a number greater than 2:");
            numberRouters = input.nextInt();
        }

        //Setup the input file, row number, scanner from file, 2d array
        int[][] costMatrix = new int[numberRouters][numberRouters];

        //Set all the values in the dynamic matrix to -1 to represent infinity.
        //This shows routers that do not have a route directly to another router.
        //Set all the values to 0 in which i = j, showing that there is no cost
        //of a router to itself.
        for(int i = 0; i < costMatrix.length; i++)
        {
            for(int j = 0; j < costMatrix[i].length; j++)
            {
                if(j == i)
                {
                    costMatrix[i][j] = 0;
                }
                else
                    costMatrix[i][j] = -1;
            }
        }

        File inputFile = new File("topo.txt");
        String validation;
        Scanner inputFromFile;
        int rowNumber = 0;
        int x, y, z; //Two temps used to populate the matrix.
        try
        {
            inputFromFile = new Scanner(inputFile);
            inputFromFile.nextLine();//Ignore the first line as it's just the legend.

            while(inputFromFile.hasNext())
            {
                rowNumber += 1; //Increment the row number.

                x = inputFromFile.nextInt(); //First router
                y = inputFromFile.nextInt(); //Next router
                z = inputFromFile.nextInt(); //Cost between these two routers.

                while(x < 0 || x >= numberRouters || y < 0 || y >= numberRouters || z < 0)
                {
                    input = new Scanner(System.in);
                    System.out.println("Invalid number detected in row " + rowNumber);
                    System.out.println("Fix the error, then type the name of the cost matrix text file to revalidate:");
                    validation = input.nextLine();

                    rowNumber = 0;
                    inputFile = new File(validation);

                    try
                    {
                        inputFromFile = new Scanner(inputFile);
                        inputFromFile.nextLine();//Ignore the legend
                        rowNumber += 1; //Increment the row number.

                        x = inputFromFile.nextInt(); //First router
                        y = inputFromFile.nextInt(); //Next router
                        z = inputFromFile.nextInt(); //Cost between these two routers.
                    }
                    catch (FileNotFoundException fileNotFoundException)
                    {
                        System.err.println();
                        System.exit(1);
                    }
                }

                costMatrix[x][y] = z;
                costMatrix[y][x] = z;
            }
        }
        catch (FileNotFoundException fileNotFoundException)
        {
            System.err.println();
            System.exit(0);
        }

        System.out.println("The Cost Matrix is below:");
        System.out.println("In this implementation, -1 is used to represent infinity.");

        //The following code is just for debugging to make sure the matrix has the correct values.
        int printHelper = 0;
        System.out.print("MX");
        while(printHelper < numberRouters)
        {
            System.out.print("  " + "V" + printHelper + " ");
            printHelper++;
        }
        System.out.println();

        for (int i = 0; i < costMatrix.length; i++)
        {
            System.out.print("V" + i );

            for (int j = 0; j < costMatrix[i].length; j++) {
                System.out.print("  " +costMatrix[i][j] + "  ");
            }
            System.out.println();
        }
        //End of debugging print code.

	    //Begin Dijkstra's Algorithm
        //Initialization
        ArrayList<String> nPrime = new ArrayList<>(); //Set of nodes whose least cost path is known
        ArrayList<String> yPrime = new ArrayList<>(); //Set of edges known in shortest path from source -> dest
        ArrayList<Integer> dBar = new ArrayList<>(); //Current value cost path -> source.
        ArrayList<String> pBar = new ArrayList<>(); //Predessor node long path -> source.
        int k = 0; //To keep track of algorithm.

        System.out.println("\nIntialization:");

        nPrime.add("V" + Integer.toString(sourceRouter)); //Add the source router first.
        System.out.println("N' = " + nPrime.toString());

        System.out.println("Y' = " + yPrime.toString());

        //Populate dBar with the costs that we know.
        for(int i = 0; i < costMatrix.length; i++)
        {
            dBar.add(i, costMatrix[sourceRouter][i]);
        }
        System.out.println("D" + k + " = " + dBar.toString());

        //Populate pBar with predessor nodes.
        for(int i = 0; i < costMatrix.length; i++)
        {
            if(i == sourceRouter)
            {
                pBar.add(i, "-");
            }
            else if(dBar.get(i) == -1)
            {
                pBar.add(i, "-");
            }
            else
            {
                pBar.add(i, "V" + Integer.toString(k));
            }

        }
        System.out.println("P" + k + " = " + pBar.toString());

        while(k < numberRouters - 1)
        {
            System.out.println("\nIteration " + (k+1) + ":");
            k++; //Evaluate the next router.
            int cost = dBar.get(k); //Currently known cost to source.
            //System.out.println("Cost for iteration " + k + " is: " + cost); //Debugging, not needed for output
            nPrime.add("V" + Integer.toString(k));

            //Create a temp array to compare to currently known costs to dest
            //the temp array is the costs of whatever k node we are currently
            //evaluating
            ArrayList<Integer> temp = new ArrayList<>();
            for(int i = 0; i < costMatrix.length; i++)
            {
                if (costMatrix[k][i] == -1)
                {
                    temp.add(i, costMatrix[k][i]);
                }
                else
                {
                    temp.add(i, costMatrix[k][i] + cost);
                }
            }
            //System.out.println("TMP = " + temp.toString()); //Debugging, not needed for output

            //Time to compare the costs that we know with the costs of K(plus the cost to travel to K)
            for(int i = 0; i < numberRouters; i++)
            {
                if(temp.get(i) < dBar.get(i))
                {
                    if(temp.get(i) != 0 && temp.get(i) != -1)
                    {
                        dBar.set(i, temp.get(i));
                        pBar.set(i, "V" + Integer.toString(k));
                    }
                }
                if(dBar.get(i) == -1 && (temp.get(i) != 0 || temp.get(i) != -1))
                {
                    dBar.set(i, temp.get(i));

                    if(dBar.get(i) != -1)
                    {
                        pBar.set(i, "V" + Integer.toString(k));
                    }
                }
            }

            //Update Y', create our pairs of known shortest edge
            String adder = "{" + pBar.get(k) + ", V" + k + "}";
            yPrime.add(k-1, adder);

            for(int i = 0; i < yPrime.size(); i++)
            {
                adder = "{" + pBar.get(i+1) + ", V" + (i+1) + "}";
                yPrime.set(i, adder);
            }

            //Output all the values needed.
            System.out.println("N' = " + nPrime.toString());
            System.out.println("Y' = " + yPrime.toString());
            System.out.println("D" + k + " = " + dBar.toString());
            System.out.println("P" + k + " = " + pBar.toString());
        }

        //Time to create the forwarding table..
        String link;
        System.out.println("\nForwarding Table: ");
        System.out.println("Destination     Link");
        for(int i = 1; i < nPrime.size(); i++)
        {
            int j = i;

            while(!(pBar.get(j).equals("V0")))
            {
                j = Integer.parseInt(pBar.get(j).replaceAll("V", ""));
                //System.out.println(pBar.get(i).replaceAll("V", ""));
            }

            if(pBar.get(j).equals("V0"))
            {
                link = "(" + "V0" + " , " + "V"+j + ")";
                System.out.println("V" + i + "              " + link);
            }
            else
            {

            }
        }
	}
}