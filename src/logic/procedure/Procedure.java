package logic.procedure;

import com.google.gson.Gson;
import logic.util.Log;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static logic.procedure.Instruction.*;

/**
 * This class serves as a wrapper class for a instructions of Instructions
 *
 * (A procedure is not allowed to call itself)
 * @author orcid.org/0009-0008-3023-1228 (inf104662)
 */
public class Procedure implements Queue<Instruction> {

    /**
     * id of procedure.
     * @apiNote optional. used to keep procedure context internally
     */
    private final int id;
    /**
     * List of instructions
     * @implNote A procedure is not allowed to call itself
     */
    private final List<Instruction> instructions;

    /**
     * Create a procedure from a collection of Instructions
     *
     * @param instructions instructions of Instructions
     */
    public Procedure(int id, Collection<Instruction> instructions) {
        this.instructions = new ArrayList<>(instructions);
        this.id = id;
    }

    /**
     * Create an empty (No Instructions) Procedure
     */
    public Procedure() {
        this(-1, new ArrayList<>());
    }

    /**
     * Create a procedure from multiple Instructions
     *
     * @param instructions multiple instructions
     */
    public Procedure(int id, Instruction... instructions) {
        this(id, Arrays.stream(instructions).collect(Collectors.toList()));
    }

    /**
     * Create a procedure from multiple Instructions
     *
     * @param instructions multiple instructions
     */
    public Procedure(Instruction... instructions) {
        this(Arrays.stream(instructions).collect(Collectors.toList()));
    }

    /**
     * Create a procedure from a instructions of Instructions
     *
     * @param instructions instructions of Instructions
     */
    public Procedure(Collection<Instruction> instructions) {
        this(-1, instructions);
    }

    /**
     * Counts the occurences of the given instruction in this procedure
     * @param target instruction that will be counted
     * @return count of target instructions in this procedure
     */
    public int countOccurances(Instruction target) {
        int counter = 0;
        for (Instruction instruction : instructions) {
            if (instruction == target) {
                counter++;
            }
        }

        return counter;
    }

    /**
     * Get id of Procedure
     * @return 0-2 (0 = Parent; 1-2 = Child)
     */
    public int getId() {
        return id;
    }

    /**
     * Optimize raw list of instructions to use sub-procedures.
     * This method is using the standard procedure constraints 12, 8, 8.
     *
     * @param instructions raw list of instructions
     * @return List of 3 Procedures
     */
    public static List<Procedure> optimize(List<Instruction> instructions) {
        if (instructions.contains(EXECUTE_P1) || instructions.contains(EXECUTE_P2)) {
            // instruction list with recursion calls leads to undefined behaviour.
            throw new IllegalArgumentException("Recursion calls not allowed.");
        }
        return optimize(instructions, 12, 8, 8);
    }

    /**
     * Optimize raw list of instructions to use sub-procedures.
     *
     * @param instructions raw list of instructions
     * @param procedureConstraints a procedure constraint is simply the max number of instruction. Each
     *                             number indicates a procedure.
     * @return List of procedures
     */
    public static List<Procedure> optimize(List<Instruction> instructions, int... procedureConstraints) {
        int maxProcedureCount = procedureConstraints.length;
        List<Procedure> result = new ArrayList<>(maxProcedureCount);

        int maxInstructionCount = 0;
        for (int constraint : procedureConstraints) {
            maxInstructionCount += constraint;
        }

        // if raw list of instruction
        if (instructions.size() <= maxProcedureCount) {
            // if there are less total instructions than procedure fields, we can simply divide instructions
            // on procedures
            Queue<Instruction> instructionQueue = new LinkedList<>(instructions);

            for (int i = 0; i < maxProcedureCount && !instructionQueue.isEmpty(); i++) {
                var p = new Procedure();
                for (int j = 0; j < procedureConstraints[i] && !instructionQueue.isEmpty(); j++) {
                    p.add(instructionQueue.poll());
                }
                result.add(p);
            }
        } else {
            // using enhanced optimize
            result.addAll(enhancedOptimize(instructions, 12, 8, 8));
        }

        return result;
    }

