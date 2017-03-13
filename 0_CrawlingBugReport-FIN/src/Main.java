import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Main {

	
	public static void main(String args[]) throws Exception{

/*		NeuralNet nn = new NeuralNet();
		nn.learnDescription();
		nn.setNN();

		double[][] test_point = new double[1][9];

		File file = new File("bugID_short.txt");
		FileReader fr = null;
		BufferedReader br = null;	

		fr = new FileReader(file);
		br = new BufferedReader(fr);
		String line = null;
		while((line=br.readLine())!=null)
		{
			int space = line.indexOf(" ");
			int id = Integer.valueOf(line.substring(0, space));
			System.out.println(id);
			test_point[0][0] = Integer.valueOf(line.substring(space+1,space+2));//BuildInformation 	7.66
			test_point[0][1] = Integer.valueOf(line.substring(space+3,space+4));//Observed Behavior	11.72
			test_point[0][2] = Integer.valueOf(line.substring(space+5,space+6));//Expected Behavior	2.3
			test_point[0][3] = Integer.valueOf(line.substring(space+7,space+8));//Steps to Reproduce	7.07
			test_point[0][4] = Integer.valueOf(line.substring(space+9,space+10));//Stack Trace			4.72
			test_point[0][5] = Integer.valueOf(line.substring(space+13,space+14));//Code Example			53.95
			test_point[0][6] = Integer.valueOf(line.substring(space+15,space+16));//Error Reports		4.82
			test_point[0][7] = Integer.valueOf(line.substring(space+17,space+18));//Test Cases			0.93

			for(int i=0;i<9;i++)
				System.out.print(test_point[0][i]);
			System.out.println();
			double[] score = NeuralNet.testNN(test_point, nn.weight_kj, nn.weight_ji, nn.bias_k, nn.bias_j, nn.hidden, nn.output);
			String str = String.format("%.2f", score[0]*100);
			System.out.println("SCORE : "+ str);
			
			BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt",true));
			bw.write(id+"	"+score[0]+"\r\n");
			bw.close();
		}
		if(fr!=null)fr.close();
		if(br!=null)br.close();

*/
		new SWING();
	}
}
