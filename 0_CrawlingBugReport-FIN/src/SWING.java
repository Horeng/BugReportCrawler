import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class SWING extends JFrame implements ActionListener{
	JPanel jp1, jp2;
	private double[][] test;
	private JTextField[] jtf;
	String items[] = {"build information",
			"observed behavior",
			"expected behavior",
			"steps to reproduce",
			"stack trace",
			"code examples",
			"error reports",
			"test cases"};
	JLabel jl[] = new JLabel[9];
	JTextArea jt[] = new JTextArea[9];
	JScrollPane jsp[] = new JScrollPane[9];
	NeuralNet NN;
	JLabel jlcom, jlScore;
	public SWING() throws Exception{
		jlcom = new JLabel();
		jlScore = new JLabel();
		NN = new NeuralNet();
		NN.setNN();
		test = new double[1][9];
		setLayout(null);
		setTitle("D-Maker");
		setSize(800,800);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	String[] labelTitle = {"Summary",
			"Product",
			"Component",
			"Version",
			"Severity",
			"Hardware",
			"Operating system"};	

	JLabel[] label = new JLabel[labelTitle.length];
	
	for(int i=0; i<labelTitle.length;i++){
		label[i] = new JLabel(labelTitle[i]);
	}
		
	jtf = new JTextField[7];
	
	for(int i=0;i<jtf.length;i++){
		jtf[i] = new JTextField();
		jtf[i].setHorizontalAlignment(JTextField.RIGHT);
		jtf[i].setText("");
	}
	
	jp1 = new JPanel();
	jp1.setLayout(null);
	jp1.setBounds(5,5,785,150);
	jp1.setLocation(5, 15);
	
	for(int i=0;i<4;i++){
		label[i].setBounds(5, 30+i*25, 120, 20);
		label[i].setOpaque(true);
		jp1.add(label[i]);
	}
	for(int i=4;i<label.length;i++){
		label[i].setBounds(400, 30+(i-3)*25, 120, 20);
		label[i].setOpaque(true);
		jp1.add(label[i]);
	}
	
	jtf[0].setBounds(150, 30, 630, 20);
	jtf[0].setOpaque(true);
	jtf[0].setHorizontalAlignment(JTextField.LEFT);;
	jp1.add(jtf[0]);
	for(int i=1;i<4;i++){
		jtf[i].setBounds(150, 30+i*25, 150, 20);
		jtf[i].setOpaque(true);
		jtf[i].setHorizontalAlignment(JTextField.LEFT);
		jp1.add(jtf[i]);
	}
	for(int i=4;i<jtf.length;i++){
		jtf[i].setBounds(550, 30+(i-3)*25, 150, 20);
		jtf[i].setOpaque(true);
		jtf[i].setHorizontalAlignment(JTextField.LEFT);
		jp1.add(jtf[i]);
	}
	
//	JLabel line1 = new JLabel("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
//	line1.setBounds(5,150, 800, 15);
//	line1.setOpaque(true);
//	jp1.add(line1);
	TitledBorder tb1 = new TitledBorder(new LineBorder(Color.black),"Main");
	jp1.setBorder(tb1);
	add(jp1);

	
////////////////////////////////jp2	start
	jp2 = new JPanel();
	jp2.setLayout(null);
	jp2.setBounds(5,180,785,550);
	

	jl[1] = new JLabel("Build Information");
	jl[1].setBounds(25, 20, 150, 20);	
	jp2.add(jl[1]);
	jt[1] = new JTextArea();
	jt[1].setText("");
	jsp[1] = new JScrollPane(jt[1]);
	jsp[1].setBounds(25, 40, 350, 70);
	jp2.add(jsp[1]);
	
	jl[2] = new JLabel("Observed Behavior");
	jl[2].setBounds(25, 120, 150, 20);	
	jp2.add(jl[2]);
	jt[2] = new JTextArea();
	jsp[2] = new JScrollPane(jt[2]);
	jsp[2].setBounds(25, 140, 350, 70);
	jp2.add(jsp[2]);
	
	jl[3] = new JLabel("Expected Behavoir");
	jl[3].setBounds(25, 220, 150, 20);	
	jp2.add(jl[3]);
	jt[3] = new JTextArea();
	jsp[3] = new JScrollPane(jt[3]);
	jsp[3].setBounds(25, 240, 350, 70);
	jp2.add(jsp[3]);
	
	jl[4] = new JLabel("Steps to Reproduce");
	jl[4].setBounds(25, 320, 150, 20);	
	jp2.add(jl[4]);
	jt[4] = new JTextArea();
	jsp[4] = new JScrollPane(jt[4]);
	jsp[4].setBounds(25, 340, 350, 70);
	jp2.add(jsp[4]);
	
	jl[5] = new JLabel("Stack Trace");
	jl[5].setBounds(25, 420, 150, 20);	
	jp2.add(jl[5]);
	jt[5] = new JTextArea();
	jsp[5] = new JScrollPane(jt[5]);
	jsp[5].setBounds(25, 440, 350, 100);
	jp2.add(jsp[5]);
	
	jl[6] = new JLabel("Code Examples");
	jl[6].setBounds(405, 20, 150, 20);	
	jp2.add(jl[6]);
	jt[6] = new JTextArea();
	jsp[6] = new JScrollPane(jt[6]);
	jsp[6].setBounds(405, 40, 350, 150);
	jp2.add(jsp[6]);
	
	jl[7] = new JLabel("Error Reports");
	jl[7].setBounds(405, 200, 150, 20);	
	jp2.add(jl[7]);
	jt[7] = new JTextArea();
	jsp[7] = new JScrollPane(jt[7]);
	jsp[7].setBounds(405, 220, 350, 150);
	jp2.add(jsp[7]);

	jl[8] = new JLabel("Test Cases");
	jl[8].setBounds(405, 380, 150, 20);	
	jp2.add(jl[8]);
	jt[8] = new JTextArea();
	jsp[8] = new JScrollPane(jt[8]);
	jsp[8].setBounds(405, 400, 350, 140);
	jp2.add(jsp[8]);
	

	TitledBorder tb2 = new TitledBorder(new LineBorder(Color.black),"Description");
	jp2.setBorder(tb2);
	add(jp2);
	
	
	JButton jbApply = new JButton("Apply");
	jbApply.setBounds(710,730,80,40);
	jbApply.addActionListener(this);
	add(jbApply);
////////////////////////////////jp2 end	
	
	setVisible(true);
}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JButton b = (JButton) e.getSource();
		if(b.getText().equals("Apply")){
			try {
				for(int i=1;i<9;i++)
				{
					String str = jt[i].getText();
					if(str.equals("")) test[0][i]=0;
					else test[0][i]=1;
				}
				for(int i=1;i<=5;i++)
					test[0][i-1]=test[0][i];
				test[0][5]=0;
				
				double[] score = NeuralNet.testNN(test, NN.weight_kj, NN.weight_ji, NN.bias_k, NN.bias_j, NN.hidden, NN.output);
				double stmp = score[0];
				String str = String.format("%.2f", score[0]*100);
				System.out.println("SCORE : "+ str);
				
				remove(jlScore);
				jlScore = new JLabel("SCORE : "+str);
				jlScore.setBounds(15,730,100,20);
				add(jlScore);
				
				int max = -1;
				double mscore = score[0];
				for(int i=0;i<9;i++)
				{

					double k = test[0][i];
					test[0][i]=1;

					double[] tmp = NeuralNet.testNN(test, NN.weight_kj, NN.weight_ji, NN.bias_k, NN.bias_j, NN.hidden, NN.output);

					String strtmp = String.format("%.2f", tmp[0]*100);
//					System.out.println("Score+"+strtmp);
					if(mscore < tmp[0]){
						max = i;
						mscore = tmp[0];
					};
					test[0][i]=k;
				}
//				System.out.println("Max : "+max);
				String comment;
				if(max==-1) comment = "The score is already maximum.";
				else{
					String strtmp = String.format("%.2f", (mscore-stmp)*100);
					if(max>=5) max= max-1;
					comment = "You can increase score ("+ strtmp + ") By adding "+ items[max] +".";
				}
//				JLabel jlcom = new JLabel(comment);
				this.remove(jlcom);
				jlcom.setText(comment);
				jlcom.setBounds(15,750,500,20);
				add(jlcom);
				repaint();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public double[][] getTest(){
		return test;
	}


}
