import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class NeuralNet {
	public static int NUM_INPUT;
	public static int NUM_HIDDEN;
	public static int NUM_OUTPUT;
	public static int NUM_TRAINING_DATA;
	public static int NUM_TEST_DATA;
	public static int MAX_EPOCH;
	public static double LEARNING_RATE = 0.15;
	
	public double hidden[];
	public double output[];
	public double weight_kj[][];
	public double weight_ji[][];
	public double bias_k[];
	public double bias_j[];

	public NeuralNet() throws Exception{
		NUM_INPUT = 8;
		NUM_HIDDEN = 10;
		NUM_OUTPUT = 1;
		MAX_EPOCH = 100000;
//		MAX_EPOCH = 1000;

		hidden = new double[NUM_HIDDEN];
		output = new double[NUM_OUTPUT];
		weight_kj = new double[NUM_OUTPUT][NUM_HIDDEN];
		weight_ji = new double[NUM_HIDDEN][NUM_INPUT];
		bias_k = new double[NUM_OUTPUT];
		bias_j = new double[NUM_HIDDEN];		
		//INPUT
		NUM_TRAINING_DATA = 0;
		NUM_TEST_DATA = 1;

	}

	public void learnDescription() throws Exception{	
//		DataInputStream file = new DataInputStream(new FileInputStream("Report.txt"));
//     	This part is for learn about Bug Description
		BufferedReader br = new BufferedReader(new FileReader("Report.txt"));
		double[][] training_point = new double[800][8];
		double[][] training_target = new double[800][1];
		
		while(true)		{
			String line = br.readLine();
			if(line==null) break;
			int space = line.indexOf(" ");
			System.out.println(line);
			for(int i=1;i<=8;i++){
				training_point[NUM_TRAINING_DATA][i-1] = Double.valueOf(String.valueOf(line.charAt(space+i*2-1)));
				System.out.print(training_point[NUM_TRAINING_DATA][i-1]);
			}System.out.println();
			space = line.lastIndexOf(" ");
			training_target[NUM_TRAINING_DATA][0] = Double.valueOf(String.valueOf(line.substring(space+1, line.length())));
			System.out.println(training_target[NUM_TRAINING_DATA][0]);
			NUM_TRAINING_DATA++;
		}
		
		br.close();


/*		NUM_INPUT = 1; //other NN example
		NUM_HIDDEN = 10;
		NUM_OUTPUT = 2;
		NUM_TRAINING_DATA = 11;
		NUM_TEST_DATA = 51;
		
		double training_point[][] 
				= { {0.0}, {0.1}, {.2}, {.3}, {.4}, {.5}, {.6}, {.7}, {.8}, {.9}, {1.0} } ;

		double training_target[][]
				= { {0.00,0.0}, {0.36,0.1}, {0.64,0.2}, {0.84,0.3}, {0.96,0.4}, {1.00,0.5}, {0.96,0.6}, {0.84,0.7}, {0.64,0.8}, {0.36,0.9}, {0.00,1.0} } ;

		double test_point[][]= { {0}, {0.02}, {0.04}, {0.06}, {0.08}, {0.1}, {0.12}, {0.14}, {0.16}, {0.18}, 
				{0.2}, {0.22}, {0.24}, {0.26}, {0.28}, {0.3}, {0.32}, {0.34}, {0.36}, {0.38}, 
				{0.4}, {0.42}, {0.44}, {0.46}, {0.48}, {0.5}, {0.52}, {0.54}, {0.56}, {0.58}, 
				{0.6}, {0.62}, {0.64}, {0.66}, {0.68}, {0.7}, {0.72}, {0.74}, {0.76}, {0.78}, 
				{0.8}, {0.82}, {0.84}, {0.86}, {0.88}, {0.9}, {0.92}, {0.94}, {0.96}, {0.98}, {1} } ;
*/
//		NUM_TRAINING_DATA = 4;
//		NUM_TEST_DATA = 4;
//		double[][] training_point = { { 1.0,1.0 },{ 1.0,0.0 },{ 0.0,1.0 },{ 0.0,0.0 } };
//		double[][] training_target = { { 0.0 },{ 1.0 },{ 1.0 },{ 0.0 } };
//		double[][] test_point = { { 0,0 },{ 0,1 },{ 1,0 },{ 1,1 } };
		//INPUT END
		
		NeuralNetStart(training_point, training_target); //****Only for first time
	}
	
	public void setNN() throws IOException{
			
		FileInputStream fis = new FileInputStream("NN.bin");
		DataInputStream dis = new DataInputStream(fis);
//		double hidden[] = new double[NUM_HIDDEN];
//		double output[] = new double[NUM_OUTPUT];
//		double weight_kj[][] = new double[NUM_OUTPUT][NUM_HIDDEN];
//		double weight_ji[][] = new double[NUM_HIDDEN][NUM_INPUT];
//		double bias_k[] = new double[NUM_OUTPUT];
//		double bias_j[] = new double[NUM_HIDDEN];
		
		for(int n=0;n<NUM_HIDDEN;n++)
		{
			hidden[n]=dis.readDouble();
		}
		for(int n=0;n<NUM_OUTPUT;n++)
		{
			output[n]=dis.readDouble();
		}
		for(int n=0;n<NUM_OUTPUT;n++)
		{
			for(int m=0;m<NUM_HIDDEN;m++)
				weight_kj[n][m]=dis.readDouble();
		}
		for(int n=0;n<NUM_HIDDEN;n++)
		{
			for(int m=0;m<NUM_INPUT;m++)
				weight_ji[n][m]=dis.readDouble();
		}
		for(int n=0;n<NUM_OUTPUT;n++)
		{
			bias_k[n]=dis.readDouble();
		}
		for(int n=0;n<NUM_HIDDEN;n++)
		{
			bias_j[n]=dis.readDouble();
		}
		dis.close();
		fis.close();
		
//		testNN(test_point, weight_kj, weight_ji, bias_k, bias_j,hidden, output);
	}
	
	public static void NeuralNetStart(double training_point[][], double training_target[][]) throws Exception{
		double hidden[] = new double[NUM_HIDDEN];
		double output[] = new double[NUM_OUTPUT];
		double weight_kj[][] = new double[NUM_OUTPUT][NUM_HIDDEN];
		double weight_ji[][] = new double[NUM_HIDDEN][NUM_INPUT];
		double bias_k[] = new double[NUM_OUTPUT];
		double bias_j[] = new double[NUM_HIDDEN];
		double delta_kj[][] = new double[NUM_OUTPUT][NUM_HIDDEN];
		double delta_ji[][] = new double[NUM_HIDDEN][NUM_INPUT];
		double delta_bias_k[] = new double[NUM_OUTPUT];
		double delta_bias_j[] = new double[NUM_HIDDEN];
		double error;

		int k, p;

		InitWeight(weight_kj, weight_ji, bias_k, bias_j);

		// loop for learning
		System.out.println("******* Training of NN (Iteration : Error) *******");

		for (int epoch = 0; epoch <= MAX_EPOCH; epoch++)
		{
			error = 0;

			ResetDelta(delta_kj, delta_ji, delta_bias_k, delta_bias_j);

			for (p = 0; p < NUM_TRAINING_DATA; p++)
			{
				Forward(training_point[p], weight_kj, weight_ji, bias_k, bias_j,
					hidden, output);

				for (k = 0; k < NUM_OUTPUT; k++)
					error += (output[k] - training_target[p][k])*(output[k] - training_target[p][k]);

				Backward(training_point[p], training_target[p], hidden, output, weight_kj,
					delta_kj, delta_ji, delta_bias_k, delta_bias_j);
			}

			UpdateWeights(delta_kj, delta_ji, delta_bias_k, delta_bias_j,
				weight_kj, weight_ji, bias_k, bias_j);

			if (epoch % 1000 == 0) System.out.println(epoch + ": "+error);
		}

		//save trained nn here
			FileOutputStream fos = new FileOutputStream("NN.bin");
			DataOutputStream dos = new DataOutputStream(fos);
			
			for(int n=0;n<NUM_HIDDEN;n++)
			{
				dos.writeDouble(hidden[n]);
			}
			for(int n=0;n<NUM_OUTPUT;n++)
			{
				dos.writeDouble(output[n]);
			}
			for(int n=0;n<NUM_OUTPUT;n++)
			{
				for(int m=0;m<NUM_HIDDEN;m++)
					dos.writeDouble(weight_kj[n][m]);
			}
			for(int n=0;n<NUM_HIDDEN;n++)
			{
				for(int m=0;m<NUM_INPUT;m++)
					dos.writeDouble(weight_ji[n][m]);
			}
			for(int n=0;n<NUM_OUTPUT;n++)
			{
				dos.writeDouble(bias_k[n]);
			}
			for(int n=0;n<NUM_HIDDEN;n++)
			{
				dos.writeDouble(bias_j[n]);
			}
			
			dos.close();
			fos.close();
		//save end
	}
		
		// testing with un-learned point 
		public static double[] testNN(double[][] test_point,double[][] weight_kj,double[][] weight_ji,
				double[] bias_k,double[] bias_j,double[] hidden,double[] output){
//		System.out.println("");
//		System.out.println("******* Test of NN (Input ; Output of NN) *******");

		for (int i = 0; i < NUM_TEST_DATA; i++)
		{
			Forward(test_point[i], weight_kj, weight_ji, bias_k, bias_j,
				hidden, output);

//			for (int k = 0; k < NUM_INPUT; k++)
//				System.out.print(test_point[i][k]+" ");
//			System.out.print("; ");

//			for (int k = 0; k < NUM_OUTPUT; k++)
//			{
//				System.out.print(output[k]);
//			}
//			System.out.println("");
		}
		return output;
	}
	
	
	
	
	private static double SIGMOID(double x)
	{
		return 1/(1+Math.exp(-(x)));
	}
	
	public static void InitWeight(double weight_kj[][], double weight_ji[][],
			double bias_k[], double bias_j[])
	{
		int i, j, k;
		Random random = new Random();

		//weight initialization
		for (k = 0; k < NUM_OUTPUT; k++)
			for (j = 0; j < NUM_HIDDEN; j++)
				weight_kj[k][j] = 1.0 * (random.nextDouble() % 1000 - 500) / 5000;

		for (j = 0; j < NUM_HIDDEN; j++)
			for (i = 0; i < NUM_INPUT; i++)
				weight_ji[j][i] = 1.0 * (random.nextDouble() % 1000 - 500) / 5000;

		for (k = 0; k < NUM_OUTPUT; k++)
			bias_k[k] = 1.0 * (random.nextDouble() % 1000 - 500) / 5000;

		for (j = 0; j < NUM_HIDDEN; j++)
			bias_j[j] = 1.0 * (random.nextDouble() % 1000 - 500) / 5000;
	}
	
	public static void ResetDelta(double delta_kj[][], double delta_ji[][],
			double delta_bias_k[], double delta_bias_j[])
	{
		int i, j, k;

		//weight initialization
		for (k = 0; k < NUM_OUTPUT; k++)
			for (j = 0; j < NUM_HIDDEN; j++)
				delta_kj[k][j] = 0;

		for (j = 0; j < NUM_HIDDEN; j++)
			for (i = 0; i < NUM_INPUT; i++)
				delta_ji[j][i] = 0;

		for (k = 0; k < NUM_OUTPUT; k++)
			delta_bias_k[k] = 0;

		for (j = 0; j < NUM_HIDDEN; j++)
			delta_bias_j[j] = 0;
	}

	public static void Forward(double training_point[],
			double weight_kj[][], double weight_ji[][],
			double bias_k[], double bias_j[],
			double hidden[], double output[])
	{
		int i, j, k;
		double net_j, net_k;

		//evaluate the output of hidden nodes
		for (j = 0; j < NUM_HIDDEN; j++)
		{
			net_j = 0;
			for (i = 0; i < NUM_INPUT; i++)
				net_j += weight_ji[j][i] * training_point[i];
			net_j += bias_j[j];
			hidden[j] = SIGMOID(net_j);
		}

		//evaluate the output of output nodes
		for (k = 0; k < NUM_OUTPUT; k++)
		{
			net_k = 0;
			for (j = 0; j < NUM_HIDDEN; j++)
				net_k += weight_kj[k][j] * hidden[j];
			net_k += bias_k[k];
				output[k] = SIGMOID(net_k);
		}
	}
	
	public static void Backward(double training_point[], double training_target[],
			double hidden[], double output[],
			double weight_kj[][],
			double delta_kj[][], double delta_ji[][],
			double delta_bias_k[], double delta_bias_j[])

	{
		int i, j, k;

		//evaluate delta_kj
		for (k = 0; k < NUM_OUTPUT; k++)
			for (j = 0; j < NUM_HIDDEN; j++)
				delta_kj[k][j] += -output[k] * (1 - output[k])*(training_target[k] - output[k])*hidden[j];

		for (k = 0; k < NUM_OUTPUT; k++)
			delta_bias_k[k] += -output[k] * (1 - output[k])*(training_target[k] - output[k]);

		//evaluate delta_ji
		for (j = 0; j < NUM_HIDDEN; j++)
			for (i = 0; i < NUM_INPUT; i++)
			{
				double delta_k = 0;
				for (k = 0; k < NUM_OUTPUT; k++)
					delta_k += -output[k] * (1 - output[k])*(training_target[k] - output[k])*weight_kj[k][j];
				delta_ji[j][i] += delta_k*hidden[j] * (1 - hidden[j])*training_point[i];
			}

		for (j = 0; j < NUM_HIDDEN; j++)
		{
			double delta_k = 0;
			for (k = 0; k < NUM_OUTPUT; k++)
				delta_k += -output[k] * (1 - output[k])*(training_target[k] - output[k])*weight_kj[k][j];
			delta_bias_j[j] += delta_k*hidden[j] * (1 - hidden[j]);
		}
	}
	
	public static void UpdateWeights(double delta_kj[][], double delta_ji[][],
			double delta_bias_k[], double delta_bias_j[],
			double weight_kj[][], double weight_ji[][],
			double bias_k[], double bias_j[])
		{
			int i, j, k;

			//update weights
			for (k = 0; k < NUM_OUTPUT; k++)
				for (j = 0; j < NUM_HIDDEN; j++)
					weight_kj[k][j] -= LEARNING_RATE*delta_kj[k][j];

			for (k = 0; k < NUM_OUTPUT; k++)
				bias_k[k] -= LEARNING_RATE*delta_bias_k[k];

			for (j = 0; j < NUM_HIDDEN; j++)
				for (i = 0; i < NUM_INPUT; i++)
					weight_ji[j][i] -= LEARNING_RATE*delta_ji[j][i];

			for (j = 0; j < NUM_HIDDEN; j++)
				bias_j[j] -= LEARNING_RATE*delta_bias_j[j];

		}
	
	public void PrintWeight(double weight_kj[][], double weight_ji[][],
			double bias_k[], double bias_j[])
		{
			int i, j, k;

			//print weights
			for (j = 0; j < NUM_HIDDEN; j++)
				for (i = 0; i < NUM_INPUT; i++)
					System.out.print(weight_ji[j][i]);

			for (j = 0; j < NUM_HIDDEN; j++)
				System.out.print(bias_j[j]);

			for (k = 0; k < NUM_OUTPUT; k++)
				for (j = 0; j < NUM_HIDDEN; j++)
					System.out.print(weight_kj[k][j]);

			for (k = 0; k < NUM_OUTPUT; k++)
				System.out.println(bias_k[k]);

	}	
}
