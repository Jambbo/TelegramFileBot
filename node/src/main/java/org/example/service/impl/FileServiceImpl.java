package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.example.dao.AppDocumentDAO;
import org.example.dao.AppPhotoDAO;
import org.example.dao.BinaryContentDAO;
import org.example.entity.AppDocument;
import org.example.entity.AppPhoto;
import org.example.entity.BinaryContent;
import org.example.exceptions.UploadFileException;
import org.example.service.FileService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Service
@Log4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Value("${token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    private final AppDocumentDAO appDocumentDAO;
    private final BinaryContentDAO binaryContentDAO;
    private final AppPhotoDAO appPhotoDAO;
    @Override
    public AppDocument processDoc(Message telegramMessage) {
        Document telegramDoc = telegramMessage.getDocument();
        String fileId =telegramDoc.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if(response.getStatusCode()== HttpStatus.OK){
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc,persistentBinaryContent);
            return appDocumentDAO.save(transientAppDoc);
        }else{
            throw new UploadFileException("Bad response from telegram service: "+response);
        }
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        //TODO  Пока что обрабатываем только одно фото в сообщении
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(0);
        String fileId =telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if(response.getStatusCode()== HttpStatus.OK){
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppPhoto transientAppPhoto = buildTransientAppPhoto(telegramPhoto,persistentBinaryContent);
            return appPhotoDAO.save(transientAppPhoto);
        }else{
            throw new UploadFileException("Bad response from telegram service: "+response);
        }
    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentDAO.save(transientBinaryContent);
    }

    private static String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
            return AppPhoto.builder()
                    .binaryContent(persistentBinaryContent)
                    .fileSize(telegramPhoto.getFileSize())
                    .telegramFileId(telegramPhoto.getFileId())
                    .build();
    }

    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFileId(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                .build();
    }

    private ResponseEntity<String> getFilePath(String fileId) {
//RestTemplate позволяет сделать http запрос к телеграму
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<>(new HttpHeaders());
// в метод exchange подставляются: uri запроса, параметры которые следует подставить в этот запрос,
// тип возвращаемого значения, объект request'a и http метод
        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token, fileId
        );
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = fileStorageUri
                .replace("{token}",token)
                .replace("{filePath}",filePath);

        try(CloseableHttpClient httpClient = HttpClients.createDefault()){
            HttpGet httpGet = new HttpGet(fullUri);

            try(CloseableHttpResponse response = httpClient.execute(httpGet)){
                int statusCode =response.getStatusLine().getStatusCode();
                if(statusCode!=200){
                    throw new IOException("Failed to download file. Status code: "+statusCode);
                }
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                response.getEntity().writeTo(outputStream);
                return outputStream.toByteArray();
            }
        }catch (IOException e){
            throw new UploadFileException(fullUri,e);
        }

//        URL urlobj = null;
//        try{
//            urlobj = new URL(fullUri);
//        }catch (MalformedURLException e){
//            throw new UploadFileException(e);
//        }
//        //TODO подумать над оптимизайцией
//        try(InputStream is = urlobj.openStream()/*открываем стрим для скачивания контента*/){
//            return is.readAllBytes();
//        }catch (IOException e){
//            throw  new UploadFileException(urlobj.toExternalForm(),e);
//        }
    }
}
