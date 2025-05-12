package javaparser_bugs;

import java.util.List;
import java.util.function.Consumer;

public class InferredGenericArgumentsLambda2
{
	class Helper<T>
	{
		public Helper(Consumer<T> consumer) { }
	}

	Helper<List<Integer>> field;
	void x()
	{
		field = new Helper<>((x) -> x.toArray());
	}
}
