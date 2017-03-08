package ver6.assignee;

import common.Property;

public class A_Main {
	static String project =""; // swt, ui, aspectj, jdt, birt
	public static void main(String[] a) throws Exception
	{
		project = Property.getInstance().getTargetProduct();
		B_DB db = new B_DB();
		db.dropTable2();
		D_Crawler cr = new D_Crawler();//RUN
	}

}
