import java.io.*;
import java.util.*;
public class pass2 {
public static void main(String[] args) throws Exception {
BufferedReader interIn = new BufferedReader(new FileReader("intermediate.txt"));

BufferedReader mntIn = new BufferedReader(new FileReader("mnt.txt"));
BufferedReader mdtIn = new BufferedReader(new FileReader("mdt.txt"));
BufferedReader alaIn = new BufferedReader(new FileReader("ala.txt"));
BufferedWriter finalOut = new BufferedWriter(new FileWriter("expanded.asm"));
// Load MNT
Map<String, Integer> MNT = new HashMap<>();
String line;
mntIn.readLine(); // skip header
mntIn.readLine(); // skip line of dashes
while ((line = mntIn.readLine()) != null && !line.trim().equals("")) {
String[] parts = line.trim().split("\\s+");
MNT.put(parts[0], Integer.parseInt(parts[1]));
}
// Load MDT
Map<Integer, String> MDT = new HashMap<>();
mdtIn.readLine(); // skip header
mdtIn.readLine(); // skip dashes
while ((line = mdtIn.readLine()) != null && !line.trim().equals("")) {
String[] parts = line.trim().split("\\s+", 2);
int idx = Integer.parseInt(parts[0]);
String instr = parts[1];
MDT.put(idx, instr);
}
// Load ALA
Map<String, List<String>> ALA = new HashMap<>();
alaIn.readLine(); // skip header
alaIn.readLine(); // skip dashes
while ((line = alaIn.readLine()) != null && !line.trim().equals("")) {
String[] parts = line.trim().split("\\s+", 2);
String macroName = parts[0];
List<String> formalArgs = Arrays.asList(parts[1].split("\\s*,\\s*")); //fixed here
ALA.put(macroName, formalArgs); //use the renamed variable
}
// Process Intermediate
while ((line = interIn.readLine()) != null) {
String trimmed = line.trim();
if (trimmed.equals("")) continue;
String[] parts = trimmed.split("\\s+", 2);
String instr = parts[0];
if (MNT.containsKey(instr)) {
// Macro call found
int mdtIndex = MNT.get(instr);
List<String> formalParams = ALA.get(instr);
String[] actualParams = new String[0];
if (parts.length > 1) {
actualParams = parts[1].split("\\s*,\\s*");

}
// Create mapping: #1 -> actual param 1, #2 -> actual param 2
Map<String, String> paramMap = new HashMap<>();
for (int i = 0; i < formalParams.size(); i++) {
if (i < actualParams.length)
paramMap.put("#" + (i + 1), actualParams[i]);
else
paramMap.put("#" + (i + 1), "");
}
// Expand MDT lines until MEND
int idx = mdtIndex;
while (!MDT.get(idx).equals("MEND")) {
String expanded = MDT.get(idx);
// replace #1, #2 with actual parameters
for (Map.Entry<String, String> e : paramMap.entrySet()) {
expanded = expanded.replace(e.getKey(), e.getValue());
}
finalOut.write(expanded + "\n");
idx++;
}
} else {
// Normal instruction
finalOut.write(line + "\n");
}
}
interIn.close();
mntIn.close();
mdtIn.close();
alaIn.close();
finalOut.close();
System.out.println("PASS 2 Completed (Generated expanded.asm)");
}
}