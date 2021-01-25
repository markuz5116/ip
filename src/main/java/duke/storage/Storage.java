package duke.storage;

import duke.exceptions.DukeCreateDirectoryException;
import duke.exceptions.DukeCreateFileException;
import duke.exceptions.DukeSaveFileException;
import duke.tasks.Task;
import duke.exceptions.DukeCorruptedStorageException;
import duke.ui.Ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Represents the Storage file that is used to store and update the save file.
 */
public class Storage {
    private static final String DATA_DIR = new File("data").getAbsolutePath();
    private final File saveFile = new File(DATA_DIR + "/save.txt");
    private static Storage INSTANCE;
    private static final Ui ui = new Ui();

    private Storage() {
        createDirectory();
    }

    /**
     * Return a new Storage or an existing one.
     * @return a new or existing Storage class.
     */
    public static Storage getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Storage();
        }
        return INSTANCE;
    }

    /**
     * Update the save file in the hardware with the new TaskList.
     * @param tasks The TaskList used to update the save file.
     */
    public void update(ArrayList<Task> tasks) {
        try {
            FileWriter fw = new FileWriter(saveFile);
            fw.write(StorageEncoder.encodeTasks(tasks));
            fw.close();
        } catch (IOException e) {
            ui.printErrorMsg(new DukeSaveFileException());
        }
    }

    /**
     * Return an ArrayList of Task from the save file.
     * @return an ArrayList of Task from the save file.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            if (!saveFile.createNewFile()) {
                Scanner sc = new Scanner(saveFile);
                ArrayList<String> inputs = new ArrayList<>();
                while (sc.hasNext()) {
                    inputs.add(sc.nextLine());
                }
                tasks = StorageDecoder.decodeSave(inputs);
            }
        } catch (IOException e) {
            ui.printErrorMsg(new DukeCreateFileException());
        } catch (DukeCorruptedStorageException e) {
            ui.printErrorMsg(e);
        }
        return tasks;
    }

    /**
     * Create a data directory from source unless it already exists.
     */
    private void createDirectory() {
        Path dataPath = Paths.get(DATA_DIR);
        try {
            Files.createDirectories(dataPath);
        } catch (IOException e) {
            ui.printErrorMsg(new DukeCreateDirectoryException(DATA_DIR));
        }
    }
}