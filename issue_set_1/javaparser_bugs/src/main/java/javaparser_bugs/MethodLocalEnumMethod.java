package javaparser_bugs;

public class MethodLocalEnumMethod
{
	public void x()
	{
		enum Enum1
		{
			A;
			public void y()
			{
			}
		}
	}
}
