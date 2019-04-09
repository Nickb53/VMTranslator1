import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Parser.java opens a specified file, and parses the VM code into is elements
 */
public class Parser {
    //instance variables for components of the VM code
    private String command;
    private String arg1;
    private int arg2;
    private CommandType commandType;
    private String rawLine;
    private String cleanLine;

    private Scanner inputFile;

    //arraylist to store the various commands for arithmetic instructions
    private ArrayList<String> arithmeticCommands = new ArrayList<>();

    /**
     * Constructor creates a new Parser using a file name as an argument
     * If the file is not found an exception is thrown and the program stops
     * @param fileName the name of the file to parse
     */
    Parser (String fileName){
        //"add","sub","neg","eq","gt","lt","and","or","not"
        arithmeticCommands.add("add");
        arithmeticCommands.add("sub");
        arithmeticCommands.add("neg");
        arithmeticCommands.add("eq");
        arithmeticCommands.add("gt");
        arithmeticCommands.add("lt");
        arithmeticCommands.add("and");
        arithmeticCommands.add("or");
        arithmeticCommands.add("not");
        try {
            inputFile = new Scanner(new File(fileName));
        }catch (FileNotFoundException e){
            System.err.println("Error opening file " + fileName);
            System.exit(0);
        }
    }

    /**
     * Are there more commands to parse?
     * @return whether or not there are more commands
     */
    public boolean hasMoreCommands(){
        return inputFile.hasNextLine();
    }


    /**
     * advance cleans the line of code in the file,
     * and then figures out which type of command it is
     */
    public void advance(){
        //get the next line of code and remove any comments
        rawLine = inputFile.nextLine();
        cleanLine = getCleanLine(rawLine);

        //if the line is not empty, assign it a command type
        if(!(cleanLine.length() == 0)){
            commandType();
            if(commandType != CommandType.C_RETURN){
                arg1();
            }
            if(commandType == CommandType.C_PUSH || commandType == CommandType.C_POP){
                arg2();
            }
            //System.out.println("CommandType: " + commandType + " arg1: " + arg1 + " arg2: " + arg2);
        }
    }

    /**
     * commandType is used to figure out which type of command the line of code is
     */
    private void commandType(){
        //if there is no whitespace, the line must be an arithmetic command
        //so the command is the only word in the string
        if(!cleanLine.contains(" ")) command = cleanLine;

        //otherwise, take the first word of the string for the command
        else {
            command = cleanLine.substring(0, cleanLine.indexOf(" "));
        }

        //make sure the command is an actual valid command
        if(arithmeticCommands.contains(command)){
            setCommandType(CommandType.C_ARITHMETIC);
        }

        //set the command type based on the first word in the string
        if(command.equals("pop")) setCommandType(CommandType.C_POP);
        if(command.equals("push")) setCommandType(CommandType.C_PUSH);
    }

    /**
     * This method is used to get the first argument in a line
     * If it is an arithmetic command, then arg1 is just the command
     */
    private void arg1(){
        if(commandType == CommandType.C_ARITHMETIC) arg1 = command;

        //if not arithmetic, get the second word in the string
        else{
           arg1 = cleanLine.substring(cleanLine.indexOf(" "), cleanLine.lastIndexOf(" ")).trim();
        }
    }

    /**
     * this method returns the second argument of a line
     * only used for memory commands right now
     */
    private void arg2(){
        if(commandType == CommandType.C_PUSH || commandType == CommandType.C_POP)
            arg2 =Integer.parseInt(cleanLine.substring(cleanLine.lastIndexOf(" ") + 1));
    }



    /**
     * Cleans a line of code read from the file
     * Removes any comments
     * @param line the line to clean
     * @return the cleaned line with no comments
     */
    private String getCleanLine(String line){
        if(line.length() == 0) return "";
        cleanLine = rawLine;
        if(cleanLine.indexOf("//") == 0) return "";
        if(cleanLine.contains("//")){
            cleanLine = cleanLine.substring(0,cleanLine.indexOf("//"));
        }

        return cleanLine;
    }

    //Getters and Setters for Instance Variables//

    public String getArg1() {
        return arg1;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public int getArg2() {
        return arg2;
    }

    public void setArg2(int arg2) {
        this.arg2 = arg2;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }
}
