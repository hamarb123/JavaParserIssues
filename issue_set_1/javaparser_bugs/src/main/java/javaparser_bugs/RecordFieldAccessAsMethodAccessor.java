package javaparser_bugs;

public class RecordFieldAccessAsMethodAccessor
{
	public void m(RecordFieldAccessAsMethod a)
	{
		a.x();
	}
}
