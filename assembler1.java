import java.io.*;
import java.util.*;
class pass1
{
static Map<String, String> IS = Map.of(
"MOVER", "01", "ADD", "02", "SUB", "03", "MOV", "04", "JMP", "05"
);
static Map<String, String> DL = Map.of(
"DC", "01", "DS", "02"
);
static Map<String, String> AD = Map.of(
"START", "01", "END", "02", "LTORG", "03"
);
static Map<String, String> REG = Map.of(
"A", "1", "B", "2", "C", "3", "D", "4"
);
static Map<String, Integer> symtab = new LinkedHashMap<>();
static List<String> littab = new ArrayList<>();
static Map<String, Integer> littabAddress = new LinkedHashMap<>();
static List<Integer> pooltab = new ArrayList<>();
static List<String> IC = new ArrayList<>();
static int LC = 0;
static int findLitIndex(String lit)
{
return littab.indexOf(lit);
}
static List<String> readSourceFile(String filename)
{
List<String> lines = new ArrayList<>();
try (BufferedReader br = new BufferedReader(new FileReader(filename)))
{
String line;
while ((line = br.readLine()) != null)
{
if (!line.trim().isEmpty())
{
lines.add(line.trim());
}
}
}
catch (IOException e)
{
System.out.println("Error reading file: " + e.getMessage());

}
return lines;
}
static void passOne(List<String> src)
{
pooltab.add(0);
for (String line : src)
{
String[] parts = line.replace(",", "").split("\\s+");
int i = 0;
if (parts[i].endsWith(":"))
{
String label = parts[i].substring(0, parts[i].length() - 1);
symtab.put(label, LC);
i++;
}
if (i >= parts.length) continue;
String op = parts[i];
// AD Directives
if (AD.containsKey(op))
{
if (op.equals("START"))
{
LC = Integer.parseInt(parts[i + 1]);
IC.add(LC + ": (AD," + AD.get(op) + ") (C," + parts[i + 1] + ")");
symtab.put("START", LC);
}
else if (op.equals("END") || op.equals("LTORG"))
{
IC.add(LC + ": (AD," + AD.get(op) + ")");
int start = pooltab.get(pooltab.size() - 1);
for (int j = start; j < littab.size(); j++)
{
String lit = littab.get(j);
littabAddress.put(lit, LC);
symtab.put("L" + j, LC); // Optional: to track as symbols
IC.add(LC + ": (DL,01) (C," + lit.substring(1) + ")");
LC++;
}
if (start != littab.size())
{
pooltab.add(littab.size());
}
}
continue;
}

// DL Directives
if (DL.containsKey(op))
{
IC.add(LC + ": (DL," + DL.get(op) + ") (C," + parts[i + 1] + ")");
LC += op.equals("DS") ? Integer.parseInt(parts[i + 1]) : 1;
continue;
}
// IS Instructions
if (IS.containsKey(op))
{
StringBuilder icLine = new StringBuilder(LC + ": (IS," + IS.get(op) + ")");
for (int j = i + 1; j < parts.length; j++)
{
String operand = parts[j];
if (REG.containsKey(operand))
{
icLine.append(" (R,").append(REG.get(operand)).append(")");
}
else if (operand.startsWith("="))
{
if (!littab.contains(operand)) littab.add(operand);
icLine.append(" (L,").append(findLitIndex(operand)).append(")");
}
else
{
// Only add symbol if not a register
if (!symtab.containsKey(operand))
symtab.put(operand, -1); // Undefined address initially
int idx = new ArrayList<>(symtab.keySet()).indexOf(operand);
icLine.append(" (S,").append(idx).append(")");
}
}
IC.add(icLine.toString());
LC++;
}
}
}
static void printTablesAndIC() throws IOException
{
try (BufferedWriter symWriter = new BufferedWriter(new FileWriter("symbtab.txt"));
BufferedWriter litWriter = new BufferedWriter(new FileWriter("littab.txt"));
BufferedWriter icWriter = new BufferedWriter(new FileWriter("intermediate.txt")))
{
symWriter.write("Index Symbol Address\n");
int idx = 0;
for (var e : symtab.entrySet())
{
symWriter.write(idx + " " + e.getKey() + " " + e.getValue() + "\n");
idx++;

}
litWriter.write("Index Literal Address\n");
for (int i = 0; i < littab.size(); i++)
{
String lit = littab.get(i);
int addr = littabAddress.getOrDefault(lit, -1);
litWriter.write(i + " " + lit + " " + addr + "\n");
}
for (String ic : IC)
{
icWriter.write(ic + "\n");
}
}
System.out.println("Pass 1 completed and files generated: symbtab.txt, littab.txt,
intermediate.txt");
}
public static void main(String[] args) throws IOException
{
List<String> source = readSourceFile("input.txt");
passOne(source);
printTablesAndIC();
}
}

==========================================================pass2.java==========================================================

import java.io.*;
import java.util.*;
public class pass2
{
public static void main(String[] args) throws IOException
{
BufferedReader br = new BufferedReader(new FileReader("intermediate.txt"));
BufferedReader symFile = new BufferedReader(new FileReader("symbtab.txt"));
BufferedReader litFile = new BufferedReader(new FileReader("littab.txt"));
Map<Integer, Integer> symtab = new HashMap<>();
Map<Integer, Integer> littab = new HashMap<>();
String line;
int index = 0;
// Reading Symbol Table (index and address)
while ((line = symFile.readLine()) != null)
{
line = line.trim();
if (line.isEmpty() || line.startsWith("Index")) continue; // skip header
String[] parts = line.split("\\s+");
if (parts.length >= 3)
{
symtab.put(index++, Integer.parseInt(parts[2]));
}
}

index = 0;
// Reading Literal Table (index and address)
while ((line = litFile.readLine()) != null)
{
line = line.trim();
if (line.isEmpty() || line.startsWith("Index")) continue; // skip header
String[] parts = line.split("\\s+");
if (parts.length >= 3)
{
littab.put(index++, Integer.parseInt(parts[2]));
}
}
while ((line = br.readLine()) != null)
{
line = line.trim();
if (line.isEmpty()) continue;
// Remove LC and extract code inside parentheses
int colonIndex = line.indexOf(':');
if(colonIndex != -1) {
line = line.substring(colonIndex + 1).trim();
}
String[] parts = line.replace("(", "").replace(")", "").split("\\s+");
if (parts[0].startsWith("AD"))
{
// Skip Assembler Directives
continue;
}
if (parts[0].startsWith("IS"))
{
String opcode = parts[0].split(",")[1];
String reg = "0";
String addr = "000";
for (int i = 1; i < parts.length; i++)
{
String[] op = parts[i].split(",");
if (op[0].equals("R"))
{
reg = op[1];
}
else if (op[0].equals("S"))
{
int symIndex = Integer.parseInt(op[1]);
int address = symtab.getOrDefault(symIndex, 0);
addr = String.format("%03d", address);
}
else if (op[0].equals("L"))

{
int litIndex = Integer.parseInt(op[1]);
int address = littab.getOrDefault(litIndex, 0);
addr = String.format("%03d", address);
}
}
System.out.println("+ " + opcode + " " + reg + " " + addr);
}
else if (parts[0].startsWith("DL"))
{
String type = parts[0].split(",")[1];
if (type.equals("01"))
{
String constant = parts[1].split(",")[1];
System.out.println("+ 00 0 " + constant);
}
else if (type.equals("02"))
{
// Usually DS directive doesn't generate machine code line, skip or print zeros
System.out.println("+ -- -- ---");
}
}
}
br.close();
symFile.close();
litFile.close();
}
}