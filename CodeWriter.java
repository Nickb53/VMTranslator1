import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * CodeWriter.java is responsible for writing the translations to the output .asm file
 */
public class CodeWriter {
    //instance variable
    private PrintWriter outputFile;
    private int count;
    private String assembly;

    //Static strings used for POP and PUSH to make things a bit cleaner
    private static final String POP =
            "@R13\n" +
                    "M=D\n" +
                    "@SP\n" +
                    "AM=M-1\n" +
                    "D=M\n" +
                    "@R13\n" +
                    "A=M\n" +
                    "M=D\n";
    private static final String PUSH =
            "@SP\n" +
                    "A=M\n" +
                    "M=D\n" +
                    "@SP\n" +
                    "M=M+1\n";


    /**
     * Construct a new CodeWriter by specifying the file name to write to
     * @param fileName the file to write to
     */
    CodeWriter(String fileName){
        try {
            outputFile = new PrintWriter(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is to keep track of pointers
     * @return the next count for a pointer
     */
    private String nextCount() {
        count += 1;
        return Integer.toString(count);
    }

    /**
     * This method takes a command and uses a switch to find its corresponding assembly code
     * The code is then written to the output file
     * @param command the command to find assembly for
     */
    public void writeArithmetic(String command){
        switch (command){
            case "add": assembly =  "@SP\n" +
                                    "AM=M-1\n" +
                                    "D=M\n" +
                                    "A=A-1\n" +
                                    "M=D+M\n";
                break;

            case "sub": assembly = "@SP\n" +
                                    "AM=M-1\n" +
                                    "D=M\n" +
                                    "A=A-1\n" +
                                    "M=M-D\n";
                break;

            case "neg": assembly =  "@SP\n" +
                                    "A=M-1\n" +
                                    "M=-M\n";
                break;

            case "eq":      String count1 = nextCount();
                            assembly =   "@SP\n" +
                                    "AM=M-1\n" +
                                    "D=M\n" +
                                    "A=A-1\n" +
                                    "D=M-D\n" +
                                    "@EQ.true." + count1 + "\n" +
                                    "D;JEQ\n" +
                                    "@SP\n" +
                                    "A=M-1\n" +
                                    "M=0\n" +
                                    "@EQ.after." + count1 + "\n" +
                                    "0;JMP\n" +
                                    "(EQ.true." + count1 + ")\n" +
                                    "@SP\n" +
                                    "A=M-1\n" +
                                    "M=-1\n" +
                                    "(EQ.after." + count1 + ")\n";
                break;

            case "gt":      String count2 = nextCount();
                            assembly = "@SP\n" +
                                    "AM=M-1\n" +
                                    "D=M\n" +
                                    "A=A-1\n" +
                                    "D=M-D\n" +
                                    "@GT.true." + count2 + "\n" +
                                    "\nD;JGT\n" +
                                    "@SP\n" +
                                    "A=M-1\n" +
                                    "M=0\n" +
                                    "@GT.after." + count2 + "\n" +
                                    "0;JMP\n" +
                                    "(GT.true." + count2 + ")\n" +
                                    "@SP\n" +
                                    "A=M-1\n" +
                                    "M=-1\n" +
                                    "(GT.after." + count2 + ")\n";
                break;

            case "lt":      String count3 = nextCount();
                            assembly =  "@SP\n" +
                                    "AM=M-1\n" +
                                    "D=M\n" +
                                    "A=A-1\n" +
                                    "D=M-D\n" +
                                    "@LT.true." + count3 + "\n" +
                                    "D;JLT\n" +
                                    "@SP\n" +
                                    "A=M-1\n" +
                                    "M=0\n" +
                                    "@LT.after." + count3 + "\n" +
                                    "0;JMP\n" +
                                    "(LT.true." + count3 + ")\n" +
                                    "@SP\n" +
                                    "A=M-1\n" +
                                    "M=-1\n" +
                                    "(LT.after." + count3 + ")\n";
                break;

            case "and": assembly =  "@SP\n" +
                                    "AM=M-1\n" +
                                    "D=M\n" +
                                    "A=A-1\n" +
                                    "M=D&M\n";
                break;

            case "or": assembly =   "@SP\n" +
                                    "AM=M-1\n" +
                                    "D=M\n" +
                                    "A=A-1\n" +
                                    "M=D|M\n";
                break;

            case "not": assembly =  "@SP\n" +
                                    "A=M-1\n" +
                                    "M=!M\n";
                break;
            default: assembly= "Command Not Recognized";
        }
        //System.out.println("COMMAND:: "+command);
        outputFile.println(assembly);
    }


    /**
     * This command is responsible for finding the corresponding assembly code for pop and push commands
     * @param commandType Push or Pop for right now
     * @param segment which memory segment we are dealing with
     * @param index where to put in memory
     */
    public void writePushPop(CommandType commandType, String segment, int index) {

        //for pop commands
        if (commandType == CommandType.C_POP) {
            switch (segment) {
                case "local":
                    assembly =  "@LCL\n" +
                                "D=M\n" +
                                "@" + index + "\n" +
                                "D=D+A\n" +
                                 POP;
                    break;

                case "argument":
                    assembly =  "@ARG\n" +
                                "D=M\n" +
                                "@" + index + "\n" +
                                "D=D+A\n" +
                                POP;
                    break;

                case "this":
                    assembly =  "@THIS\n" +
                                "D=M\n" +
                                "@" + index + "\n" +
                                "D=D+A\n" +
                                POP;
                    break;

                case "that":
                    assembly =  "@THAT\n" +
                                "D=M\n" +
                                "@" + index + "\n" +
                                "D=D+A\n" +
                                POP;
                    break;

                case "pointer":
                    if (index == 0) assembly =  "@THIS\n" +
                                                "D=A\n" +
                                                POP;
                    else assembly = "@THAT\n" +
                                    "D=A\n" +
                                    POP;
                    break;

                case "static":
                    assembly =   "@" + "FileNameHere" + "." + index + "\n" +
                                 "D=A\n" +
                                  POP;
                    break;

                case "temp":
                    assembly =   "@R5\n" +
                                 "D=A\n" +
                                 "@" + index + "\n" +
                                 "D=D+A\n" +
                                  POP;
                    break;

                default:
                    assembly = "Command not Recognized";
            }
            outputFile.println(assembly);
        }

        //for push commands
        if (commandType == CommandType.C_PUSH) {
            System.out.println("SEGMENT: " + segment);
            switch (segment) {
                case "local":
                        assembly =  "@LCL\n" +
                                    "D=M\n" +
                                    "@" + index + "\n" +
                                    "A=D+A\n" +
                                    "D=M\n" +
                                    PUSH;
                    break;

                case "argument":
                    assembly =  "@ARG\n" +
                                "D=M\n" +
                                "@" + index + "\n" +
                                "A=D+A\n" +
                                "D=M\n" +
                                PUSH;
                    break;

                case "this":
                    assembly =  "@THIS\n" +
                                "D=M\n" +
                                "@" + index + "\n" +
                                "A=D+A\n" +
                                "D=M\n" +
                                PUSH;
                    break;

                case "that":
                    assembly =  "@THAT\n" +
                                "D=M\n" +
                                "@" + index + "\n" +
                                "A=D+A\n" +
                                "D=M\n" +
                                PUSH;
                    break;

                case "pointer":
                        if(index==0) assembly =  "@THIS\n" +
                                                 "D=M\n" +
                                                  PUSH;

                        else assembly =          "@THAT\n" +
                                                 "D=M\n" +
                                                  PUSH;
                    break;

                case "constant":
                    assembly =  "@" + index + "\n" +
                                "D=A\n" +
                                 PUSH;
                    break;

                case "static":
                    assembly =  "@" + "FileNameHere" + "." + index + "\n" +
                                "D=M\n" +
                                PUSH;
                    break;

                case "temp":
                    assembly =  "@R5\n" +
                                "D=A\n" +
                                "@" + index + "\n" +
                                "A=D+A\n" +
                                "D=M\n" +
                                PUSH;
                    break;

                default:
                    assembly = "Command not Recognized in Push";
            }
            outputFile.println(assembly);
        }


    }
    /**
     * Close the printwrite after it is done being used
     */
    public void close(){
        outputFile.close();
    }
}
