package javaparser_bugs;

import java.util.HashMap;
import java.util.List;

public class LambdaParametersCallMethod
{
	void x(HashMap<String, List<Integer>> a)
	{
		a.values().forEach((list) -> list.toArray());
	}
}
