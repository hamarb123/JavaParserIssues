package javaparser_bugs;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Supplier;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedArrayType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public class Main
{
	public static void main(String[] args) throws Throwable
	{
		String dir = Path.of(System.getProperty("user.dir"), "src", "main", "java", "javaparser_bugs").toAbsolutePath().toString();
		TypeSolver symbolResolver = new CombinedTypeSolver(new ReflectionTypeSolver(true), new JavaParserTypeSolver(dir));
		JavaParser parser = new JavaParser(new ParserConfiguration().setSymbolResolver(new JavaSymbolSolver(symbolResolver)));
		for (File file : new File(dir).listFiles())
		{
			if ("Main.java".equals(file.getName())) continue;
			class X
			{
				public <T> T checkError(String actionName, Node n, Supplier<T> function)
				{
					try
					{
						return function.get();
					}
					catch (Throwable t)
					{
						String nodeText = n instanceof CallableDeclaration<?> cd ? cd.getDeclarationAsString() : n.toString();
						System.out.println("Fail on " + nodeText + " in " + file.getName() + " for action " + actionName + " with " + t.getClass().getName() + " " + t.getMessage());
						return null;
					}
				}
			}

			CompilationUnit cu = parser.parse(file).getResult().get();
			cu.setData(Node.SYMBOL_RESOLVER_KEY, parser.getParserConfiguration().getSymbolResolver().get()); // If you remove this line, you get a bunch of IllegalStateException Symbol resolution not configured errors

			if ("MethodLocalEnumMethod.java".equals(file.getName()))
			{
				Node n = cu.getChildNodes().get(1).getChildNodes().get(2).getChildNodes().get(3).getChildNodes().get(0);
				System.out.println();
				System.out.println("MethodLocalEnumMethod.java node type: " + n.getClass().toString() + " has text " + n.toString() + " in parent's parent " + n.getParentNode().get().getParentNode().get().toString());
				System.out.println();
				continue;
			}

			cu.accept(new VoidVisitorAdapter<String>()
			{
				@Override
				public void visit(MethodDeclaration n, String arg)
				{
					handleResolvedMethod(n, new X().checkError("resolve MethodDeclaration", n, () -> n.resolve()));
					super.visit(n, arg);
				}

				@Override
				public void visit(MethodCallExpr n, String arg)
				{
					handleResolvedMethod(n, new X().checkError("resolve MethodCallExpr", n, () -> n.resolve()));
					super.visit(n, arg);
				}

				@Override
				public void visit(ConstructorDeclaration n, String arg)
				{
					handleResolvedMethod(n, new X().checkError("resolve ConstructorDeclaration", n, () -> n.resolve()));
					super.visit(n, arg);
				}

				@Override
				public void visit(ExplicitConstructorInvocationStmt n, String arg)
				{
					handleResolvedMethod(n, new X().checkError("resolve ExplicitConstructorInvocationStmt", n, () -> n.resolve()));
					super.visit(n, arg);
				}

				@Override
				public void visit(ObjectCreationExpr n, String arg)
				{
					handleResolvedMethod(n, new X().checkError("resolve ObjectCreationExpr", n, () -> n.resolve()));
					super.visit(n, arg);
				}

				private void handleResolvedMethod(Node n, ResolvedMethodDeclaration decl)
				{
					if (decl == null) return;
					handleResolvedTypeDeclaration(n, new X().checkError("declaringType", n, () -> decl.declaringType()));
					handleResolvedType(n, new X().checkError("getReturnType", n, () -> decl.getReturnType()));
					var list = new X().checkError("formalParameterTypes", n, () -> decl.formalParameterTypes());
					if (list != null) list.forEach((x) -> handleResolvedType(n, x));
				}
				private void handleResolvedMethod(Node n, ResolvedConstructorDeclaration decl)
				{
					if (decl == null) return;
					handleResolvedTypeDeclaration(n, new X().checkError("declaringType", n, () -> decl.declaringType()));
					var list = new X().checkError("formalParameterTypes", n, () -> decl.formalParameterTypes());
					if (list != null) list.forEach((x) -> handleResolvedType(n, x));
				}
				private void handleResolvedType(Node n, ResolvedType rt)
				{
					if (rt == null) return;
					rt = rt.erasure();
					if (!(rt instanceof ResolvedReferenceType rrt))
					{
						if (rt instanceof ResolvedArrayType rat) handleResolvedType(n, new X().checkError("getComponentType", n, () -> rat.getComponentType()));
						return;
					}
					handleResolvedTypeDeclaration(n, rrt.getTypeDeclaration().get());
				}
				private void handleResolvedTypeDeclaration(Node n, ResolvedTypeDeclaration rtd)
				{
					if (rtd == null) return;
					handleResolvedTypeDeclaration(n, new X().checkError("declaringType", n, () -> rtd.containerType().orElse(null)));
				}
			}, "");
		}
	}
}
