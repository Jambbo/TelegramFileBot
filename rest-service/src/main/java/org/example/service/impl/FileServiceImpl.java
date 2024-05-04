package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.aspectj.util.FileUtil;
import org.example.dao.AppDocumentDAO;
import org.example.dao.AppPhotoDAO;
import org.example.entity.AppDocument;
import org.example.entity.AppPhoto;
import org.example.entity.BinaryContent;
import org.example.service.FileService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Log4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    @Override
    public AppDocument getDocument(String id) {
        //TODO добавить деширование хеш-строки
        var docId = Long.parseLong(id);
        return appDocumentDAO.findById(docId).orElseThrow(
                ()->new RuntimeException("Document with id: "+docId+" not found.")
        );
    }

    @Override
    public AppPhoto getPhoto(String id) {
        //TODO добавить деширование хеш-строки
        var photoId = Long.parseLong(id);
        return appPhotoDAO.findById(photoId).orElseThrow(
                ()->new RuntimeException("Photo with id: "+photoId+" not found.")
        );
    }
//этот метод предназначен для того чтобы массив байт из бд преобразовать в объект класса FileSystemResource,
// который можно отправить в теле ответа пользователю, и его бразуер скачает файл
    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try{
            //TODO добавить генерацию имени временного файла
            File temp = File.createTempFile("tempFile",".bin");
            temp.deleteOnExit();//при завершении работы приложения удалит этот временный файл из постоянной памяти компа
            FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());//записываем массив байт в темп
            return new FileSystemResource(temp);
        } catch (IOException e) {
            log.error(e);
            return null;
        }
    }
}
