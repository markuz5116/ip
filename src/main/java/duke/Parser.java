package duke;

import duke.commands.AddCommandType;
import duke.commands.CommandType;
import duke.exceptions.DukeCorruptedStorageException;
import duke.exceptions.DukeNoDescriptionException;
import duke.exceptions.DukeUnknownArgumentsException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a Parser class to parse input to specified outputs based on inputs.
 */
public class Parser {
    public static final String DEADLINE_COMMAND = "deadline";
    private static final String DONE_COMMAND = "done";
    public static final String LIST_COMMAND = "list";
    public static final String DELETE_COMMAND = "delete";
    public static final String TODO_COMMAND = "todo";
    public static final String EVENT_COMMAND = "event";
    public static final int TODO_MIN_ARGUMENTS = 2;
    public static final int TODO_DESCRIPTION = 5;
    public static final int ENCODE_DATE_PARAM = 3;
    public static final int DESCRIPTION_PARAM = 0;
    public static final String DATE_SEPARATOR = "/";
    public static final int DATE_INPUT_MIN_ARGUMENTS = 4;
    public static final int INDEX_PADDING = 1;
    public static final String TODO_COMMAND_TYPE = "T";
    public static final String DEADLINE_COMMAND_TYPE = "D";
    public static final String EVENT_COMMAND_TYPE = "E";
    public static final String DATA_SEPARATOR = " \\| ";
    public static final int TODO_COMMAND_TYPE_PARAM = 0;
    public static final int TODO_DESCRIPTION_PARAM = 2;
    public static final int IS_DONE_PARAM = 1;
    public static final String DONE_ENCODING = "1";
    public static final String NOT_DONE_ENCODING = "0";
    public static final int DATE_PARAM = 1;
    public static final int DATE_POSTFIX = 3;

    /**
     * Return a string representation based on LocalDate.
     * @param date date used to create the string representation.
     * @return the date with "MMM dd yyyy".
     */
    public static String localDateToString(LocalDate date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd yyyy");
        return date.format(dtf);
    }

    /**
     * Return CommandType based on input.
     * @param input user input used to return a CommandType.
     * @return CommandType.
     */
    static CommandType parseCommand(String input) {
        if (input.startsWith(DONE_COMMAND)) {
            return CommandType.DONE;
        } else if (input.startsWith(LIST_COMMAND)) {
            return CommandType.LIST;
        } else if (input.startsWith(DELETE_COMMAND)) {
            return CommandType.DELETE;
        } else {
            return CommandType.ADD;
        }
    }

    /**
     * Return index based on string input.
     * @param input input used to get index.
     * @param i when the string representation of the index starts.
     * @return index based on the input.
     */
    public static int stringToIndex(String input, int i) {
        return Integer.parseInt(input.substring(i)) - INDEX_PADDING;
    }

    /**
     * Return AddCommandType based on input: TODO, DEADLINE, EVENT.
     * @param input user input used to get AddCommandType.
     * @return AddCommandType based on input.
     * @throws DukeUnknownArgumentsException when the input contains an unknown command.
     */
    public static AddCommandType inputToAddCommand(String input) throws DukeUnknownArgumentsException {
        if (input.startsWith(TODO_COMMAND)) {
            return AddCommandType.TODO;
        } else if (input.startsWith(DEADLINE_COMMAND)) {
            return AddCommandType.DEADLINE;
        } else if (input.startsWith(EVENT_COMMAND)) {
            return AddCommandType.EVENT;
        } else {
            throw new DukeUnknownArgumentsException();
        }
    }

    /**
     * Return description of the Todo based on input.
     * @param input user input to get description of Todo.
     * @return description of Todo based on input.
     * @throws DukeNoDescriptionException when the description is empty.
     */
    public static String parseTodoInput(String input) throws DukeNoDescriptionException {
        if (input.split(" ").length < TODO_MIN_ARGUMENTS) {
            throw new DukeNoDescriptionException(TODO_COMMAND);
        } else {
            return input.substring(TODO_DESCRIPTION).trim();
        }
    }

