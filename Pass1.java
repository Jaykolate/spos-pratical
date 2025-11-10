import java.io.*;
import java.util.*;

public class Pass1 {
    public static void main(String[] args) throws Exception {
        BufferedReader input = new BufferedReader(new FileReader("input.txt"));
        BufferedWriter mntOut = new BufferedWriter(new FileWriter("mnt.txt"));
        BufferedWriter mdtOut = new BufferedWriter(new FileWriter("mdt.txt"));
        BufferedWriter alaOut = new BufferedWriter(new FileWriter("ala.txt"));
        BufferedWriter interOut = new BufferedWriter(new FileWriter("intermediate.txt"));

        Map<String, Integer> MNT = new LinkedHashMap<>();
        Map<String, List<String>> ALA = new LinkedHashMap<>();
        List<String> MDT = new ArrayList<>();

        int mdtIndex = 1; // MDT index starts at 1
        String line;

        boolean inMacro = false;
        String macroName = "";

        while ((line = input.readLine()) != null) {
            line = line.trim();
            if (line.equals("")) continue;

            if (line.equalsIgnoreCase("MACRO")) {
                inMacro = true;
                continue;
            }

            if (inMacro) {
                // Macro header line
                String[] parts = line.split("\\s+", 2);
                macroName = parts[0];
                List<String> argsList = new ArrayList<>();

                if (parts.length > 1) {
                    String[] argsArr = parts[1].split(",");
                    for (String arg : argsArr) argsList.add(arg.trim());
                }

                ALA.put(macroName, argsList);
                MNT.put(macroName, mdtIndex);

                // Replace &arg with #1, #2 etc in macro body
                while ((line = input.readLine()) != null) {
                    line = line.trim();
                    if (line.equalsIgnoreCase("MEND")) {
                        MDT.add(mdtIndex + " MEND");
                        mdtIndex++;
                        break;
                    }

                    for (int i = 0; i < argsList.size(); i++) {
                        String arg = argsList.get(i);
                        line = line.replace(arg, "#" + (i + 1));
                    }

                    MDT.add(mdtIndex + " " + line);
                    mdtIndex++;
                }

                inMacro = false; // macro ended
                macroName = "";
                continue;
            }

            // Normal (non-macro) line
            interOut.write(line + "\n");
        }

        // Write MNT
        // ----- Write MNT -----
mntOut.write("MNT Table\n");
mntOut.write("MacroName\tMDTIndex\n");
for (var e : MNT.entrySet())
    mntOut.write(e.getKey() + "\t\t" + e.getValue() + "\n");

        // Write MDT
        mdtOut.write("Index\tInstruction\n");
        mdtOut.write("----------------------------\n");
        for (String s : MDT) mdtOut.write(s + "\n");

        // Write ALA
        alaOut.write("ALA Table\n");
alaOut.write("MacroName\tArguments\n");
for (var e : ALA.entrySet())
    alaOut.write(e.getKey() + "\t\t" + String.join(", ", e.getValue()) + "\n");

        // Close files
        input.close();
        mntOut.close();
        mdtOut.close();
        alaOut.close();
        interOut.close();

        System.out.println("PASS 1 Completed ✅");
        System.out.println("Generated: mnt.txt, mdt.txt, ala.txt, intermediate.txt");
    }
}