    /**
     * This method does a lot of stuff to optimize the raw list of instructions into the smallest
     * amount of instructions
     *
     * @param instructions raw list of instruction, excluding recursive calls
     * @param procedureConstraints max instruction count in procedure, each number indicates another procedure.
     * @return List of optimized Procedures
     */
    private static List<Procedure> enhancedOptimize(List<Instruction> instructions, int... procedureConstraints) {
        instructions = new ArrayList<>(instructions);
        var finalProcedures = new ArrayList<Procedure>(procedureConstraints.length);
        Log.debug("Trying to optimize: ");
        Log.debug(instructions);

        // iterating through procedure constraints
        for (int i = 1; i < procedureConstraints.length; i++) {
            var repSeqs = searchRepeatingSequences(instructions);

            // filtering out too small optimizations
            var seqs = new ArrayList<>(repSeqs.entrySet().stream()
                    .filter(e -> e.getValue().size() > 0)
                    .filter(e -> e.getKey().size() > 1)
                    .toList());

            // if any seqs left
            if (seqs.size() > 0) {

                // sorting seqs by diffrence between unoptimized and optimized amount of instructions
                seqs.sort(Comparator.comparingInt(value -> {
                    var procSize = value.getKey().size();
                    var ocs = value.getValue().size();
                    return procSize * ocs - ocs;
                }));

                Map.Entry<List<Instruction>, List<Integer>> subProc = null;
                boolean procWasFound = false;
                int pos = seqs.size() - 1;

                // selecting an optimized procedure, not violating constraints
                while (!procWasFound && pos >= 0) {
                    subProc = seqs.get(pos--);
                    if (subProc.getKey().size() <= procedureConstraints[i]) {
                        procWasFound = true;
                    } else {
                        Log.warning("Suggested Procedure(%2d) too big for Procedure Constraints[%d]: %d | %s\n",
                                subProc.getKey().size(), i, procedureConstraints[i], subProc.getKey());
                    }
                }
                assert subProc != null;

                var sequenceOccurrences = subProc.getValue();
                var procedure = new Procedure(subProc.getKey());

                Collections.reverse(sequenceOccurrences);
                finalProcedures.add(procedure);

                // removing instructions with optimized recursion call
                for (Integer occ : sequenceOccurrences) {
                    for (int j = 0; j < procedure.size(); j++) {
                        instructions.remove(occ.intValue());
                    }
                    instructions.add(occ, Instruction.getRecursive(i));
                }
            }
        }

        // add root procedure
        finalProcedures.add(0, new Procedure(0, instructions));
        Log.debug("Root Procedure: %s\n", finalProcedures.get(0));
        if (finalProcedures.get(0).size() > procedureConstraints[0]) Log.warning("Root proc to big");

        return finalProcedures;
    }

    /**
     * Searches for repeating sequences in instructions list and returns summary of sequences and start index
     * of occurrences in list
     *
     * @param instructions list of instructions
     * @return summary of occurrences and sequences
     */
    private static Map<List<Instruction>, List<Integer>> searchRepeatingSequences(List<Instruction> instructions) {
        Map<List<Instruction>, List<Integer>> sequences = new HashMap<>();
        var outerSequenceQueue = new LinkedList<>(instructions);

        // outer loop, removes from front (sequencer)
        while (!outerSequenceQueue.isEmpty()) {
            var innerSequenceQueue = new LinkedList<>(outerSequenceQueue);
            Map<List<Instruction>, List<Integer>> subSeqs = new HashMap<>();

            // inner loop, removes from end (sequencer)
            do {
                var first = innerSequenceQueue.peek();

                // comparing sequence with instructions list
                for (int i = 0; i < instructions.size() && i + innerSequenceQueue.size() < instructions.size(); i++) {
                    // if first in sequence == instructions[i]
                    if (instructions.get(i) == first) {
                        // sublist from instructions with size of sequence
                        var sub = instructions.subList(i, i + innerSequenceQueue.size());

                        // if sublist and sequence matches -> occurrence found
                        if (sub.equals(innerSequenceQueue)) {
                            // if sequence already stored, add occurrence only
                            if (subSeqs.containsKey(sub)) {
                                subSeqs.get(sub).add(i);
                            } else {
                                var os = new ArrayList<Integer>();
                                os.add(i);
                                subSeqs.put(sub, os);
                            }

                            // increase i to end of compared sublist
                            i = i - 1 + sub.size();
                        }
                    }
                }

                if (!innerSequenceQueue.isEmpty()) innerSequenceQueue.removeLast();
            } while (!innerSequenceQueue.isEmpty());

            // add found sequences to result map
            for (var e : subSeqs.entrySet()) {
                if (!sequences.containsKey(e.getKey())) {
                    sequences.put(e.getKey(), e.getValue());
                }
            }

            outerSequenceQueue.remove();
        }

        return sequences;
    }

