package com.xxm.dataingestionservice.utils;

import com.xxm.dataingestionservice.controller.FileManager;
import com.xxm.dataingestionservice.exception.FileWatcherException;
import com.xxm.dataingestionservice.ingestion.CustomFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class FileWatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileWatcher.class);

    @Autowired
    private FileManager fileManager;

    private CustomFile affectedFile;

    public Runnable startWatching(File targetFolder){
        LOGGER.info("Start Watching new files in folder [{}]",targetFolder);
        FileWatchService fileWatchService = new FileWatchService(targetFolder);
        return fileWatchService;
    }

    public CustomFile getAffectedFile(){
        return affectedFile;
    }

    class FileWatchService implements Runnable{

        private final File targetFolder;

        FileWatchService(File targetFolder) {
            this.targetFolder = targetFolder;
        }

        @Override
        public void run() {
            try {
                WatchService watcherService = FileSystems.getDefault().newWatchService();
                Path path = targetFolder.toPath();
                path.register(watcherService, StandardWatchEventKinds.ENTRY_CREATE);
                watchForFile(watcherService);
            } catch (IOException e) {
                throw new FileWatcherException("Error Watching target folder " + targetFolder.getName());
            } catch (InterruptedException e) {
                LOGGER.error("Thread execution interrupted");
            }
        }
        private void watchForFile(WatchService service) throws InterruptedException {
            WatchKey key;
            while((key = service.take())!=null){
                for (WatchEvent<?> watchEvent: key.pollEvents()){
                    if (watchEvent.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)){
                        LOGGER.info("Found new file [{}]",watchEvent.context().toString());
                        setAffectedFile(watchEvent.context().toString());
                        return;
                    }
                }
            }
        }

        private void setAffectedFile(String fileName){
            //affectedFile = new File(pathToFile);
            String pathToFile = targetFolder + File.separator + fileName;
            String[] arr = fileName.split("_");
            affectedFile = new CustomFile(pathToFile,arr[1],arr[2],arr[3]);
        }
    }

}