    /**
     * Return LocalDate based on input and AddCommandType's postfix.
     * @param input user input to get Task date representation.
     * @param command AddCommandType to be used to distinguish how to get the LocalDate.
     * @return LocalDate based on the string representation of the date.
     */
    public static LocalDate obtainDate(String input, AddCommandType command) {
        input = input.substring(command.getPostfix());
        String[] inputs = input.split(DATE_SEPARATOR);
        return LocalDate.parse(inputs[DATE_PARAM].substring(DATE_POSTFIX));
    }

    /**
     * Return description of the Event and Deadline Task, depending on the command.
     * @param input user input to get the description of the Event or Deadline.
     * @param command AddCommandType used to differentiate the Event and Deadline.
     * @return description of either Event or Deadline.
     * @throws DukeNoDescriptionException when the description of the input is empty.
     */
    public static String obtainDescription(String input, AddCommandType command) throws DukeNoDescriptionException {
        if (input.split(" ").length < DATE_INPUT_MIN_ARGUMENTS) {
            throw new DukeNoDescriptionException(command.getName());
        } else {
            input = input.substring(command.getPostfix());
            String[] inputs = input.split(DATE_SEPARATOR);
            return inputs[DESCRIPTION_PARAM];
        }
    }

    /**
     * Return AddCommandType based on encoded input.
     * @param input used to get encoded representation to get the AddCommandType.
     * @return TODO if "T", DEADLINE if "D", EVENT if "E".
     * @throws DukeCorruptedStorageException when the encoded command is unknown.
     */
    public static AddCommandType parseCommandType(String input) throws DukeCorruptedStorageException {
        String[] separatedInput = input.split(DATA_SEPARATOR);
        String command = separatedInput[TODO_COMMAND_TYPE_PARAM];
        switch (command) {
        case TODO_COMMAND_TYPE:
            return AddCommandType.TODO;
        case DEADLINE_COMMAND_TYPE:
            return AddCommandType.DEADLINE;
        case EVENT_COMMAND_TYPE:
            return AddCommandType.EVENT;
        default:
            throw new DukeCorruptedStorageException();
        }
    }

    /**
     * Return description of task based on encoded input.
     * @param input encoded input from save file.
     * @return description of task.
     * @throws DukeCorruptedStorageException when the encoded input is not of the right format.
     */
    public static String obtainEncodedDescription(String input)
            throws DukeCorruptedStorageException {
        String[] separatedInput = input.split(DATA_SEPARATOR);
        if (separatedInput[TODO_DESCRIPTION_PARAM].isBlank()) {
            throw new DukeCorruptedStorageException();
        }
        return separatedInput[TODO_DESCRIPTION_PARAM];
    }

    /**
     * Return true if encoded task is marked as done, otherwise false.
     * @param input encoded task from save file.
     * @return true if encoded task is marked as done.
     * @throws DukeCorruptedStorageException when the encoded task is not of the right format.
     */
    public static boolean isEncodedTaskDone(String input) throws DukeCorruptedStorageException {
        String[] separatedInput = input.split(DATA_SEPARATOR);
        String isDone = separatedInput[IS_DONE_PARAM];
        if (isDone.equals(DONE_ENCODING)) {
            return true;
        } else if (isDone.equals(NOT_DONE_ENCODING)) {
            return false;
        } else {
            throw new DukeCorruptedStorageException();
        }
    }

    /**
     * Return LocalDate of either the Event's date or Deadline's date.
     * @param input encoded task used to get the LocalDate.
     * @return LocalDate based on input.
     * @throws DukeCorruptedStorageException when the encoded task is not of the right format.
     */
    public static LocalDate obtainEncodedDate(String input) throws DukeCorruptedStorageException {
        String[] separatedInput = input.split(DATA_SEPARATOR);
        String date = separatedInput[ENCODE_DATE_PARAM];
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new DukeCorruptedStorageException();
        }
    }
}