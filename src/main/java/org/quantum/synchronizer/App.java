package org.quantum.synchronizer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.quantum.synchronizer.dao.StaffDao;
import org.quantum.synchronizer.entity.Action;
import org.quantum.synchronizer.exception.FileAccessException;
import org.quantum.synchronizer.parser.XmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        if (!isArgumentsValid(args)) {
            throw new IllegalArgumentException("Arguments exception");
        }
        var action = Action.valueOf(args[0].toUpperCase());
        var file = new File(args[1]);

        var staffDao = StaffDao.getInstance();
        var xmlParser = XmlParser.getInstance();
        switch (action) {
            case DOWNLOAD -> writeToXml(file, xmlParser, staffDao);
            case UPLOAD -> writeToDb(file, xmlParser, staffDao);
        }
    }

    private static void writeToDb(File xmlFile, XmlParser xmlParser, StaffDao staffDao) {
        if (!isReadable(xmlFile)) {
            var message = "File %s not exists or is not readable".formatted(xmlFile.getName());
            log.info(message);
            throw new FileAccessException(message);
        }
        log.info("synchronize database content with {}", xmlFile);
        staffDao.insert(xmlParser.parseFrom(xmlFile));
    }

    private static void writeToXml(File xmlFile, XmlParser xmlParser, StaffDao staffDao) {
        if (!isWritable(xmlFile)) {
            var message = "File %s is not writable".formatted(xmlFile.getName());
            log.error(message);
            throw new FileAccessException(message);
        }
        log.info("extract database to {}", xmlFile);
        xmlParser.parseTo(xmlFile, staffDao.findAll());
    }

    private static boolean isWritable(File file) {
        try {
            file.createNewFile();
            return file.canWrite();
        } catch (IOException e) {
        }
        return false;
    }

    private static boolean isReadable(File file) {
        return file.exists() && file.canRead();
    }

    private static boolean isArgumentsValid(String[] args) {
        if (args.length < 2) {
            log.error("too few arguments: actual {}, required 2", args.length);
            return false;
        }
        Optional<Action> action = Arrays.stream(Action.values()).filter(a -> a.name().equalsIgnoreCase(args[0]))
            .findAny();
        if (action.isEmpty()) {
            log.error("missed argument: action");
            return false;
        }
        return true;
    }

}
