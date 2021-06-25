import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Q1a {
	public static void main(String[] args) {
		File file = new File("Maze.txt");
		int count = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				for (int i = 0; i < line.length(); ++i) {
					char c = line.charAt(i);
					if (c != '1')
						++count;
				}
			}
		} catch (IOException e) {
    		e.printStackTrace();
		}
		System.out.println(count);
	}

}
