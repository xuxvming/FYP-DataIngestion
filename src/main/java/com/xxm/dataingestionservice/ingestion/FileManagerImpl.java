package com.xxm.dataingestionservice.ingestion;

import com.xxm.dataingestionservice.controller.FileManager;
import com.xxm.dataingestionservice.controller.RequestSettings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FileManagerImpl implements FileManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileManagerImpl.class);

    private static final String FILE_METADATA = "result";

    @Value("${service.pool.address}")
    private String poolAddress;

    public CustomFile StringToFile(String content,Map fileParams){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMddHHmm");
        String timeStamp = simpleDateFormat.format(new Date());
        String filePath = poolAddress+fileParams.get(RequestSettings.SYMBOL.getField());
        String function = (String) fileParams.get(RequestSettings.TIME_INTERVAL.getField());
        String timeInterval = (String) fileParams.get(RequestSettings.FUNCTION.getField());
        String fileName = fileParams.get(RequestSettings.SYMBOL.getField())+"_"+ function+"_"+timeInterval+"_"+timeStamp;
        CustomFile symbolFile = new CustomFile (filePath+File.separator+fileName,function,timeInterval,timeStamp);
        try {
            FileUtils.writeStringToFile(symbolFile,content, Charset.defaultCharset());
        } catch (IOException e) {
            LOGGER.error("Error writing symbol file",e);
        }
        return symbolFile;
    }

    @Override
    public Map getFileInformation(String symbol, String date) {
        Map<String, List<HashMap<String, String>>> fileInfoMap = getFileInformation(symbol);
        List<HashMap<String, String>> fileInfoList = fileInfoMap.get(FILE_METADATA);
        for(Map fileInfo: fileInfoList){
            if (fileInfo.get("timestamp").equals(date)){
                return fileInfo;
            }
        }
        return new HashMap<String, String>();
    }

    @Override
    public Map<String, List<HashMap<String, String>>> getFileInformation(String symbol) {
        File symbolPool =  new File(poolAddress + File.separator+ symbol);
        Collection<File> files =  FileUtils.listFiles(symbolPool,null,true);

        List<HashMap<String,String>> fileList = new ArrayList<>();
        Map<String,List<HashMap<String, String>>> res = new HashMap<>();
        for (File file : files){
            HashMap<String,String> fileInfo = new HashMap<>();
            fileInfo.put("Lines",String.valueOf(getFileRecordsLength(file)));
            fileInfo.put("Location",file.getAbsolutePath());
            String fileName = file.getName();
            String[] fileNameComponents = fileName.split("_");
            fileInfo.put("name",file.getName());
            fileInfo.put(RequestSettings.SYMBOL.getField(),fileNameComponents[0]);
            fileInfo.put(RequestSettings.FUNCTION.getField(),fileNameComponents[fileNameComponents.length-3]);
            fileInfo.put(RequestSettings.TIME_INTERVAL.getField(),fileNameComponents[fileNameComponents.length-2]);
            fileInfo.put("timeStamp",fileNameComponents[fileNameComponents.length-1]);
            fileList.add(fileInfo);
        }
        res.put(FILE_METADATA,fileList);
        return res;
    }

    private List<String> getFileRecords(File file){
        List<String> records = new ArrayList<>();
        try(LineIterator lineIterator = FileUtils.lineIterator(file)) {
            while(lineIterator.hasNext()){
                records.add(lineIterator.next());
            }
        } catch (IOException e) {
            LOGGER.error("Error getting line number",e);
        }
        return records;
    }

    public List<HashMap<String,String>> getFileRecordsAsMaps(CustomFile file){
        List<String> fileRecords = getFileRecords(new File(file.getLocation()));
        List<String> headers = Arrays.asList(fileRecords.get(0).split(","));
        List<HashMap<String,String>> res = new LinkedList<>();
        for (int i = fileRecords.size()-1; i>0;i--){
            String[] line = fileRecords.get(i).split(",");
            HashMap<String, String> hm = new HashMap<>();
            for (int j = 0; j < line.length; j++){
                hm.put(headers.get(j),line[j]);
            }
            res.add(hm);
        }
        return res;
    }

    private int getFileRecordsLength(File file){
        List<String> fileRecords = getFileRecords(file);
        return fileRecords.size()-1;
    }

}
