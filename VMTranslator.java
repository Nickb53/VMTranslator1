/**
 * VMTranslator.java takes an input file of VM code and Translates it into Assembly code
 */
public class VMTranslator {
    //Constant for file names
    public static final String FILE_NAME = "StaticTest";

    public static void main(String[] args) {

        //creating a parser object and a codeWriter object for reading the file
        //and then writing the corresponding assembly code
        Parser parser = new Parser(FILE_NAME + ".vm");
        CodeWriter codeWriter = new CodeWriter(FILE_NAME + ".asm");

        //while the parser has additional commands, continue
        while(parser.hasMoreCommands()){
            parser.advance();

            //if the commandType is null, just ignore it and continue on
            //otherwise look at the command type and write the code using the appropriate method
            if(parser.getCommandType() != null) {
                System.out.println(parser.getCommandType());
                if (parser.getCommandType() == CommandType.C_ARITHMETIC) {
                    codeWriter.writeArithmetic(parser.getArg1());
                } else {
                    codeWriter.writePushPop(parser.getCommandType(), parser.getArg1(), parser.getArg2());
                }
            }
        }
        //close the writer to save the file
        codeWriter.close();


    }
}
