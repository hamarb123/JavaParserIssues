package javaparser_bugs;

class MethodLocalRecordMethod
{
	public void x()
	{
		record Record1()
		{
			public void y()
			{
			}
		}
	}
}
