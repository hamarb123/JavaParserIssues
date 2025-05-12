package javaparser_bugs;

import java.util.List;

public class MethodCallLoopWithSameName
{
	List<Integer> x;
	void y()
	{
		for (Integer x : x.toArray(new Integer[5]))
		{
		}
	}
}
