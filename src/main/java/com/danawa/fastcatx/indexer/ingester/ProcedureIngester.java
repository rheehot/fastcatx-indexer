package com.danawa.fastcatx.indexer.ingester;

import com.danawa.fastcatx.indexer.FileIngester;
import com.danawa.fastcatx.indexer.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ProcedureIngester extends FileIngester {

    private Type entryType;
    private Gson gson;
    private String dumpFormat;
    private boolean isRun;

    public ProcedureIngester(String filePath, String dumpFormat, String encoding, int bufferSize, int limitSize) {

        super(filePath, encoding, bufferSize, limitSize);
        logger.info("filePath : {}", filePath);
        gson = new Gson();
        entryType = new TypeToken<Map<String, Object>>() {}.getType();
        isRun = true;

        this.dumpFormat = dumpFormat;

    }


    @Override
    protected void initReader(BufferedReader reader) throws IOException {

    }

    @Override
    protected Map<String, Object> parse(BufferedReader reader) throws IOException {
        String line="";

        //종료 체크용 카운트
        int waitCount = 0;
        while (isRun) {
            try {
                line = reader.readLine();
                if(line != null) {
                    waitCount=0;
                    //TODO String dumpFormat,
                    Map<String, Object> record = new HashMap<>();
                    if(dumpFormat.equals("konan")){
                        record = gson.fromJson(Utils.convertKonanToNdJson(line), entryType);
                    }else if(dumpFormat.equals("ndjson")) {
                        record = gson.fromJson(line, entryType);
                    }
                    return record;
                }else{
                    //대기 상태가 연속으로 X회 이상이면 반복 중지
                    // FIXME rsync 전송이 갑자기 느려져서 20초간 한문서도 전송받지 못하거나
                    // 그외 어떤 이유(시스템 부하)로 잠시 멈출때는 전송완료와 구별하지 못함.
                    if(waitCount > 20) {
                        stop();
                    }
                    logger.info(("wait"));
                    waitCount++;
                    Thread.sleep(1000);
                }
            }catch(Exception e) {

                logger.error("parsing error : line= " + line, e);
            }
        }
        throw new IOException("EOF");
    }

    public void stop() {
        isRun =false;
    }

}
