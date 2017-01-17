package ver5.struct;


public class D_Crawler {
	private C_Parser pa;
	private String url = "https://bugs.eclipse.org/bugs/show_bug.cgi?id=";


	D_Crawler() throws Exception
	{
		System.out.println("-------CRAWLER CREATE-------");
		pa = new C_Parser(url);

		pa.parse();

		this.pa.quit();
		System.out.println("-------CRAWLER QUIT-------");
	}

}
