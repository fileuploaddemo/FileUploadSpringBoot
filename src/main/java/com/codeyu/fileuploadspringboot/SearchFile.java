package com.codeyu.fileuploadspringboot;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class SearchFile {

	public static class SearchFileVisitor extends SimpleFileVisitor<Path> {

		private final PathMatcher pathMatcher;
		private int matchCount = 0;

		SearchFileVisitor(String globPattern) {
			pathMatcher = FileSystems.getDefault().getPathMatcher(
					"glob:" + globPattern);
		}

		@Override
		public FileVisitResult visitFile(Path filePath,
				BasicFileAttributes basicFileAttrib) {
			if (pathMatcher.matches(filePath.getFileName())) {
				matchCount++;
				System.out.println(filePath);
			}
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path directoryPath,
				BasicFileAttributes basicFileAttrib) {
			if (pathMatcher.matches(directoryPath.getFileName())) {
				matchCount++;
				System.out.println(directoryPath);
			}
			return FileVisitResult.CONTINUE;
		}

		public int getMatchCount() {
			return matchCount;
		}
	}

	public static void main(String[] args) throws IOException {

		Path rootPath = Paths.get(".");
		String globPattern = "*.txt";

		SearchFileVisitor searchFileVisitor = new SearchFileVisitor(globPattern);
		Files.walkFileTree(rootPath, searchFileVisitor);
		System.out.println("Match Count: " + searchFileVisitor.getMatchCount());
	}
}