package org.sjn.enlighten;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.YamlPrinter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class EnlightenApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(EnlightenApplication.class, args);

		URL temp = ClassLoader.getSystemResource("temp.zip");
		URI uri = temp.toURI();
		Path zipPath = Paths.get(uri);
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");

		String introspect = "/HelloWorld.java";

		try (FileSystem zipfs = FileSystems.newFileSystem(zipPath, ClassLoader.getSystemClassLoader())) {
			for (Path p : zipfs.getRootDirectories()) {
				System.out.println(p);
			}

			Path readPath = zipfs.getPath(introspect);

			Files.lines(readPath).forEach(System.out::println);

			CompilationUnit compilationUnit = JavaParser.parse(readPath);

			System.out.println("====");
			System.out.println(compilationUnit.findFirst(ClassOrInterfaceDeclaration.class).get().getName().asString());

			System.out.println(new YamlPrinter(true).output(compilationUnit.findRootNode()));


			new VoidVisitorAdapter<Object>() {
				@Override
				public void visit(MethodCallExpr n, Object arg) {
					super.visit(n, arg);
					if ("sendBody".equals(n.getNameAsString())) {
						System.out.println(n.getArguments().get(0).toString());
					}
				}

				@Override
				public void visit(FieldDeclaration n, Object arg) {
					super.visit(n, arg);
					if ("ProducerTemplate".equals(n.getElementType().toString())) {
						System.out.println("=" + n.getElementType() + "->" + n.getVariable(0).getNameAsString());
					}
				}

				@Override
				public void visit(ClassOrInterfaceDeclaration n, Object arg) {
					super.visit(n, arg);
					System.out.println(n.getNameAsString() + " at " + n.getName().getTokenRange().get().getBegin());
					System.out.println("*" + n.getName());
				}

				@Override
				public void visit(VariableDeclarationExpr n, Object arg) {
					super.visit(n, arg);
					if ("message".equals(n.getVariable(0).getNameAsString())) {
						System.out.println("-" + n.getElementType() + "->" + n.getVariable(0).getNameAsString());
					}
				}
			}.visit(compilationUnit, null);

		}
	}
}