    /**
     * Verifies that procedures can be executed
     * @param root root procedure
     * @param p1 first sub procedure
     * @param p2 second sub procedure
     * @return true, if procedures are 'correct'
     */
    public static boolean verify(Procedure root, Procedure p1, Procedure p2) {
        if (!containIllegalRecursion(p1, p2)) {
            int exitCount = root.countOccurances(EXIT);
            exitCount += p1.countOccurances(EXIT);
            exitCount += p2.countOccurances(EXIT);

            return exitCount == 1;
        } else {
            return false;
        }
    }

    /**
     * Checks if the two given child procedures call each other in a way that's illegal;
     * @param p1 child procedure 1
     * @param p2 child procedure 2
     * @return true, if illegal recursion was found
     */
    public static boolean containIllegalRecursion(Procedure p1, Procedure p2) {
        return (p1.contains(Instruction.EXECUTE_P1) || p2.contains(Instruction.EXECUTE_P2))
                || (p1.contains(Instruction.EXECUTE_P2) && p2.contains(Instruction.EXECUTE_P1))
                || (p2.contains(Instruction.EXECUTE_P1) && p1.contains(Instruction.EXECUTE_P2));
    }



    /*
    All methods from here on out are just Queue implementation that wrap around the instructions list.
     */

    @Override
    public int size() {
        return instructions.size();
    }

    @Override
    public boolean isEmpty() {
        return instructions.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return instructions.contains(o);
    }

    @Override
    public Iterator<Instruction> iterator() {
        return instructions.iterator();
    }

    @Override
    public Object[] toArray() {
        return instructions.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return instructions.toArray(a);
    }

    @Override
    public boolean add(Instruction instruction) {
        return instructions.add(instruction);
    }

    @Override
    public boolean offer(Instruction instruction) {
        return instructions.add(instruction);
    }

    @Override
    public Instruction remove() {
        if (instructions.isEmpty()) throw new NoSuchElementException("procedure is empty");
        return instructions.remove(0);
    }

    @Override
    public Instruction poll() {
        if (instructions.isEmpty()) return null;
        return instructions.remove(0);
    }

    @Override
    public Instruction element() {
        if (instructions.isEmpty()) throw new NoSuchElementException("procedure is empty");
        return instructions.get(0);
    }

    @Override
    public Instruction peek() {
        if (instructions.isEmpty()) return null;
        return instructions.get(0);
    }

    @Override
    public boolean remove(Object o) {
        return instructions.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return instructions.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Instruction> c) {
        return instructions.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return instructions.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return instructions.retainAll(c);
    }

    @Override
    public void clear() {
        instructions.clear();
    }

    @Override
    public void forEach(Consumer<? super Instruction> action) {
        instructions.forEach(action);
    }

    @Override
    public Spliterator<Instruction> spliterator() {
        return instructions.spliterator();
    }

    @Override
    public String toString() {
        return "Procedure" + new Gson().toJson(this.instructions);
    }

    @Override
    public Procedure clone() {
        return new Procedure(instructions);
    }


}
