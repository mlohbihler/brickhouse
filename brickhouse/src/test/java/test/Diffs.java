package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.brickhouse.datatype.HMap;
import org.brickhouse.zinc.ZincReader;

public class Diffs {
    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader("data/proj.diffs"));

        String line;
        List<HMap> rows = new ArrayList<>();
        while ((line = in.readLine()) != null) {
            if (line.startsWith("#"))
                // Comment. Skip.
                continue;

            if (line.startsWith("+")) {
                HMap map = parseLine(line);
                System.out.println(map);
            }
            else if (line.startsWith("^")) {
                HMap map = parseLine(line);
                System.out.println(map);
            }
            else {
                System.out.println("Unknonwn line operation: " + line.charAt(0));
                continue;
            }
        }

        in.close();
    }

    static HMap parseLine(String line) {
        line = line.substring(2, line.length() - 1);
        ZincReader in = new ZincReader(line);
        return in.readDiff();
    }
}
